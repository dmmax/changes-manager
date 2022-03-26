package me.dmmax.patterns.history.event;

import me.dmmax.patterns.history.model.User;

public abstract class UserEvent implements ChangesEvent {

    private final User user;

    protected UserEvent(User user) {
        this.user = user;
    }

    public User user() {
        return user;
    }

    public static AddUserEvent addUser(User user) {
        return new AddUserEvent(user);
    }

    public static DeleteUserEvent deleteUser(User user) {
        return new DeleteUserEvent(user);
    }

    public static UpdateUserEvent updateUser(User user) {
        return new UpdateUserEvent(user);
    }

    public static class AddUserEvent extends UserEvent {

        private AddUserEvent(User user) {
            super(user);
        }
    }

    public static class DeleteUserEvent extends UserEvent {

        private DeleteUserEvent(User user) {
            super(user);
        }
    }

    public static class UpdateUserEvent extends UserEvent {

        private UpdateUserEvent(User user) {
            super(user);
        }
    }
}
