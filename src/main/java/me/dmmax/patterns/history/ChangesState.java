package me.dmmax.patterns.history;

import java.util.HashSet;
import java.util.Set;

public class ChangesState {

    private final Set<String> users = new HashSet<>();

    public Set<String> users() {
        return users;
    }

    void addUser(String user) {
        users.add(user);
    }

    void deleteUser(String user) {
        users.remove(user);
    }
}
