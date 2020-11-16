package studio.ecxx.jcordext.commands;

import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import studio.ecxx.jcordext.util.mentions.*;

/**
 * An interface for adding commands.
 */
public interface Extension {

    /**
     * Converts a channel to a mention string.
     * @param channel the channel.
     * @return the mention string for the channel.
     */
    default String mention(Channel channel) {
        return new ChannelMentionConverter().convert(channel);
    }

    /**
     * Converts a role into a mention string.
     * @param role the role.
     * @return the mention string for the role.
     */
    default String mention(Role role) {
        return new RoleMentionConverter().convert(role);
    }

    /**
     * Converts a user into a mention string.
     * @param user the user.
     * @return the mention string for the user.
     */
    default String mention(User user) {
        return new UserMentionConverter().convert(user);
    }


}
