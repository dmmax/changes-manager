package me.dmmax.patterns.history.command;

import me.dmmax.patterns.history.ChangesManager;
import me.dmmax.patterns.history.event.CompanyEvent;
import me.dmmax.patterns.history.model.Company;

public abstract class CompanyCommand implements UndoCommand {

    protected final ChangesManager changesManager;
    protected final Company company;

    protected CompanyCommand(ChangesManager changesManager, Company company) {
        this.changesManager = changesManager;
        this.company = company;
    }

    public static AddCompanyCommand addCompany(ChangesManager changesManager, Company company) {
        return new AddCompanyCommand(changesManager, company);
    }

    public static DeleteCompanyCommand deleteCompany(ChangesManager changesManager, Company company) {
        return new DeleteCompanyCommand(changesManager, company);
    }

    public static class AddCompanyCommand extends CompanyCommand {

        private AddCompanyCommand(ChangesManager changesManager, Company company) {
            super(changesManager, company);
        }

        @Override
        public void execute() {
            changesManager.post(CompanyEvent.addCompany(company));
        }

        @Override
        public UndoCommand undoCommand() {
            return deleteCompany(changesManager, company);
        }
    }

    public static class DeleteCompanyCommand extends CompanyCommand {

        private DeleteCompanyCommand(ChangesManager changesManager, Company company) {
            super(changesManager, company);
        }

        @Override
        public void execute() {
            changesManager.post(CompanyEvent.deleteCompany(company));
        }

        @Override
        public UndoCommand undoCommand() {
            return addCompany(changesManager, company);
        }
    }
}
