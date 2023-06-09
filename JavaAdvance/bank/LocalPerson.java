package info.kgeorgiy.ja.latanov.bank;

import java.io.Serializable;

public class LocalPerson extends Person implements Serializable {
    public LocalPerson(String firstName, String lastName, String passportNumber) {
        super(firstName, lastName, passportNumber);
    }

    // :NOTE: как получить локальные аккаунты без банка
}
