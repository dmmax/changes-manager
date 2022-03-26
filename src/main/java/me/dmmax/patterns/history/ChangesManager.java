package me.dmmax.patterns.history;

import com.google.common.eventbus.EventBus;
import me.dmmax.patterns.history.command.UndoCommand;
import me.dmmax.patterns.history.event.ChangesEvent;

public class ChangesManager {

    private final HistoryProvider historyProvider = new HistoryProvider();
    private final EventBus eventBus = new EventBus(getClass().getSimpleName());

    public ChangesManager(ChangesState state) {
        registerListener(state);
    }

    /**
     * Use this method when you want to keep the command in history and use it later with undo/redo actions
     *
     * @param command – undo command
     */
    public void executeCommand(UndoCommand command) {
        System.out.println("Executing command and add it to the history");
        command.execute();
        historyProvider.addCommand(command);
    }

    /**
     * Use this method when you want to send an update to listeners
     *
     * @param event – changes event
     */
    public void post(ChangesEvent event) {
        eventBus.post(event);
    }

    public void registerListener(Object listener) {
        eventBus.register(listener);
    }

    public HistoryProvider historyProvider() {
        return historyProvider;
    }
}
