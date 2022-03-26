package me.dmmax.patterns.history.command;

import me.dmmax.patterns.history.ChangesManager;
import me.dmmax.patterns.history.event.UserEvent;

public abstract class UserCommand implements UndoCommand{

    protected final ChangesManager changesManager;

    protected UserCommand(ChangesManager changesManager) {
        this.changesManager = changesManager;
    }

    public static AddUserCommand addUser(ChangesManager changesManager, String user) {
        return new AddUserCommand(changesManager, user);
    }

    public static DeleteUserCommand deleteUser(ChangesManager changesManager, String user) {
        return new DeleteUserCommand(changesManager, user);
    }

    public static class AddUserCommand extends UserCommand {

        private final String user;

        private AddUserCommand(ChangesManager changesManager, String user) {
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

        private final String user;

        private DeleteUserCommand(ChangesManager changesManager, String user) {
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
}
