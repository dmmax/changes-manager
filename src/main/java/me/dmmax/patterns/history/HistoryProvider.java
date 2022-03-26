package me.dmmax.patterns.history;

import me.dmmax.patterns.history.command.UndoCommand;

import java.util.ArrayDeque;
import java.util.Deque;

public class HistoryProvider {

    private final Deque<UndoCommand> undoHistory = new ArrayDeque<>();
    private final Deque<UndoCommand> redoHistory = new ArrayDeque<>();

    public void addCommand(UndoCommand command) {
        redoHistory.clear();
        undoHistory.add(command.undoCommand());
    }

    public void undo() {
        executeCommandFromHistory(undoHistory, redoHistory);
    }

    public void redo() {
        executeCommandFromHistory(redoHistory, undoHistory);
    }

    private void executeCommandFromHistory(Deque<UndoCommand> historyCommands, Deque<UndoCommand> undoCommands) {
        System.out.println("Executing command from history provider");
        assert !historyCommands.isEmpty();
        var fromCommand = historyCommands.pollLast();
        fromCommand.execute();
        undoCommands.add(fromCommand.undoCommand());
    }

    public boolean canUndo() {
        return !undoHistory.isEmpty();
    }

    public boolean canRedo() {
        return !redoHistory.isEmpty();
    }
}
