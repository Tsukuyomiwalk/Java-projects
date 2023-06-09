package info.kgeorgiy.ja.latanov.bank;

import java.rmi.Remote;

public class Person implements Remote {
    private final String firstName;
    private final String lastName;
    private final String passportNumber;

    public Person(String firstName, String lastName, String passportNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.passportNumber = passportNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassport() {
        return passportNumber;
    }
}

