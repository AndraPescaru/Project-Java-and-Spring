package com.luxoft.bankapp.exceptions;

public class OverdraftLimitExceededException extends NotEnoughFundsException {

    private static final long serialVersionUID = 1L;
    private String account;

    public OverdraftLimitExceededException(String account, double amount) {
        super(amount);
        this.account = account;
    }

    @Override
    public String getMessage() {
        return "Overdraft Limit exceeded on " + this.account + " amount: " + amount;

    }
}
