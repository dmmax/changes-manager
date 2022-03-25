package me.dmmax.patterns.history.event;

public class AddUserEvent implements ChangesEvent {

    private final String user;

    public AddUserEvent(String user) {
        this.user = user;
    }

    public String user() {
        return user;
    }
}
