package studio.ecxx.jcordext.util.mentions;

import org.javacord.api.entity.channel.Channel;

public class ChannelMentionConverter implements MentionConverter<Channel> {

    @Override
    public String convert(Channel input) {
        return "<#" + input.getId() + ">";
    }

}
