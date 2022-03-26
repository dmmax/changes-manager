package me.dmmax.patterns.history;

import com.google.common.eventbus.Subscribe;
import me.dmmax.patterns.history.command.UserCommand;
import me.dmmax.patterns.history.event.ExceptionHandlerEvent;
import me.dmmax.patterns.history.event.UserEvent;
import me.dmmax.patterns.history.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChangesManagerTest {

    private final ChangesState changesState = new ChangesState();
    private final ChangesManager changesManager = new ChangesManager(changesState);
    private final HistoryProvider historyProvider = changesManager.historyProvider();

    private int addUserEventsCount = 0;
    private int deleteUserEventsCount = 0;
    private int updateUserEventsCount = 0;

    private int exceptionThrownEventsCount = 0;

    @BeforeEach
    void setUp() {
        changesManager.registerListener(new Object() {
            @Subscribe
            void onAddedUser(UserEvent.AddUserEvent event) {
                addUserEventsCount++;
            }

            @Subscribe
            void onAddedUser(UserEvent.DeleteUserEvent event) {
                deleteUserEventsCount++;
            }

            @Subscribe
            void onUpdatedUser(UserEvent.UpdateUserEvent event) {
                updateUserEventsCount++;
            }

            @Subscribe
            void onExceptionThrown(ExceptionHandlerEvent event) {
                exceptionThrownEventsCount++;
            }
        });
    }

    @Test
    void testAddUser() {
        // Add first user
        addUser("test1");
        verifyState("test1");
        verifyCanUndo(true);
        verifyCanRedo(false);
        verifyAddUserEventsCount(1);
        // Add second user
        addUser("test2");
        verifyState("test1", "test2");
        verifyCanUndo(true);
        verifyCanRedo(false);
        verifyAddUserEventsCount(2);
        // Undo
        undo();
        verifyState("test1");
        verifyCanUndo(true);
        verifyCanRedo(true);
        verifyDeleteUserEventsCount(1);
        // One more undo
        undo();
        // Should be empty list
        verifyState();
        verifyCanUndo(false);
        verifyCanRedo(true);
        verifyAddUserEventsCount(2);
        verifyDeleteUserEventsCount(2);
    }

    @Test
    void testDeleteUser() {
        // Add two users
        addUser("test1");
        addUser("test2");
        verifyState("test1", "test2");
        verifyCanUndo(true);
        verifyCanRedo(false);
        verifyAddUserEventsCount(2);
        // Delete first user
        deleteUser("test1");
        verifyState("test2");
        verifyCanUndo(true);
        verifyCanRedo(false);
        verifyDeleteUserEventsCount(1);
        // Try to delete first user again (should throw an exception)
        verifyThrownExceptionEventsCount(0);
        deleteUser("test1");
        verifyThrownExceptionEventsCount(1);
        verifyDeleteUserEventsCount(2);
        // Delete user 2
        deleteUser("test2");
        // Should be empty list
        verifyState();
        verifyCanUndo(true);
        verifyCanRedo(false);
        verifyDeleteUserEventsCount(3);
        // Undo
        undo();
        verifyState("test2");
        verifyCanUndo(true);
        verifyCanRedo(true);
        verifyDeleteUserEventsCount(3);
        verifyAddUserEventsCount(3);
    }

    @Test
    void testUpdateUser() {
        // Add two users
        var user1 = addUser("test1");
        var user2 = addUser("test2");
        verifyState("test1", "test2");
        verifyCanUndo(true);
        verifyCanRedo(false);
        verifyAddUserEventsCount(2);
        // Update user 1
        updateUser(user1, "user1");
        verifyState("user1", "test2");
        verifyCanUndo(true);
        verifyCanRedo(false);
        verifyUpdateUserEventsCount(1);
        // Update user 2
        updateUser(user2, "user2");
        verifyState("user1", "user2");
        verifyCanUndo(true);
        verifyCanRedo(false);
        verifyUpdateUserEventsCount(2);
        // Undo two times
        undo();
        undo();
        verifyState("test1", "test2");
        verifyCanUndo(true);
        verifyCanRedo(true);
        verifyUpdateUserEventsCount(4);
        // Redo two times to get the final state
        redo();
        redo();
        verifyState("user1", "user2");
        verifyCanUndo(true);
        verifyCanRedo(false);
        verifyUpdateUserEventsCount(6);
        // Try to update non-existing user id
        verifyThrownExceptionEventsCount(0);
        updateUser(new User("unkonwn-id", "unknown-name"), "newName");
        verifyThrownExceptionEventsCount(1);
        verifyUpdateUserEventsCount(7);
    }

    private User addUser(String idAndName) {
        var userToAdd = new User(idAndName, idAndName);
        changesManager.executeCommand(UserCommand.addUser(changesManager, userToAdd));
        return userToAdd;
    }

    private void deleteUser(String idAndName) {
        changesManager.executeCommand(UserCommand.deleteUser(changesManager, new User(idAndName, idAndName)));
    }

    private void updateUser(User oldUser, String newName) {
        changesManager.executeCommand(UserCommand.updateUser(changesManager, oldUser, new User(oldUser.id(), newName)));
    }

    private void undo() {
        historyProvider.undo();
    }

    private void redo() {
        historyProvider.redo();
    }

    void verifyState(String... users) {
        assertThat(changesState.users()).extracting(User::name)
                .containsExactlyInAnyOrder(users);
    }

    void verifyCanUndo(boolean canUndo) {
        assertThat(historyProvider.canUndo()).isEqualTo(canUndo);
    }

    void verifyCanRedo(boolean canRedo) {
        assertThat(historyProvider.canRedo()).isEqualTo(canRedo);
    }

    void verifyAddUserEventsCount(int expectEventsCount) {
        assertThat(addUserEventsCount).isEqualTo(expectEventsCount);
    }

    void verifyDeleteUserEventsCount(int expectEventsCount) {
        assertThat(deleteUserEventsCount).isEqualTo(expectEventsCount);
    }

    void verifyUpdateUserEventsCount(int expectEventsCount) {
        assertThat(updateUserEventsCount).isEqualTo(expectEventsCount);
    }

    void verifyThrownExceptionEventsCount(int expectEventsCount) {
        assertThat(exceptionThrownEventsCount).isEqualTo(expectEventsCount);
    }
}