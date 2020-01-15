package com.sample.stockexchange.entity;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * OrderEntry contains a executed list of valid pairs of {@link Order}
 */
public class OrderEntry {
    private final UUID id;
    private final Order party;
    private final Order counterParty;
    private final int quantity;
    private final BigDecimal executionPrice;

    public OrderEntry(Order party, Order counterParty, int quantity, BigDecimal executionPrice) {
        this.id = UUID.randomUUID();
        this.party = party;
        this.counterParty = counterParty;
        this.quantity = quantity;
        this.executionPrice = executionPrice;
    }

    public UUID getId() {
        return id;
    }

    public Order getParty() {
        return this.party;
    }

    public Order getCounterParty() {
        return this.counterParty;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public BigDecimal getExecutionPrice() {
        return this.executionPrice;
    }
}