package me.dmmax.patterns.history;

import com.google.common.eventbus.Subscribe;
import me.dmmax.patterns.history.event.CompanyEvent;
import me.dmmax.patterns.history.event.UserEvent;
import me.dmmax.patterns.history.model.Company;
import me.dmmax.patterns.history.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChangesState {

    private final List<User> users = new ArrayList<>();
    private final List<Company> companies = new ArrayList<>();

    public List<User> users() {
        return users;
    }

    public List<User> findUsersByCompanyId(String companyId) {
        return users().stream()
                .filter(it -> it.companyId().equals(companyId))
                .collect(Collectors.toList());
    }

    public List<Company> companies() {
        return companies;
    }

    // -- user

    @Subscribe
    private void onAddedUser(UserEvent.AddUserEvent event) {
        var user = event.user();
        System.out.println("Added user: " + user.name());
        users.add(user);
    }

    @Subscribe
    private void onDeletedUser(UserEvent.DeleteUserEvent event) {
        var user = event.user();
        System.out.println("Deleted user: " + user.name());
        var idx = findUserIdxById(user.id());
        users.remove(idx);
    }

    @Subscribe
    private void onUpdateUser(UserEvent.UpdateUserEvent event) {
        var user = event.user();
        System.out.println("Updated user: " + user.name());
        var idx = findUserIdxById(user.id());
        users.set(idx, user);
    }

    private int findUserIdxById(String userId) {
        for (var idx = 0; idx < users.size(); idx++) {
            if (users.get(idx).id().equals(userId)) {
                return idx;
            }
        }
        throw new IllegalArgumentException("Could not find user by id: " + userId);
    }

    // -- company

    @Subscribe
    private void onAddedCompany(CompanyEvent.AddCompanyEvent event) {
        var company = event.company();
        System.out.println("Added company: " + company.name());
        companies.add(company);
    }

    @Subscribe
    private void onDeletedCompany(CompanyEvent.DeleteCompanyEvent event) {
        var company = event.company();
        System.out.println("Deleted company: " + company.name());
        var idx = findCompanyIdxById(company.id());
        companies.remove(idx);
    }

    private int findCompanyIdxById(String companyId) {
        for (var idx = 0; idx < companies.size(); idx++) {
            if (companies.get(idx).id().equals(companyId)) {
                return idx;
            }
        }
        throw new IllegalArgumentException("Could not find company by id: " + companyId);
    }
}
