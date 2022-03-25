package me.dmmax.patterns.history.command;

import me.dmmax.patterns.history.ChangesManager;
import me.dmmax.patterns.history.event.AddUserEvent;

public class AddUserCommand implements Command {

    private final ChangesManager changesManager;
    private final String user;

    public AddUserCommand(ChangesManager changesManager, String user) {
        this.changesManager = changesManager;
        this.user = user;
    }

    @Override
    public void execute() {
        System.out.println("Added user " + user);
        changesManager.post(new AddUserEvent(user));
    }

    @Override
    public Command undoCommand() {
        return new DeleteUserCommand(changesManager, user);
    }
}
