package me.dmmax.patterns.history.event;

import me.dmmax.patterns.history.model.User;

import java.util.List;

public interface UserEvent extends ChangesEvent {

    static AddUserEvent addUser(User user) {
        return new AddUserEvent(user);
    }

    static DeleteUserEvent deleteUser(User user) {
        return new DeleteUserEvent(user);
    }

    static UpdateUserEvent updateUser(User user) {
        return new UpdateUserEvent(user);
    }

    static AddUsersEvent addUsers(List<User> users) {
        return new AddUsersEvent(users);
    }

    static DeleteUsersEvent deleteUsers(List<User> users) {
        return new DeleteUsersEvent(users);
    }

    class SingleUserEvent implements UserEvent {

        private final User user;

        protected SingleUserEvent(User user) {
            this.user = user;
        }

        public User user() {
            return user;
        }
    }

    class AddUserEvent extends SingleUserEvent {

        private AddUserEvent(User user) {
            super(user);
        }
    }

    class DeleteUserEvent extends SingleUserEvent {

        private DeleteUserEvent(User user) {
            super(user);
        }
    }

    class UpdateUserEvent extends SingleUserEvent {

        private UpdateUserEvent(User user) {
            super(user);
        }
    }

    class MultipleUsersEvent implements UserEvent {

        private final List<User> users;

        protected MultipleUsersEvent(List<User> users) {
            this.users = users;
        }

        public List<User> users() {
            return users;
        }
    }

    class AddUsersEvent extends MultipleUsersEvent {

        private AddUsersEvent(List<User> users) {
            super(users);
        }
    }

    class DeleteUsersEvent extends MultipleUsersEvent {

        private DeleteUsersEvent(List<User> users) {
            super(users);
        }
    }
}
