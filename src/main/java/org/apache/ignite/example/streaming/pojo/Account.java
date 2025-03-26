/*
 *  Copyright (C) GridGain Systems. All Rights Reserved.
 *  _________        _____ __________________        _____
 *  __  ____/___________(_)______  /__  ____/______ ____(_)_______
 *  _  / __  __  ___/__  / _  __  / _  / __  _  __ `/__  / __  __ \
 *  / /_/ /  _  /    _  /  / /_/ /  / /_/ /  / /_/ / _  /  _  / / /
 *  \____/   /_/     /_/   \_,__/   \____/   \__,_/  /_/   /_/ /_/
 */

package org.apache.ignite.example.streaming.pojo;

/**
 * POJO class that represents Account record.
 */
public class Account {
    /** Account number. */
    private int accountNumber;

    /** Name. */
    private String name;

    /** Balance. */
    private long balance;

    /** Is account active. */
    private boolean active;

    /**
     * Default constructor (required for deserialization).
     */
    @SuppressWarnings("unused")
    public Account() {
    }

    /**
     * Constructor.
     *
     * @param accountNumber Account number.
     */
    @SuppressWarnings("unused")
    public Account(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Constructor.
     *
     * @param accountNumber Account number.
     * @param name Name.
     * @param balance Balance.
     * @param active Is account active.
     */
    public Account(int accountNumber, String name, long balance, boolean active) {
        this.accountNumber = accountNumber;
        this.name = name;
        this.balance = balance;
        this.active = active;
    }

    /** Gets account number. */
    public int getAccountNumber() {
        return accountNumber;
    }

    /** Gets current balance. */
    public long getBalance() {
        return balance;
    }

    /** Sets current balance. */
    public void setBalance(long balance) {
        this.balance = balance;
    }

    /** Is account active. */
    public boolean isActive() {
        return active;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Account{" +
                "accountNumber=" + accountNumber +
                ", name='" + name + '\'' +
                ", balance=" + balance +
                ", active=" + active +
                '}';
    }
}
