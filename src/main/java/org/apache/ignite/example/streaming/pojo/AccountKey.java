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
 * POJO class that represents Account key.
 */
public class AccountKey {
    /** Account number. */
    private int accountNumber;

    /**
     * Default constructor (required for deserialization).
     */
    @SuppressWarnings("unused")
    public AccountKey() {
    }

    /**
     * Constructor.
     *
     * @param accountNumber Account number.
     */
    public AccountKey(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "AccountKey{"
                + "accountNumber=" + accountNumber
                + '}';
    }
}
