package studio.ecxx.jcordext.tasks;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.RUNTIME)
public @interface Task {

    /**
     * The time between executions of the task.
     */
    int timer() default 10000;

    /**
     * The number of times this is repeated. 1e9 is set as default, so for an (nearly) infinite loop,
     * just don't touch this parameter.
     */
    long repeat() default (int) 1e9;

}
