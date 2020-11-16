package studio.ecxx.jcordext.util.converters;

import org.javacord.api.DiscordApi;

public abstract class DiscordConverter<T> implements Converter<T> {

    /**
     * This class is a base class for RoleConverter, TextChannelConverter and UserConverter.
     */
    protected DiscordApi api;

}
