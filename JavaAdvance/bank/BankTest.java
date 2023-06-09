package info.kgeorgiy.ja.latanov.bank;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.rmi.RemoteException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class BankTest {
    private RemoteBank bank;


    @Before
    public void setUp() throws RemoteException {
        bank = new RemoteBank(8888);
    }

    @Test
    public void testPersonFunctionality() {
        String firstName = "Dan";
        String lastName = "Darwin";
        String passportNumber = "000012123";
        Person remotePerson = new RemotePerson(firstName, lastName, passportNumber);
        Person localPerson = new LocalPerson(firstName, lastName, passportNumber);
        assertEquals(firstName, remotePerson.getFirstName());
        assertEquals(lastName, remotePerson.getLastName());
        assertEquals(passportNumber, remotePerson.getPassport());
        assertEquals(firstName, localPerson.getFirstName());
        assertEquals(lastName, localPerson.getLastName());
        assertEquals(passportNumber, localPerson.getPassport());


    }

    @Test
    public void testPersonFunctionality2() throws RemoteException {
        String firstName = "John";
        String lastName = "Doe";
        String passportNumber = "123456789";
        Person remotePerson = new RemotePerson(firstName, lastName, passportNumber);
        Person localPerson = new LocalPerson(firstName, lastName, passportNumber);

        Bank bank = new RemoteBank(8888);
        Account remoteAccount = bank.createAccount(String.valueOf(remotePerson));
        Account localAccount = bank.createAccount(String.valueOf(localPerson));

        int initialBalance = 100;
        remoteAccount.setAmount(initialBalance);
        localAccount.setAmount(initialBalance);
        assertEquals(initialBalance, remoteAccount.getAmount());
        assertEquals(initialBalance, localAccount.getAmount());

        int depositAmount = 50;
        remoteAccount.setAmount(remoteAccount.getAmount() + depositAmount);
        localAccount.setAmount(localAccount.getAmount() + depositAmount);

        int expectedBalance = initialBalance + depositAmount;
        assertEquals(expectedBalance, remoteAccount.getAmount());
        assertEquals(expectedBalance, localAccount.getAmount());
    }


    @Test
    public void testAccountCreation() throws RemoteException {
        String accountId = "123";
        Account account = bank.createAccount(accountId);

        assertNotNull(account);

        assertEquals(accountId, account.getId());
    }

    @Test
    public void testGetAccount() throws RemoteException {
        String nonExistentAccountId = "456";
        Account nonExistentAccount = bank.getAccount(nonExistentAccountId);

        assertNull(nonExistentAccount);

        String accountId = "789";
        bank.createAccount(accountId);

        Account retrievedAccount = bank.getAccount(accountId);

        assertNotNull(retrievedAccount);

        assertEquals(accountId, retrievedAccount.getId());
    }

    @Test
    public void testFindPersonByPassport_NonExistingPerson() throws RemoteException {
        RemoteBank bank = new RemoteBank(8888);

        Person foundPerson = bank.findPersonByPassport("342789547329", false);

        assertNull(foundPerson);
    }

    @Test
    public void testAddPerson_NewPerson() throws RemoteException {
        RemoteBank bank = new RemoteBank(8888);

        Person newPerson = new LocalPerson("Jane", "Smith", "48957395729");
        bank.addPerson(newPerson);

        Person foundPerson = bank.findPersonByPassport("48957395729", false);


        assertNotNull(foundPerson);
        assertEquals("Jane", foundPerson.getFirstName());
        assertEquals("Smith", foundPerson.getLastName());
        assertEquals("48957395729", foundPerson.getPassport());
    }

    @Test
    public void testAccountBalanceUpdate() throws RemoteException {
        String accountId = "123";
        Account account = bank.createAccount(accountId);

        int initialBalance = account.getAmount();
        assertEquals(0, initialBalance);

        int newBalance = 100;
        account.setAmount(newBalance);

        int updatedBalance = account.getAmount();
        assertEquals(newBalance, updatedBalance);
    }

    @Test
    public void testAccountConcurrency() throws InterruptedException, RemoteException {
        RemoteBank bank = new RemoteBank(8888);
        Account account = bank.createAccount("12345");
        int initialBalance = 0;
        account.setAmount(initialBalance);

        int numberOfThreads = 5;

        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

        IntStream.range(0, numberOfThreads).parallel().forEach(i -> {
            try {
               bank.updateAccountBalance(account.getId(), 10);

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        executor.shutdown();

        if (executor.awaitTermination(1L, TimeUnit.MINUTES)) {
            int finalBalance = account.getAmount();

            int expectedBalance = initialBalance + (numberOfThreads * 10);

            Assert.assertEquals(expectedBalance, finalBalance);
        } else {
            Assert.fail("Timeout occurred while waiting for threads to finish.");
        }
    }

}
