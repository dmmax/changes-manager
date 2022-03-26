package me.dmmax.patterns.history;

import com.google.common.base.VerifyException;
import com.google.common.eventbus.Subscribe;
import me.dmmax.patterns.history.event.UserEvent;
import me.dmmax.patterns.history.model.User;

import java.util.ArrayList;
import java.util.List;

public class ChangesState {

    private final List<User> users = new ArrayList<>();

    public List<User> users() {
        return users;
    }

    @Subscribe
    void onAddedUser(UserEvent.AddUserEvent event) {
        var user = event.user();
        System.out.println("Added user: " + user.name());
        users.add(user);
    }

    @Subscribe
    void onDeletedUser(UserEvent.DeleteUserEvent event) {
        var user = event.user();
        System.out.println("Deleted user: " + user.name());
        var idx = findIdxById(user.id());
        users.remove(idx);
    }

    @Subscribe
    void onUpdateUser(UserEvent.UpdateUserEvent event) {
        var user = event.user();
        System.out.println("Updated user: " + user.name());
        var idx = findIdxById(user.id());
        users.set(idx, user);
    }

    private int findIdxById(String userId) {
        for (var idx = 0; idx < users.size(); idx++) {
            if (users.get(idx).id().equals(userId)) {
                return idx;
            }
        }
        throw new VerifyException("Could not find user by id: " + userId);
    }
}
