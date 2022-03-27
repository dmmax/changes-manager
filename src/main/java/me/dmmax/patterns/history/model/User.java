package me.dmmax.patterns.history.model;

public class User {

    private final String id;
    private final String name;
    private final String companyId;

    public User(String id, String name, String companyId) {
        this.id = id;
        this.name = name;
        this.companyId = companyId;
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String companyId() {
        return companyId;
    }
}
