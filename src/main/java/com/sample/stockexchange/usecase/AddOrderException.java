package com.sample.stockexchange.usecase;

/**
 * AddOrderException may be thrown while trying to add an order to
 * {@link com.sample.stockexchange.entity.BuyOrderSet}
 */
public class AddOrderException extends Exception {
    private static final long serialVersionUID = 6111010203853573098L;

    public AddOrderException(String msg) {
        super(msg);
    }
}