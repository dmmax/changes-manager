package me.dmmax.patterns.history.event;

import me.dmmax.patterns.history.model.Company;

public abstract class CompanyEvent implements ChangesEvent {

    private final Company company;

    protected CompanyEvent(Company company) {
        this.company = company;
    }

    public Company company() {
        return company;
    }

    public static AddCompanyEvent addCompany(Company company) {
        return new AddCompanyEvent(company);
    }

    public static DeleteCompanyEvent deleteCompany(Company company) {
        return new DeleteCompanyEvent(company);
    }

    public static class AddCompanyEvent extends CompanyEvent {

        private AddCompanyEvent(Company company) {
            super(company);
        }
    }

    public static class DeleteCompanyEvent extends CompanyEvent {

        private DeleteCompanyEvent(Company company) {
            super(company);
        }
    }
}
