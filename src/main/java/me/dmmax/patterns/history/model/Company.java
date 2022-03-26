package me.dmmax.patterns.history.model;

public class Company {

    private final String id;
    private final String name;

    public Company(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }
}
