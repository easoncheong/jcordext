package studio.ecxx.jcordext.tasks;

import org.javacord.api.DiscordApi;
import studio.ecxx.jcordext.commands.Extension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Framework to execute tasks. This implements runnable and is called automatically.
 */
public class TaskExecutor implements Runnable {

    private Method method;
    private Extension extension;
    private DiscordApi api;
    private Task task;

    public TaskExecutor(DiscordApi api) {
        this.api = api;
    }

    /**
     * Submit a task to the framework.
     * @param extension The Extension the method is in.
     * @param method The method to execute.
     * @param task The task annotation.
     */
    public void submitTask(Extension extension, Method method, Task task) {
        this.extension = extension;
        this.method = method;
        this.task = task;
    }

    @Override
    public void run() {
        for (int i = 0; i < task.repeat(); i++) {
            try {
                Thread.sleep(task.timer());
            } catch (InterruptedException e) {
            }
            try {
                method.invoke(extension, api);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

}
