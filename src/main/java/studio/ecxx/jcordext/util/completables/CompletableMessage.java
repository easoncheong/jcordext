package studio.ecxx.jcordext.util.completables;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.Message;

import java.util.concurrent.CompletableFuture;

/**
 * The CompletableMessage class is designed so bots can easily wait for a reply from the user.
 */
public class CompletableMessage {

    protected MessageCheck checker;
    protected CompletableFuture<Message> future = new CompletableFuture<>();
    protected DiscordApi api;

    /**
     * Constructor for a CompletableMessage. This will however, not add it to any database so it will not work.
     * @param checker The Message Check.
     * @param api The Discord Api.
     */
    public CompletableMessage(MessageCheck checker, DiscordApi api) {
        this.checker = checker; this.api = api;
    }

    /**
     * Verify if the message meets requirements. If so, complete the future. This will activate the future.join() line to continue execution.
     * @param message The message to check.
     * @return If the checker is complete.
     */
    public boolean complete(Message message) {
        if (checker.check(message)) {
            future.complete(message);
            return true;
        }
        return false;
    }

    public Message get() {
        return future.join();
    }

}
