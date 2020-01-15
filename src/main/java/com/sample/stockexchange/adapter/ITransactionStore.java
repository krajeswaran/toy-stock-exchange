package com.sample.stockexchange.adapter;

import java.util.List;

import com.sample.stockexchange.entity.OrderEntry;

/**
 * Interface for persisting order book containing executed orders. For
 * simplicity's sake, just a list for this implementation
 * 
 */
public interface ITransactionStore {
    public List<OrderEntry> getOrderEntries();
}