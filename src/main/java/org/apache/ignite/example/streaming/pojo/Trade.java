/*
 *  Copyright (C) GridGain Systems. All Rights Reserved.
 *  _________        _____ __________________        _____
 *  __  ____/___________(_)______  /__  ____/______ ____(_)_______
 *  _  / __  __  ___/__  / _  __  / _  / __  _  __ `/__  / __  __ \
 *  / /_/ /  _  /    _  /  / /_/ /  / /_/ /  / /_/ / _  /  _  / / /
 *  \____/   /_/     /_/   \_,__/   \____/   \__,_/  /_/   /_/ /_/
 */

package org.apache.ignite.example.streaming.pojo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * POJO class that represents Transaction record.
 */
public class Trade implements Serializable {
    /** Trade ID. */
    private int tradeId;

    /** Related account number. */
    private int accountNumber;

    /** Trade amount. */
    private int amount;

    /**
     * Default constructor (required for deserialization).
     */
    @SuppressWarnings("unused")
    public Trade() {
    }

    /**
     * Constructor.
     *
     * @param tradeId Trade ID.
     * @param accountNumber Related account number.
     * @param amount Trade amount.
     */
    public Trade(int tradeId, int accountNumber, int amount) {
        this.tradeId = tradeId;
        this.accountNumber = accountNumber;
        this.amount = amount;
    }

    /** Gets trade ID. */
    public int getTradeId() {
        return tradeId;
    }

    /** Gets related account number. */
    public int getAccountNumber() {
        return accountNumber;
    }

    /** Gets trade amount. */
    public int getAmount() {
        return amount;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Trade{" +
                "tradeId=" + tradeId +
                ", accountNumber=" + accountNumber +
                ", amount=" + amount +
                '}';
    }

    /**
     * Deserializes {@link Trade} object from byte[].
     *
     * @param trade Byte array to deserialize.
     * @return Deserialized {@link Trade} object.
     */
    public static Trade parseTrade(byte[] trade) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(trade);
                ObjectInput in = new ObjectInputStream(bis)) {
            return (Trade) in.readObject();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Serializes {@link Trade} object into byte[].
     *
     * @param trade Object to serialize.
     * @return Serialized object as byte[].
     */
    public static byte[] toByteArray(Trade trade) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(trade);
            out.flush();

            return bos.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
