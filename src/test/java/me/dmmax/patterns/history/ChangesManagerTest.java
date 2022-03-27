package me.dmmax.patterns.history;

import com.google.common.eventbus.Subscribe;
import me.dmmax.patterns.history.command.CompanyCommand;
import me.dmmax.patterns.history.command.UserCommand;
import me.dmmax.patterns.history.event.CompanyEvent;
import me.dmmax.patterns.history.event.ExceptionHandlerEvent;
import me.dmmax.patterns.history.event.UserEvent;
import me.dmmax.patterns.history.model.Company;
import me.dmmax.patterns.history.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChangesManagerTest {

    private static final String DEFAULT_COMPANY_ID = "companyId";

    private final ChangesState changesState = new ChangesState();
    private final ChangesManager changesManager = new ChangesManager(changesState);
    private final HistoryProvider historyProvider = changesManager.historyProvider();

    private int addUserEventsCount = 0;
    private int deleteUserEventsCount = 0;
    private int updateUserEventsCount = 0;
    private int addUsersEventsCount = 0;
    private int deleteUsersEventsCount = 0;

    private int exceptionThrownEventsCount = 0;

    private int addCompanyEventsCount = 0;
    private int deleteCompanyEventsCount = 0;

    @BeforeEach
    void setUp() {
        changesManager.registerListener(new Object() {
            @Subscribe
            private void onAddedUser(UserEvent.AddUserEvent event) {
                addUserEventsCount++;
            }

            @Subscribe
            private void onAddedUser(UserEvent.DeleteUserEvent event) {
                deleteUserEventsCount++;
            }

            @Subscribe
            private void onUpdatedUser(UserEvent.UpdateUserEvent event) {
                updateUserEventsCount++;
            }

            @Subscribe
            private void onAddedUsers(UserEvent.AddUsersEvent event) {
                addUsersEventsCount++;
            }

            @Subscribe
            private void onDeletedUsers(UserEvent.DeleteUsersEvent event) {
                deleteUsersEventsCount++;
            }

            @Subscribe
            private void onExceptionThrown(ExceptionHandlerEvent event) {
                exceptionThrownEventsCount++;
            }

            @Subscribe
            private void onAddedCompany(CompanyEvent.AddCompanyEvent event) {
                addCompanyEventsCount++;
            }

            @Subscribe
            private void onDeletedCompany(CompanyEvent.DeleteCompanyEvent event) {
                deleteCompanyEventsCount++;
            }
        });
    }

    @Test
    void testAddUser() {
        // Add first user
        addUser("test1");
        verifyUserState("test1");
        verifyCanUndo(true);
        verifyCanRedo(false);
        verifyAddUserEventsCount(1);
        // Add second user
        addUser("test2");
        verifyUserState("test1", "test2");
        verifyCanUndo(true);
        verifyCanRedo(false);
        verifyAddUserEventsCount(2);
        // Undo
        undo();
        verifyUserState("test1");
        verifyCanUndo(true);
        verifyCanRedo(true);
        verifyDeleteUserEventsCount(1);
        // One more undo
        undo();
        // Should be empty list
        verifyUserState();
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
        verifyUserState("test1", "test2");
        verifyCanUndo(true);
        verifyCanRedo(false);
        verifyAddUserEventsCount(2);
        // Delete first user
        deleteUser("test1");
        verifyUserState("test2");
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
        verifyUserState();
        verifyCanUndo(true);
        verifyCanRedo(false);
        verifyDeleteUserEventsCount(3);
        // Undo
        undo();
        verifyUserState("test2");
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
        verifyUserState("test1", "test2");
        verifyCanUndo(true);
        verifyCanRedo(false);
        verifyAddUserEventsCount(2);
        // Update user 1
        updateUser(user1, "user1");
        verifyUserState("user1", "test2");
        verifyCanUndo(true);
        verifyCanRedo(false);
        verifyUpdateUserEventsCount(1);
        // Update user 2
        updateUser(user2, "user2");
        verifyUserState("user1", "user2");
        verifyCanUndo(true);
        verifyCanRedo(false);
        verifyUpdateUserEventsCount(2);
        // Undo two times
        undo();
        undo();
        verifyUserState("test1", "test2");
        verifyCanUndo(true);
        verifyCanRedo(true);
        verifyUpdateUserEventsCount(4);
        // Redo two times to get the final state
        redo();
        redo();
        verifyUserState("user1", "user2");
        verifyCanUndo(true);
        verifyCanRedo(false);
        verifyUpdateUserEventsCount(6);
        // Try to update non-existing user id
        verifyThrownExceptionEventsCount(0);
        updateUser(new User("unknown-id", "unknown-name", DEFAULT_COMPANY_ID), "newName");
        verifyThrownExceptionEventsCount(1);
        verifyUpdateUserEventsCount(7);
    }

    @Test
    void testAddAndDeleteCompaniesWithoutUsers() {
        // Add two companies
        addCompany("test1");
        addCompany("test2");
        verifyCompanyState("test1", "test2");
        verifyAddCompanyEventsCount(2);
        // Undo
        undo();
        verifyCompanyState("test1");
        verifyAddCompanyEventsCount(2);
        verifyDeleteCompanyEventsCount(1);
        // Redo
        redo();
        verifyCompanyState("test1", "test2");
        verifyAddCompanyEventsCount(3);
        verifyDeleteCompanyEventsCount(1);
        // Delete company
        deleteCompany("test1");
        verifyCompanyState("test2");
        verifyAddCompanyEventsCount(3);
        verifyDeleteCompanyEventsCount(2);
        // Undo
        undo();
        verifyCompanyState("test1", "test2");
        verifyAddCompanyEventsCount(4);
        verifyDeleteCompanyEventsCount(2);
        // Delete non-existing company
        verifyThrownExceptionEventsCount(0);
        deleteCompany("non-existing-company");
        verifyThrownExceptionEventsCount(1);
        verifyDeleteCompanyEventsCount(3);
        verifyAddUsersEventsCount(0);
        verifyDeleteUsersEventsCount(0);
    }

    @Test
    void testAddAndDeleteCompanyWithUsers() {
        // Add company
        var companyId = "company1";
        addCompany(companyId);
        verifyCompanyState(companyId);
        verifyAddCompanyEventsCount(1);
        // Add two users with the same company id
        addUser("user1", companyId);
        addUser("user2", companyId);
        verifyUserState("user1", "user2");
        verifyAddUserEventsCount(2);
        // Delete company
        deleteCompany(companyId);
        // Both states should be empty
        verifyCompanyState();
        verifyUserState();
        verifyDeleteUsersEventsCount(1);
        // Undo
        undo();
        verifyCompanyState(companyId);
        verifyUserState("user1", "user2");
        verifyAddCompanyEventsCount(2);
        verifyAddUsersEventsCount(1);
        // Redo
        redo();
        verifyCompanyState();
        verifyUserState();
        verifyAddCompanyEventsCount(2);
        verifyAddUsersEventsCount(1);
        verifyDeleteCompanyEventsCount(2);
        verifyDeleteUsersEventsCount(2);

    }

    private User addUser(String idAndName) {
        return addUser(idAndName, DEFAULT_COMPANY_ID);
    }

    private User addUser(String idAndName, String companyId) {
        var userToAdd = new User(idAndName, idAndName, companyId);
        changesManager.executeCommand(UserCommand.addUser(changesManager, userToAdd));
        return userToAdd;
    }

    private void deleteUser(String idAndName) {
        var userToDelete = new User(idAndName, idAndName, DEFAULT_COMPANY_ID);
        changesManager.executeCommand(UserCommand.deleteUser(changesManager, userToDelete));
    }

    private void updateUser(User oldUser, String newName) {
        var newUser = new User(oldUser.id(), newName, DEFAULT_COMPANY_ID);
        changesManager.executeCommand(UserCommand.updateUser(changesManager, oldUser, newUser));
    }

    private void addCompany(String idAndName) {
        var companyToAdd = new Company(idAndName, idAndName);
        changesManager.executeCommand(CompanyCommand.addCompany(changesManager, changesState, companyToAdd));
    }

    private void deleteCompany(String idAndName) {
        var companyToDelete = new Company(idAndName, idAndName);
        changesManager.executeCommand(CompanyCommand.deleteCompany(changesManager, changesState, companyToDelete));
    }

    private void undo() {
        historyProvider.undo();
    }

    private void redo() {
        historyProvider.redo();
    }

    void verifyUserState(String... userNames) {
        assertThat(changesState.users()).extracting(User::name)
                .containsExactlyInAnyOrder(userNames);
    }

    void verifyCompanyState(String... companyNames) {
        assertThat(changesState.companies()).extracting(Company::name)
                .containsExactlyInAnyOrder(companyNames);
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

    void verifyAddUsersEventsCount(int expectedEventsCount) {
        assertThat(addUsersEventsCount).isEqualTo(expectedEventsCount);
    }

    void verifyDeleteUsersEventsCount(int expectedEventsCount) {
        assertThat(deleteUsersEventsCount).isEqualTo(expectedEventsCount);
    }

    void verifyThrownExceptionEventsCount(int expectEventsCount) {
        assertThat(exceptionThrownEventsCount).isEqualTo(expectEventsCount);
    }

    void verifyAddCompanyEventsCount(int expectEventsCount) {
        assertThat(addCompanyEventsCount).isEqualTo(expectEventsCount);
    }

    void verifyDeleteCompanyEventsCount(int expectEventsCount) {
        assertThat(deleteCompanyEventsCount).isEqualTo(expectEventsCount);
    }
}