package me.dmmax.patterns.history;

import me.dmmax.patterns.history.command.Command;
import me.dmmax.patterns.history.listeners.AddUserListener;
import me.dmmax.patterns.history.listeners.DeleteUserListener;

import java.util.ArrayList;
import java.util.List;

public class ChangesManager {

    // Service
    private final CommandsExecutor commandsExecutor = new CommandsExecutor();

    // Listeners
    private final List<AddUserListener> addUserListeners = new ArrayList<>();
    private final List<DeleteUserListener> deleteUserListeners = new ArrayList<>();

    // Data
    private final ChangesState state;

    public ChangesManager(ChangesState state) {
        this.state = state;
    }

    public void executeCommand(Command command) {
        commandsExecutor.execute(command);
    }

    // HISTORY

    public void undo() {
        commandsExecutor.undo();
    }

    public boolean canUndo() {
        return commandsExecutor.canUndo();
    }

    public void redo() {
        commandsExecutor.redo();
    }

    public boolean canRedo() {
        return commandsExecutor.canRedo();
    }

    // ADD USER

    public void addUser(String user) {
        state.addUser(user);
        notifyAddUserListeners(user);
    }

    public void addListener(AddUserListener listener) {
        addUserListeners.add(listener);
    }

    private void notifyAddUserListeners(String user) {
        addUserListeners.forEach(listener -> listener.onAddedUser(user));
    }

    // DELETE USER

    public void deleteUser(String user) {
        state.deleteUser(user);
        notifyDeleteUserListeners(user);
    }

    public void addListener(DeleteUserListener listener) {
        deleteUserListeners.add(listener);
    }

    private void notifyDeleteUserListeners(String user) {
        deleteUserListeners.forEach(listener -> listener.onRemovedUser(user));
    }
}
