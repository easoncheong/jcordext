package studio.ecxx.jcordext.util.converters;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.TextChannel;

import java.util.Collection;
import java.util.Optional;

public class TextChannelConverter extends DiscordConverter<TextChannel> {

    public TextChannelConverter(DiscordApi api) {this.api = api;}

    /**
     * Converts a string into a text channel, if possible. Else it returns null.
     * The searching strategy looks for a mention or an id, followed by a name.
     * If this command was invoked in a private channel, the command searches its whole database. Else it searches just the server.
     * @param context The channel the function was sent in, for context.
     * @param input The input string.
     * @return the text channel.
     */
    @Override
    public TextChannel convert(Channel context, String input) {

        String idString = input.replaceAll("[\\\\<>@#&!]", "");
        Optional<TextChannel> channel = api.getTextChannelById(idString);
        if (channel.isPresent()) return channel.get();


        Collection<TextChannel> channels = api.getTextChannelsByName(input);
        if (
                context.asServerChannel().isPresent()
        ) {
            if (!channels.isEmpty()) {
                for (TextChannel t : channels) {
                    for (Channel ch : context.asServerChannel().get().getServer().getChannels()) {
                        if (ch.getId()==context.getId()) return t;
                    }
                }
            }

        } else {
            if (!channels.isEmpty()) {
                return (TextChannel) channels.toArray()[0];
            }
        }

        return null;
    }

}
