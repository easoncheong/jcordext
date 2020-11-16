package studio.ecxx.jcordext.commands;

/**
 * Exception for usage when an extension name does not fulfil the requirements.
 */
public class IllegalExtensionNameException extends Exception {

    public IllegalExtensionNameException(String message) {
        super(message);
    }

}
