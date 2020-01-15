package com.sample.stockexchange.entity;

import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * BuyOrderSet is a set of {@link Order} of type BUY, sorted by time of order
 * placement.
 */
public class BuyOrderSet {
    private SortedSet<Order> orderSet;

    public BuyOrderSet() {
        Comparator<Order> comparator = new BuyOrderComparator();
        this.orderSet = new TreeSet<>(comparator);
    }

    public Set<Order> getOrderSet() {
        return orderSet;
    }
}

final class BuyOrderComparator implements Comparator<Order> {
    @Override
    public int compare(Order a, Order b) {
        if (a.getId().equals(b.getId())) {
            return 0; // invalid orders
        }

        int timeCompare = a.getTime().compareTo(b.getTime());
        if (timeCompare == 0) {
            // A tie on time, check Id
            return a.getId().compareTo(b.getId());
        }
        return timeCompare;
    }
}