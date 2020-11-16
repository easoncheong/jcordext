package studio.ecxx.jcordext.util.converters;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.permission.Role;

import java.util.Collection;
import java.util.Optional;

public class RoleConverter extends DiscordConverter<Role> {

    public RoleConverter(DiscordApi api) {this.api = api;}

    /**
     * Converts a string into a role, if possible. Else it returns null.
     * The searching strategy looks for a mention or an id, followed by a name.
     * If this command was invoked in a private channel, the command searches its whole database. Else it searches just the server.
     * @param context The channel the function was sent in, for context.
     * @param input The input string.
     * @return the role.
     */
    @Override
    public Role convert(Channel context, String input) {

        String idString = input.replaceAll("[\\\\<>@#&!]", "");
        Optional<Role> role = api.getRoleById(idString);
        if (role.isPresent()) return role.get();


        Collection<Role> roles = api.getRolesByName(input);
        if (
                context.asServerChannel().isPresent()
        ) {
            if (!roles.isEmpty()) {
                for (Role r : roles) {
                    for (Channel ch : context.asServerChannel().get().getServer().getChannels()) {
                        if (ch.getId()==context.getId()) return r;
                    }
                }
            }

        } else {
            if (!roles.isEmpty()) {
                return (Role) roles.toArray()[0];
            }
        }

        return null;
    }

}
