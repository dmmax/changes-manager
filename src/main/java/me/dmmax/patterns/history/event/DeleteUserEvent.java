package me.dmmax.patterns.history.event;

public class DeleteUserEvent implements ChangesEvent {

    private final String user;

    public DeleteUserEvent(String user) {
        this.user = user;
    }

    public String user() {
        return user;
    }
}
