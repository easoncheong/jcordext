package studio.ecxx.jcordext.commands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class JCommand {

    private final Command annotation;
    private final Method executor;
    private final Extension extension;
    private final String invocator;
    private final String extensionName;

    public JCommand(Command annotation, Method executor, Extension extension, String invocator, String extensionName) {
        this.annotation = annotation;
        this.executor = executor;
        this.extension = extension;
        this.invocator = invocator;
        this.extensionName = extensionName;
    }

    public Command getAnnotation() {
        return annotation;
    }

    public Method getExecutor() {
        return executor;
    }

    public Extension getExtension() {
        return extension;
    }

    public String getInvocator() { return invocator; }

    public String getExtensionName() { return extensionName; }

    /**
     * Invokes command using args.
     * @param args the arguments used.
     */
    public void invoke(Object[] args) {
        try {
            executor.invoke(extension, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
