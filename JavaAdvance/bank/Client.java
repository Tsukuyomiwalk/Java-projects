package info.kgeorgiy.ja.latanov.bank;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public final class Client {

    private static final int BANK_PORT = 8888;

    /**
     * Utility class.
     */
    private Client() {
    }

    public static void main(final String... args) throws RemoteException {

        if (args.length < 5) {
            System.err.println("Not enough arguments");
            return;
        }

        String firstName = args[0];
        String lastName = args[1];
        String passportNumber = args[2];
        String accountNumber = args[3];
        int amount = Integer.parseInt(args[4]);

        try {
            Registry registry = LocateRegistry.createRegistry(BANK_PORT);

            Bank bank = new RemoteBank(BANK_PORT);

            registry.rebind("bank", bank);

            new RemotePerson(firstName, lastName, passportNumber);
            new LocalPerson(firstName, lastName, passportNumber);
            Account account = bank.getAccount(passportNumber + ":" + accountNumber);
            if (account == null) {
                account = bank.createAccount(passportNumber + ":" + accountNumber);
            }

            account.setAmount(account.getAmount() + amount);

            System.out.println("New balance: " + account.getAmount());
            account.setAmount(account.getAmount() + 120);
            System.out.println("New balance: " + account.getAmount());
            System.exit(0);
        } catch (RemoteException e) {
            System.err.println("Remote exception: " + e.getMessage());
        }
    }
}