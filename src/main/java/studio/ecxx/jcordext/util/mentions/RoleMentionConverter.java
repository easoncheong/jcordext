package studio.ecxx.jcordext.util.mentions;

import org.javacord.api.entity.permission.Role;

public class RoleMentionConverter implements MentionConverter<Role> {

    @Override
    public String convert(Role input) {
        return input.getMentionTag();
    }
}
