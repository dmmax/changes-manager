package me.dmmax.patterns.history.command;

import me.dmmax.patterns.history.ChangesManager;

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
        changesManager.deleteUser(user);
    }

    @Override
    public Command undo() {
        return new AddUserCommand(changesManager, user);
    }
}
