package studio.ecxx.jcordext.util.mentions;

public interface MentionConverter<T> {

    String convert(T input);

}
