package studio.ecxx.jcordext.util.completables;

import org.javacord.api.entity.message.Message;

/**
 * This is an interface for checking CompletableMessages.
 */
public interface MessageCheck {

    /**
     * Check if the message meets the needs of the CompletableMessage. Returns {@code true} if the message meets the requirements.
     * @param message The message to check.
     */
    boolean check(Message message);

}
