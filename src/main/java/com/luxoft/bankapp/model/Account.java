package com.luxoft.bankapp.model;

import com.luxoft.bankapp.exceptions.NotEnoughFundsException;

import java.util.Map;

public interface Account extends Identifiable {
    double getBalance();

    void deposit(double amount);

    void withdraw(double amount) throws NotEnoughFundsException;

    void parseFeed(Map<String, String> map);
}
