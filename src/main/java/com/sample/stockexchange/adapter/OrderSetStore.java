package com.sample.stockexchange.adapter;

import java.util.HashMap;
import java.util.Map;

import com.sample.stockexchange.entity.BuyOrderSet;
import com.sample.stockexchange.entity.SellOrderSet;
import com.sample.stockexchange.entity.Stock;

/**
 * Contains an in-memory store for
 * {@link com.sample.stockexchange.entity.BuyOrderSet} mapped per
 * {@link com.sample.stockexchange.entity.Stock}
 */
public final class OrderSetStore implements IOrderSetStore {
    private final HashMap<Stock, BuyOrderSet> buyMap;
    private final HashMap<Stock, SellOrderSet> sellMap;

    private OrderSetStore() {
        buyMap = new HashMap<>();
        sellMap = new HashMap<>();
    }

    private static class LazyHolder {
        private static final OrderSetStore INSTANCE = new OrderSetStore();
    }

    public static OrderSetStore getInstance() {
        return LazyHolder.INSTANCE;
    }

    @Override
    public Map<Stock, BuyOrderSet> getBuyOrderStore() {
        return getInstance().buyMap;
    }

    @Override
    public Map<Stock, SellOrderSet> getSellOrderStore() {
        return getInstance().sellMap;
    }
}