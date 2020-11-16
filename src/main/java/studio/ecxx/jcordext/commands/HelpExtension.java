package studio.ecxx.jcordext.commands;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageDecoration;

import java.util.ArrayList;

/**
 * The default help extension for a bot.
 */
public class HelpExtension implements Extension {

    DiscordApi api;

    public HelpExtension(DiscordApi api) {
        this.api = api;
    }

    /**
     * Help Extension. This is a default extension that comes shipped with the package.
     * @param ctx Context. standard in a studio.ecxx.jcordext.command
     */
    @Command(name="help", description="Displays this message.")
    public void help(Context ctx) {

        MessageBuilder builder = new MessageBuilder();
        ArrayList<JCommand> commands = ctx.getBot().getCommandList();
        commands.sort(new CommandSorter());

        String currentHeader = commands.get(0).getExtensionName();
        String currentGroup = "";

        currentGroup += "Command " + ctx.getBot().getPrefix()+commands.get(0).getInvocator() + "\n";
        currentGroup += "Usage: " + commands.get(0).getAnnotation().usage() + "\n";
        currentGroup += "Description: " + commands.get(0).getAnnotation().description() + "\n\n";

        for (int i = 1; i < commands.size(); i++) {

            JCommand command = commands.get(i);

            if (!command.getAnnotation().shownInHelpCommand()) return;

            String header = command.getExtensionName();
            if (!header.equals(currentHeader)) {
                builder = builder.append("\nExtension " + currentHeader + ":", MessageDecoration.BOLD)
                        .appendCode("", currentGroup);
                currentGroup = "";
                currentHeader = header;
            }
            currentGroup += "Command " + ctx.getBot().getPrefix()+command.getInvocator() + "\n";
            currentGroup += "Usage: " + command.getAnnotation().usage() + "\n";
            currentGroup += "Description: " + command.getAnnotation().description() + "\n\n";


        }
        builder = builder.append("\nExtension " + currentHeader + ":", MessageDecoration.BOLD)
                .appendCode("", currentGroup);

        builder.send(ctx.getChannel());

    }

}
