package com.luxoft.bankapp.model;

import com.luxoft.bankapp.exceptions.OverdraftLimitExceededException;
import com.luxoft.bankapp.service.feed.Feed;

import java.util.Map;

public class CheckingAccount extends AbstractAccount {
    @Feed
    private double overdraft = 0;

    public CheckingAccount(double overdraft) {
        setOverdraft(overdraft);
    }

    public void setOverdraft(double amount) {
        if (overdraft < 0) {
            return;
        }

        overdraft = amount;
    }

    public double getOverdraft() {
        return overdraft;
    }

    @Override
    public void withdraw(double amount) throws OverdraftLimitExceededException {
        if (getBalance() + overdraft < amount) {
            throw new OverdraftLimitExceededException(
                    String.valueOf(getId()), getBalance() + overdraft);
        }

        setBalance(getBalance() - amount);

    }

    // TODO feed
    @Override
    public void parseFeed(Map<String, String> map) {
        super.parseFeed(map);

        String overdraft = map.get("overdraft");
        this.overdraft = Double.parseDouble(overdraft != null ? overdraft : "");
    }
}
