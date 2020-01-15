package com.sample.stockexchange.entity;

import java.math.BigDecimal;
import java.time.LocalTime;

public class Order {
    private final String id;
    private final LocalTime time;
    private final OrderType type;
    private int quantity;
    private final Stock stock;
    private final BigDecimal askingPrice;

    public Order(String id, LocalTime time, OrderType type, int quantity, Stock stock, BigDecimal askingPrice) {
        this.id = id;
        this.time = time;
        this.type = type;
        this.quantity = quantity;
        this.stock = stock;
        this.askingPrice = askingPrice;
    }

    public BigDecimal getAskingPrice() {
        return askingPrice;
    }

    public Stock getStock() {
        return stock;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public OrderType getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public LocalTime getTime() {
        return time;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Order) && (this.id.equals(((Order) obj).getId()));
    }
}