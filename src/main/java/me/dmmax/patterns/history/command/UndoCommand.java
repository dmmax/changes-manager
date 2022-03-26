package me.dmmax.patterns.history.command;

public interface UndoCommand {

    void execute();

    UndoCommand undoCommand();
}
