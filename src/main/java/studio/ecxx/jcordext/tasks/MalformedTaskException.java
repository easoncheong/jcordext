package studio.ecxx.jcordext.tasks;

/**
 * MalformedTaskException is thrown when a Malformed Task is registered / attempts to be registered.
 */
public class MalformedTaskException extends Exception {

    public MalformedTaskException(String message) {
        super(message);
    }
}

