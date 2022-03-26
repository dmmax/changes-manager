package me.dmmax.patterns.history.command;

import me.dmmax.patterns.history.ChangesManager;
import me.dmmax.patterns.history.event.UserEvent;
import me.dmmax.patterns.history.model.User;

public abstract class UserCommand implements UndoCommand{

    protected final ChangesManager changesManager;

    protected UserCommand(ChangesManager changesManager) {
        this.changesManager = changesManager;
    }

    public static AddUserCommand addUser(ChangesManager changesManager, User user) {
        return new AddUserCommand(changesManager, user);
    }

    public static DeleteUserCommand deleteUser(ChangesManager changesManager, User user) {
        return new DeleteUserCommand(changesManager, user);
    }

    public static UpdateUserCommand updateUser(ChangesManager changesManager, User oldUser, User newUser) {
        return new UpdateUserCommand(changesManager, oldUser, newUser);
    }

    public static class AddUserCommand extends UserCommand {

        private final User user;

        private AddUserCommand(ChangesManager changesManager, User user) {
            super(changesManager);
            this.user = user;
        }

        @Override
        public void execute() {
            changesManager.post(UserEvent.addUser(user));
        }

        @Override
        public UndoCommand undoCommand() {
            return UserCommand.deleteUser(changesManager, user);
        }
    }

    public static class DeleteUserCommand extends UserCommand {

        private final User user;

        private DeleteUserCommand(ChangesManager changesManager, User user) {
            super(changesManager);
            this.user = user;
        }

        @Override
        public void execute() {
            changesManager.post(UserEvent.deleteUser(user));
        }

        @Override
        public UndoCommand undoCommand() {
            return UserCommand.addUser(changesManager, user);
        }
    }

    public static class UpdateUserCommand extends UserCommand {

        private final User oldUser;
        private final User newUser;

        private UpdateUserCommand(ChangesManager changesManager, User oldUser, User newUser) {
            super(changesManager);
            this.oldUser = oldUser;
            this.newUser = newUser;
        }

        @Override
        public void execute() {
            changesManager.post(UserEvent.updateUser(newUser));
        }

        @Override
        public UndoCommand undoCommand() {
            // Replace positions newUser -> oldUser, oldUser -> newUser
            return UserCommand.updateUser(changesManager, newUser, oldUser);
        }
    }
}
