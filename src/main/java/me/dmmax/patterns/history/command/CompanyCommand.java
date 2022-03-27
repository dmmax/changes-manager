package me.dmmax.patterns.history.command;

import me.dmmax.patterns.history.ChangesManager;
import me.dmmax.patterns.history.ChangesState;
import me.dmmax.patterns.history.event.CompanyEvent;
import me.dmmax.patterns.history.event.UserEvent;
import me.dmmax.patterns.history.model.Company;
import me.dmmax.patterns.history.model.User;

import java.util.List;

public abstract class CompanyCommand implements UndoCommand {

    protected final ChangesManager changesManager;
    protected final ChangesState state;
    protected final Company company;

    protected CompanyCommand(ChangesManager changesManager, ChangesState state, Company company) {
        this.changesManager = changesManager;
        this.state = state;
        this.company = company;
    }

    public static AddCompanyCommand addCompany(ChangesManager changesManager, ChangesState state, Company company) {
        return new AddCompanyCommand(changesManager, state, company);
    }

    public static DeleteCompanyCommand deleteCompany(ChangesManager changesManager, ChangesState state, Company company) {
        return new DeleteCompanyCommand(changesManager, state, company);
    }

    public static class AddCompanyCommand extends CompanyCommand {

        private AddCompanyCommand(ChangesManager changesManager, ChangesState state, Company company) {
            super(changesManager, state, company);
        }

        @Override
        public void execute() {
            changesManager.post(CompanyEvent.addCompany(company));
        }

        @Override
        public UndoCommand undoCommand() {
            return deleteCompany(changesManager, state, company);
        }
    }

    public static class DeleteCompanyCommand extends CompanyCommand {

        private final List<User> companyUsers;

        private DeleteCompanyCommand(ChangesManager changesManager, ChangesState state, Company company) {
            super(changesManager, state, company);
            this.companyUsers = state.findUsersByCompanyId(company.id());
        }

        @Override
        public void execute() {
            changesManager.post(CompanyEvent.deleteCompany(company));
            changesManager.post(UserEvent.deleteUsers(companyUsers));
        }

        @Override
        public UndoCommand undoCommand() {
            if (companyUsers.isEmpty()) {
                return addCompany(changesManager, state, company);
            }
            return new AddCompanyWithUsersCommand(changesManager, state, company, companyUsers);
        }
    }

    private static class AddCompanyWithUsersCommand extends AddCompanyCommand {

        private final List<User> companyUsers;

        private AddCompanyWithUsersCommand(ChangesManager changesManager, ChangesState state, Company company,
                                           List<User> companyUsers) {
            super(changesManager, state, company);
            this.companyUsers = companyUsers;
        }

        @Override
        public void execute() {
            super.execute();
            changesManager.post(UserEvent.addUsers(companyUsers));
        }

        @Override
        public UndoCommand undoCommand() {
            return deleteCompany(changesManager, state, company);
        }
    }
}
