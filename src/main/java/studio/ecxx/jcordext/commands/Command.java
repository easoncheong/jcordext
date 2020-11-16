package studio.ecxx.jcordext.commands;

import org.javacord.api.entity.permission.PermissionType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A command annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    /**
     * Each command in a Bot Framework should be associated with a unique name.
     * The name of the command is defined in this parameter.
     * If the name of the command is left unchanged (i.e. it is left as none at runtime), the command will be named with the name of the method.
     * @return The name of the command.
     */
    String name() default "none";

    /**
     * Determines if the command should be invocable in a private message.
     * @return If this command will be run in a private message.
     */
    boolean enablePrivateMessages() default true;

    /**
     * Determines if the command should be invocable in a server message.
     * @return If this command will be run in a server message.
     */
    boolean enableServerMessages() default true;

    /**
     * Gets description of the command.
     * @return description of the command. this is used to construct the help command.
     */
    String description() default "none";

    /**
     * Gets the format of the command, e.g. !ban 'user' 'message'.
     * @return the format of the command. this is used to construct the help command.
     */
    String usage() default "No format provided.";

    /**
     * Get whether the the permission is to be run in a separate thread.
     * This is strongly recommended if the command requires a User Mention, as user mentions are generally a slow process.
     * Defaults to {@code true}.
     * @return whether the command should be run in a separate thread.
     */
    boolean async() default true;

    /**
     * Get whether the command is to be only permitted in an NSFW channel only.
     * @return whether the command is to be only permitted in an NSFW channel only.
     */
    boolean runInNSFWOnly() default false;

    /**
     * Get whether the command is to be shown in a help command.
     * @return whether the command is to be shown in a help command.
     */
    boolean shownInHelpCommand() default true;

    /**
     * Allow the owner of the bot to overwrite permissions, if necessary. Defaults to {@code false}.
     * @return if the owner is allowed to overwrite permissions (and run the command anyways).
     */
    boolean botOwnerOverride() default true;

    /**
     * List all required permissions for this command to successfully run.
     * @return all required permissions for this command to successfully run.
     */
    PermissionType[] discordPermissions() default {};

}
