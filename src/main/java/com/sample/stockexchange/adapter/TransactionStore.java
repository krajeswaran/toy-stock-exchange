package com.sample.stockexchange.adapter;

import java.util.ArrayList;
import java.util.List;

import com.sample.stockexchange.entity.OrderEntry;

/**
 * Contains an in-memory store for
 * {@link com.sample.stockexchange.entity.OrderEntry}
 */
public final class TransactionStore implements ITransactionStore {
    private final List<OrderEntry> orderEntries;

    private TransactionStore() {
        orderEntries = new ArrayList<>();
    }

    private static class LazyHolder {
        private static final TransactionStore INSTANCE = new TransactionStore();
    }

    public static TransactionStore getInstance() {
        return LazyHolder.INSTANCE;
    }

    @Override
    public List<OrderEntry> getOrderEntries() {
        return getInstance().orderEntries;
    }
}