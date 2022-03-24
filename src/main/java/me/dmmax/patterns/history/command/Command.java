package me.dmmax.patterns.history.command;

public interface Command {

    void execute();

    Command undo();
}
