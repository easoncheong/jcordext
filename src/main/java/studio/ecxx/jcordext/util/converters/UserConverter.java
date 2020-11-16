package studio.ecxx.jcordext.util.converters;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.user.User;

import java.util.Optional;

public class UserConverter extends DiscordConverter<User> {

    public UserConverter(DiscordApi api) {this.api = api;}

    /**
     * Converts a string into a user in the bot's cache, if possible. Else it returns null.
     * The searching strategy looks for a mention or an id, followed by a name, then by discriminant.
     * If this command was invoked in a private channel, the command searches its whole database. Else it searches just the server.
     * @param context The channel the function was sent in, for context.
     * @param input The input string.
     * @return the user.
     */
    @Override
    public User convert(Channel context, String input) {
        String idString = input.replaceAll("[\\\\<>@#&!]", "");
        User user = api.getUserById(idString).join();
        if (user!=null) return user;


        Optional<User> otherUser = api.getCachedUserByDiscriminatedName(input);
        return otherUser.orElse(null);
    }
}
