package me.dmmax.patterns.history;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import me.dmmax.patterns.history.command.Command;
import me.dmmax.patterns.history.event.AddUserEvent;
import me.dmmax.patterns.history.event.ChangesEvent;
import me.dmmax.patterns.history.event.DeleteUserEvent;

public class ChangesManager {

    // Service
    private final HistoryProvider historyProvider = new HistoryProvider();

    // Listeners
    private final EventBus eventBus = new EventBus("Changes Manager");

    // Data
    private final ChangesState state;

    public ChangesManager(ChangesState state) {
        this.state = state;
        initInternalListeners();
    }

    private void initInternalListeners() {
        registerListener(new ChangesListener() {
            @Subscribe
            public void onAddedUser(AddUserEvent event) {
                state.addUser(event.user());
            }
        });
        registerListener(new ChangesListener() {
            @Subscribe
            public void onDeletedUser(DeleteUserEvent event) {
                state.deleteUser(event.user());
            }
        });
    }

    public void executeCommand(Command command) {
        System.out.println("Executing command");
        command.execute();
        historyProvider.addCommand(command);
    }

    public void post(ChangesEvent event) {
        eventBus.post(event);
    }

    public void registerListener(ChangesListener listener) {
        eventBus.register(listener);
    }

    public HistoryProvider historyProvider() {
        return historyProvider;
    }
}
