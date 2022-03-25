package me.dmmax.patterns.history.command;

import me.dmmax.patterns.history.ChangesManager;
import me.dmmax.patterns.history.event.DeleteUserEvent;

public class DeleteUserCommand implements Command {

    private final ChangesManager changesManager;
    private final String user;

    public DeleteUserCommand(ChangesManager changesManager, String user) {
        this.changesManager = changesManager;
        this.user = user;
    }

    @Override
    public void execute() {
        System.out.println("Deleted user " + user);
        changesManager.post(new DeleteUserEvent(user));
    }

    @Override
    public Command undoCommand() {
        return new AddUserCommand(changesManager, user);
    }
}
