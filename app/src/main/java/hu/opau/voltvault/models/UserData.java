package hu.opau.voltvault.models;

import java.util.List;

public class UserData {
    private List<BillingAddress> billingAddresses;
    private String firstName;
    private String lastName;

    public UserData() {}

    public UserData(List<BillingAddress> billingAddresses, String firstName, String lastName) {
        this.billingAddresses = billingAddresses;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public List<BillingAddress> getBillingAddresses() {
        return billingAddresses;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
