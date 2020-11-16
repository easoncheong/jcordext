package studio.ecxx.jcordext.util.converters;

import org.javacord.api.entity.channel.Channel;

public interface Converter<T> {

     /**
      * An interface to convert an input parameter into a different type.
      * @param context The channel the function was sent in, for context.
      * @param input The input string.
      * @return the converted parameter.
      */
     T convert(Channel context, String input);

}
