package studio.ecxx.jcordext.commands;

/**
 * MalformedCommandException is thrown when a Malformed Command is registered / attempts to be registered.
 */
public class MalformedCommandException extends Exception {

    public MalformedCommandException(String message) {
        super(message);
    }
}
