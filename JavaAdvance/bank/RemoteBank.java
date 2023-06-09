package info.kgeorgiy.ja.latanov.bank;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
/**
 * @author created by Daniil Latanov
 */
public class RemoteBank extends UnicastRemoteObject implements Bank {
    private final ConcurrentMap<String, Account> accounts;
    private final ConcurrentMap<String, Person> clients;

    public RemoteBank(int port) throws RemoteException {
        super(port);
        accounts = new ConcurrentHashMap<>();
        clients = new ConcurrentHashMap<>();
    }

    @Override
    public Account createAccount(String id) throws RemoteException {
        Account account = new RemoteAccount(id);
        accounts.put(id, account);
        return account;
    }

    @Override
    public Account getAccount(String id) throws RemoteException {
        return accounts.get(id);
    }

    // :NOTE: не т тестов
    public Person findPersonByPassport(String passportNumber, boolean isRemote) throws RemoteException {
        Person person = clients.get(passportNumber);
        if (person != null) {
            if (isRemote) {
                return new RemotePerson(person.getFirstName(), person.getLastName(), person.getPassport());
            }
            return new LocalPerson(person.getFirstName(), person.getLastName(), person.getPassport());
        }
        return null;
    }

    public void addPerson(Person person) throws RemoteException {
        String passportNumber = person.getPassport();
        if (!clients.containsKey(passportNumber)) {
            clients.put(passportNumber, person);
        }
    }

    public void updateAccountBalance(String accountId, int amount) throws RemoteException {
        Account account = accounts.get(accountId);
        if (account != null) {
            account.setAmount(account.getAmount() + amount);
        }
    }
}
