package studio.ecxx.jcordext.util.mentions;

import org.javacord.api.entity.user.User;

public class UserMentionConverter implements MentionConverter<User> {

    @Override
    public String convert(User input) {
        return input.getMentionTag();
    }

}
