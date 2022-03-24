package me.dmmax.patterns.history;

import me.dmmax.patterns.history.command.AddUserCommand;
import me.dmmax.patterns.history.command.DeleteUserCommand;
import me.dmmax.patterns.history.listeners.AddUserListener;
import me.dmmax.patterns.history.listeners.DeleteUserListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChangesManagerTest {

    private final ChangesState changesState = new ChangesState();
    private final ChangesManager changesManager = new ChangesManager(changesState);

    private int addUserEventsCount = 0;
    private int deleteUserEventsCount = 0;

    @BeforeEach
    void setUp() {
        changesManager.addListener((AddUserListener) user -> addUserEventsCount++);
        changesManager.addListener((DeleteUserListener) user -> deleteUserEventsCount++);
    }

    @Test
    void testCasesAllAtOnce() {
        // Add
        addUser("test1");
        addUser("test2");
        addUser("test3");
        verifyState("test1", "test2", "test3");
        verifyCanRedo(false);
        verifyCanUndo(true);
        verifyAddUserEventsCount(3);
        // Delete
        deleteUser("test2");
        verifyState("test1", "test3");
        verifyCanRedo(false);
        verifyCanUndo(true);
        verifyDeleteUserEventsCount(1);
        // Undo
        undo();
        verifyState("test1", "test2", "test3");
        verifyCanRedo(true);
        verifyCanUndo(true);
        verifyAddUserEventsCount(4);
        // Undo again
        undo();
        verifyState("test1", "test2");
        verifyCanRedo(true);
        verifyCanUndo(true);
        verifyDeleteUserEventsCount(2);
        // Redo
        redo();
        verifyState("test1", "test2", "test3");
        verifyCanRedo(true);
        verifyCanUndo(true);
        verifyAddUserEventsCount(5);
        // Undo while it is possible
        undo();
        undo();
        undo();
        verifyState();
        verifyCanRedo(true);
        verifyCanUndo(false);
        verifyDeleteUserEventsCount(5);
        // Add field again
        addUser("test4");
        verifyState("test4");
        verifyCanRedo(false);
        verifyCanUndo(true);
        verifyAddUserEventsCount(6);
        verifyDeleteUserEventsCount(5);
    }

    private void addUser(String user) {
        changesManager.executeCommand(new AddUserCommand(changesManager, user));
    }

    private void deleteUser(String user) {
        changesManager.executeCommand(new DeleteUserCommand(changesManager, user));
    }

    private void undo() {
        changesManager.undo();
    }

    private void redo() {
        changesManager.redo();
    }

    void verifyState(String... users) {
        assertThat(changesState.users()).containsExactlyInAnyOrder(users);
    }

    void verifyCanUndo(boolean canUndo) {
        assertThat(changesManager.canUndo()).isEqualTo(canUndo);
    }

    void verifyCanRedo(boolean canRedo) {
        assertThat(changesManager.canRedo()).isEqualTo(canRedo);
    }

    void verifyAddUserEventsCount(int expectEventsCount) {
        assertThat(addUserEventsCount).isEqualTo(expectEventsCount);
    }

    void verifyDeleteUserEventsCount(int expectEventsCount) {
        assertThat(deleteUserEventsCount).isEqualTo(expectEventsCount);
    }
}