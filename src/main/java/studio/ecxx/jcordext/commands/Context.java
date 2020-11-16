package studio.ecxx.jcordext.commands;


import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import studio.ecxx.jcordext.executor.Bot;
import studio.ecxx.jcordext.util.completables.MessageCheck;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * The invocation context of a command.
 */
public class Context {

    public Context(Bot executor, DiscordApi api, MessageCreateEvent event, String[] args) {

        TextChannel tchannel = null;
        this.api = api;
        this.event = event;
        this.message = event.getMessage();
        this.bot = executor;
        this.args = args;
        this.author = message.getAuthor();
        Optional<TextChannel> channel = message.getChannel().asTextChannel();
        if (channel.isPresent()) {tchannel = channel.get();}
        this.channel = tchannel;

    }

    /**
     * The DiscordApi that received the command.
     */
    private final DiscordApi api;

    /**
     * The MessageCreateEvent that contains the message.
     */
    private final MessageCreateEvent event;

    /**
     * The Message.
     */
    private final Message message;

    /**
     * The Text Channel the message was sent in.
     */
    private final TextChannel channel;

    /**
     * The Bot associated with the command.
     */
    private final Bot bot;

    /**
     * The arguments used (in string form)
     */
    private final String[] args;

    /**
     * The author of the message.
     */
    private final MessageAuthor author;

    /**
     * Send a messagebuilder to the channel of the message.
     * @param builder The messagebuilder.
     */
    public CompletableFuture<Message> send(MessageBuilder builder) {
        return builder.send(channel);
    }

    /**
     * Send a message (string form) to the channel of the message.
     * @param content The message.
     */
    public CompletableFuture<Message> send(String content) {
        return channel.sendMessage(content);
    }

    /**
     * A method to retrieve a message that meets the requirements of the check.
     * @param check The MessageCheck implemented to verify the message.
     * @return The message received.
     */
    public Message awaitMessage(MessageCheck check) {
        return bot.awaitMessage(check);
    }

    /**
     * Obtains the DiscordApi object that received the command.
     * @return the DiscordApi object that received the command.
     */
    public DiscordApi getApi() {
        return api;
    }

    /**
     * Obtains the MessageCreateEvent object that triggered the command.
     * @return the MessageCreateEvent object that triggered the command.
     */
    public MessageCreateEvent getEvent() {
        return event;
    }

    /**
     * Obtains the Message that was sent.
     * @return the Message that was sent.
     */
    public Message getMessage() {
        return message;
    }

    /**
     * Obtains the Channel that the message was sent in.
     * @return the Channel that the message was sent in..
     */
    public TextChannel getChannel() {
        return channel;
    }

    /**
     * Obtains the Bot object that handled the command.
     * @return the Bot object that handled the command.
     */
    public Bot getBot() {
        return bot;
    }

    /**
     * Obtains the arguments used for the command.
     * @return the arguments used for the command.
     */
    public String[] getArgs() {
        return args;
    }

    /**
     * Obtains the author of the message.
     * @return the author of the message.
     */
    public MessageAuthor getAuthor() {
        return author;
    }
}
