package me.dmmax.patterns.history;

import com.google.common.eventbus.Subscribe;
import me.dmmax.patterns.history.event.UserEvent;

import java.util.HashSet;
import java.util.Set;

public class ChangesState {

    private final Set<String> users = new HashSet<>();

    public Set<String> users() {
        return users;
    }

    @Subscribe
    void onAddedUser(UserEvent.AddUserEvent event) {
        var user = event.user();
        System.out.println("Added user: " + user);
        users.add(user);
    }

    @Subscribe
    void onDeletedUser(UserEvent.DeleteUserEvent event) {
        var user = event.user();
        System.out.println("Deleted user: " + user);
        users.remove(user);
    }
}
