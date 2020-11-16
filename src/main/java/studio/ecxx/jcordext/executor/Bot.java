package studio.ecxx.jcordext.executor;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.channel.VoiceChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import studio.ecxx.jcordext.commands.*;
import studio.ecxx.jcordext.tasks.*;
import studio.ecxx.jcordext.util.completables.*;
import studio.ecxx.jcordext.util.converters.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Bot {

    /**
     * The Discord Api used in the bot.
     * This is listed in protected privacy level to allow for inheritance of the functionality of the Api.
     */
    protected DiscordApi api;

    /**
     * The Command List, in Hashmap form, for relative efficiency compared to the standard technique of iterating through a list.
     * Searching for a command in a HashMap is done in O(1) time, rather than the usual O(n) in a list or O(log n) in a TreeMap.
     */
    protected HashMap<String, JCommand> commands = new HashMap<>();

    /**
     * We use this ArrayList, seeing as iterating through a HashMap is not possible.
     */
    protected ArrayList<JCommand> commandList = new ArrayList<>();

    /**
     * The prefix of the bot. The prefix is used to invoke commands, e.g., a command with name "ping" and prefix "!" will be invoked with "!ping".
     */
    protected String prefix;

    /**
     * The Manager for {@code awaitMessage} calls.
     */
    protected CompletableMessageManager awaitmanager;

    /**
     * Primary constructor for a Bot. Provide the token, which is available on Discord's Developer Portal, and the prefix of the bot.
     * @param prefix The prefix of the bot. All commands will use this prefix.
     * @param token the bot token.
     */
    public Bot(String prefix, String token) {

        this(prefix, new DiscordApiBuilder()
                .setToken(token)
                .login().join());

    }

    /**
     * This is an additional constructor provided as a utility for further control of the api.
     * In this constructor, instead of the Bot generating the API, the API is created by the user then the reference to the API is given to the bot.
     * @param prefix The prefix of the bot. All commands will use this prefix.
     * @param api The Discord Api.
     */
    public Bot(String prefix, DiscordApi api) {
        this(prefix, api, new HelpExtension(api));
    }

    /**
     * This constructor provides customization on the structure of the help extension, by allowing a user to register a customised help extension with their own specific usages.
     * This is the base constructor used for initialisation.
     * @param prefix The prefix of the bot. All commands will use this prefix.
     * @param api The Discord Api.
     * @param helpExtension the help extension. This extension should contain a command "help".
     */
    public Bot(String prefix, DiscordApi api, Extension helpExtension) {
        this.api = api;
        this.prefix = prefix;
        api.addMessageCreateListener(this::onMessage);
        this.registerSpecial(helpExtension, "&help");
        this.awaitmanager = new CompletableMessageManager(api);
    }

    /**
     * This command is used to register a special constructor. A special constructor is allowed to use the blocked names:
     * {@code &help}
     * @param extension The Extension to register.
     */
    private void registerSpecial(Extension extension, String extensionName) {
        for (Method method : extension.getClass().getMethods()) {
            addCommand(method, extension, extensionName);
            addTask(method, extension);
        }
    }

    /**
     * Registers commands into the command framework / list in the bot.
     * @param extension The Extension that is being added to the Executor
     * @param extensionName The name of the extension that is being added.
     * @throws IllegalExtensionNameException This exception is thrown if the extension name is {@code &help}.
     */
    public void registerExtension(Extension extension, String extensionName) throws IllegalExtensionNameException {
        if (extensionName.equals("&help")) {
            throw new IllegalExtensionNameException("&help is a default extension name and cannot be used.");
        }
        for (Method method : extension.getClass().getMethods()) {
            addCommand(method, extension, extensionName);
            addTask(method, extension);
        }
    }

    /**
     * Registers commands into the command framework / list in the bot.
     * This version of the extentsion register does not require an extension name to be entered, and instead uses the name of the class.
     * @param extension The Extension that is being added to the Executor
     */
    public void registerExtension(Extension extension) throws IllegalExtensionNameException {
        String extensionName = extension.getClass().getSimpleName();
        registerExtension(extension, extensionName);
    }

    /**
     * Handles a new message
     * @param event the MessageCreateEvent
     */
    protected void onMessage(MessageCreateEvent event) {
        parseCommand(event);
    }

    /**
     * checks if a command is in the message, and executes it if necessary
     * @param event The MessageCreateEvent
     */
    protected void parseCommand(MessageCreateEvent event) {
        Message message = event.getMessage();
        String content = message.getContent();

        if (message.getUserAuthor().map(User::isYourself).orElse(false)) {
            return;
        } // do not respond to yourself

        if (!content.startsWith(prefix)) {
            return;
        } // check if this is a command

        // strip away prefix
        content = content.substring(prefix.length());

        // get command
        String commandString = content.split("[\\s]")[0];
        JCommand command = commands.get(commandString.toLowerCase());
        if (command == null) {
            return;
        } // no command found, no problems

        // check command permissions
        Command commandAnnotation = command.getAnnotation();
        if (message.getPrivateChannel().isPresent() && !commandAnnotation.enablePrivateMessages()) {
            return;
        }
        if ((!message.getPrivateChannel().isPresent()) && !commandAnnotation.enableServerMessages()) {
            return;
        }

        // submit invocation
        if (commandAnnotation.async()) {
            api.getThreadPool().getExecutorService().submit(() -> invokeCommand(command, event));
        } else {
            invokeCommand(command, event);
        }
    }

    /**
     * Verifies if a method is suitable for usage as a command. A command must:
     * - Have the first param be a Context argument, and have all other parameters be of one of the following types:
     * Role, Channel, User or String.
     * @param method the method to verify as a command
     * @throws MalformedCommandException when the method is invalid
     */
    protected void verifyCommand(Method method) throws MalformedCommandException {
        Class<?>[] params = method.getParameterTypes();

        if (!params[0].equals(Context.class)) {
            throw new MalformedCommandException("First parameter of a command must be of class Context.");
        }

        for (int i = 1; i < params.length; i++) {
            if (params[i].equals(String.class)) continue;
            if (params[i].equals(Role.class)) continue;
            if (params[i].equals(TextChannel.class)) continue;
            if (params[i].equals(Channel.class)) continue;
            if (params[i].equals(VoiceChannel.class)) continue;
            if (params[i].equals(User.class)) continue;
            throw new MalformedCommandException("Parameter type not permitted: " +
                    "parameter type must be in {Role, TextChannel, VoiceChannel, Channel, User, String}.");
        }
    }

    /**
     * Add command, if no command found, return without output.
     * @param method the method to verify/add
     * @param extension the extension that the method is in
     */
    protected void addCommand(Method method, Extension extension, String extensionName) {
        String name;
        Command annotation = method.getAnnotation(Command.class);
        if (annotation == null) {
            return;
        }
        try {
            verifyCommand(method);
        } catch (MalformedCommandException e) {
            e.printStackTrace();
            System.out.println("Due to an error (as outlined in the exception above, the system was unable to register the command.");
            return;
        }

        if (annotation.name().equals("none")) {
            name = method.getName();
        } else {
            name = annotation.name();
        }

        if (!commands.containsKey(name)) {
            JCommand jcommand = new JCommand(annotation, method, extension, name.toLowerCase(), extensionName);

            commandList.add(jcommand);
            commands.put(name.toLowerCase(), jcommand);
        } else {
            System.out.println("Command " + name + " not registered: name already taken");
        }
    }

    /**
     * Assembles a Object[] of args to invoke the command by type checking.
     * @param command the JCommand to invoke
     * @param event the MessageCreateEvent that triggered the command
     */
    protected void invokeCommand(JCommand command, MessageCreateEvent event) {

        String content = event.getMessageContent();
        content = content.substring(prefix.length());

        if (command.getAnnotation().runInNSFWOnly()
                && !event.getChannel().asServerTextChannel().map(ServerTextChannel::isNsfw).orElse(true)) {
            return;
        }

        Message message = event.getMessage();

        boolean breaks = false;

        if (message.getServerTextChannel().isPresent()) {
            for (int i = 0; i < command.getAnnotation().discordPermissions().length; i++) {
                if (!message.getServerTextChannel().get().hasPermission(
                        message.getAuthor().asUser().get(),
                        command.getAnnotation().discordPermissions()[i]
                )) breaks = true;
            }
        }

        if (breaks && (!command.getAnnotation().botOwnerOverride() || !message.getAuthor().isBotOwner())) return;

        ArrayList<String> matchList = new ArrayList<String>();
        Pattern regex = Pattern.compile("([^\"\\\\]*(?:\\\\.[^\"\\\\]*)*)\"|'([^'\\\\]*(?:\\\\.[^'\\\\]*)*)'|[^\\s]+");
        Matcher regexMatcher = regex.matcher(content);
        while (regexMatcher.find()) {
            if (regexMatcher.group(1) != null) {
                // Add double-quoted string without the quotes
                matchList.add(regexMatcher.group(1));
            } else if (regexMatcher.group(2) != null) {
                // Add single-quoted string without the quotes
                matchList.add(regexMatcher.group(2));
            } else {
                // Add unquoted word
                matchList.add(regexMatcher.group());
            }
        }

        String[] userArguments = matchList.toArray(new String[0]);
        Class<?>[] params = command.getExecutor().getParameterTypes();
        Object[] args = new Object[params.length];

        Context invocationContext = new Context(this, api, event, userArguments);
        args[0] = invocationContext;

        String[] arguments = new String[params.length];

        for (int i = 0; i < params.length; i++) {
            if (i>=userArguments.length) {
                arguments[i] = "";
            } else {
                arguments[i] = userArguments[i];
            }
        }

        for (int i = 1; i < params.length; i++) {
            if (params[i].equals(String.class)) {
                args[i] = arguments[i];
            }
            if (params[i].equals(Role.class)) {
                args[i] = new RoleConverter(api).convert(event.getChannel(), arguments[i]);
            }
            if (params[i].equals(Channel.class)) {
                args[i] = new ChannelConverter(api).convert(event.getChannel(), arguments[i]);
            }
            if (params[i].equals(TextChannel.class)) {
                args[i] = new TextChannelConverter(api).convert(event.getChannel(), arguments[i]);
            }
            if (params[i].equals(User.class)) {
                args[i] = new UserConverter(api).convert(event.getChannel(), arguments[i]);
            }
        }

        command.invoke(args);

    }

    /**
     * Verifies if a method is suitable for usage as a task. A task must have 1 parameter of type api.
     * @param method the method being verified
     * @throws MalformedTaskException thrown if the task does not have the correct param format
     */
    protected void verifyTask(Method method) throws MalformedTaskException {
        Class<?>[] params = method.getParameterTypes();

        if (params.length!=1) {
            throw new MalformedTaskException("Task must have 1 parameter of type api.}");
        }
        if (!params[0].equals(DiscordApi.class)) {
            throw new MalformedTaskException("Task must have 1 parameter of type api.}");
        }
    }

    /**
     * Add task, if no task found, continue without error
     * @param method method
     * @param extension extension
     */
    protected void addTask(Method method, Extension extension) {
        String name;
        Task annotation = method.getAnnotation(Task.class);
        if (annotation == null) {
            return;
        }

        try {
            verifyTask(method);
        } catch (MalformedTaskException e) {
            e.printStackTrace();
            System.out.println("Due to an error in the command the system was unable to register the task.");
            return;
        }

        TaskExecutor taskExecutor = new TaskExecutor(api);
        taskExecutor.submitTask(extension, method, annotation);

        new Thread(taskExecutor).start();

    }

    /**
     * Return the command list of the bot. Note that this command list is the original.
     * Tampering with it may cause errors in help command generation, and is strongly discouraged.
     * @return The command list of this Bot.
     */
    public ArrayList<JCommand> getCommandList() {
        return commandList;
    }

    /**
     * Return the prefix of the bot.
     * @return the prefix of the bot.
     */
    public String getPrefix() {return prefix;}

    /**
     * Waits for a message that meets the requirements of the checker's check method.
     * @param checker the checking utility.
     * @return the first message that matches.
     */
    public Message awaitMessage(MessageCheck checker) {
        CompletableMessage waiter = new CompletableMessage(checker, this.api);
        awaitmanager.attach(waiter);
        return waiter.get();
    }

    /**
     * Returns the discord api.
     * @return the discord api object.
     */
    public DiscordApi getApi() {return api;}

}
