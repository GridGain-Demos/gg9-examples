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
 * POJO class that represents Account value.
 */
public class AccountValue {
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
    public AccountValue() {
    }

    /**
     * Constructor.
     *
     * @param name Name.
     * @param balance Balance.
     * @param active Is account active.
     */
    public AccountValue(String name, long balance, boolean active) {
        this.name = name;
        this.balance = balance;
        this.active = active;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "AccountValue{"
                + "name='" + name + '\''
                + ", balance=" + balance
                + ", active=" + active
                + '}';
    }
}
