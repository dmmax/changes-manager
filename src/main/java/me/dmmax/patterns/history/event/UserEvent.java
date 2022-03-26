package me.dmmax.patterns.history.event;

public abstract class UserEvent implements ChangesEvent {

    private final String user;

    protected UserEvent(String user) {
        this.user = user;
    }

    public String user() {
        return user;
    }

    public static AddUserEvent addUser(String user) {
        return new AddUserEvent(user);
    }

    public static DeleteUserEvent deleteUser(String user) {
        return new DeleteUserEvent(user);
    }

    public static class AddUserEvent extends UserEvent {

        private AddUserEvent(String user) {
            super(user);
        }
    }

    public static class DeleteUserEvent extends UserEvent {

        private DeleteUserEvent(String user) {
            super(user);
        }
    }
}
