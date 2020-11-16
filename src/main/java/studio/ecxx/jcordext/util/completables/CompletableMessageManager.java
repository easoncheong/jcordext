package studio.ecxx.jcordext.util.completables;

import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.ArrayList;

public class CompletableMessageManager {

    private DiscordApi api;
    private final ArrayList<CompletableMessage> completables = new ArrayList<>(0);

    /**
     * Attach a CompletableMessageManager to an Api.
     * @param api DiscordApi to attach onto.
     */
    public CompletableMessageManager(DiscordApi api) {
        api.addMessageCreateListener(this::onMessage);
    }

    /**
     * Attach a completableMessage to the Manager, so it can be invoked.
     * @param completable The completableMessage
     */
    public void attach(CompletableMessage completable) {
        completables.add(completable);
    }

    private void onMessage(MessageCreateEvent event) {
        ArrayList<CompletableMessage> toRemove = new ArrayList<>(0);
        for (int i = 0; i < completables.size(); i++) {
            CompletableMessage completable = completables.get(i);
            if (completable.complete(event.getMessage())) toRemove.add(completable);
        }
        for (int i = 0; i < toRemove.size(); i++) {
            completables.remove(toRemove.get(i));
        }
    }

}
