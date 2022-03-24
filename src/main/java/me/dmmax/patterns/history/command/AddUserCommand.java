package me.dmmax.patterns.history.command;

import me.dmmax.patterns.history.ChangesManager;

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
        changesManager.addUser(user);
    }

    @Override
    public Command undo() {
        return new DeleteUserCommand(changesManager, user);
    }
}
