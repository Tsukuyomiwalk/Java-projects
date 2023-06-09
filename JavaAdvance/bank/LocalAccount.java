package info.kgeorgiy.ja.latanov.bank;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;

public class LocalAccount implements Account, Serializable {
    private final String id;
    private int amount;
    private final List<Account> localAccounts;


    public LocalAccount(String id, List<Account> localAccounts) {
        this.id = id;
        this.localAccounts = localAccounts;
        amount = 0;
    }

    @Override
    public String getId() throws RemoteException {
        return id;
    }

    @Override
    public int getAmount() throws RemoteException {
        return amount;
    }

    @Override
    public void setAmount(int amount) throws RemoteException {
        this.amount = amount;
    }


    public void addLocalAccount(Account account) {
        localAccounts.add(account);
    }

    public Account getLocalAccount(String id) {
        return localAccounts.stream()
                .filter(account -> {
                    try {
                        return account.getId().equals(id);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    return false;
                })
                .findFirst()
                .orElse(null);
    }
}

