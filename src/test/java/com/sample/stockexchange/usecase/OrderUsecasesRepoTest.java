
package com.sample.stockexchange.usecase;

import static com.sample.stockexchange.entity.OrderType.BUY;
import static com.sample.stockexchange.entity.OrderType.SELL;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sample.stockexchange.adapter.OrderSetStore;
import com.sample.stockexchange.adapter.TransactionStore;
import com.sample.stockexchange.entity.BuyOrderSet;
import com.sample.stockexchange.entity.Order;
import com.sample.stockexchange.entity.OrderEntry;
import com.sample.stockexchange.entity.SellOrderSet;
import com.sample.stockexchange.entity.Stock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OrderUsecasesRepoTest {

    private OrderUsecasesRepo repo;
    private Map<Stock, BuyOrderSet> buys;
    private Map<Stock, SellOrderSet> sells;

    @BeforeEach
    void instantiateRepo() {
        this.repo = new OrderUsecasesRepo(OrderSetStore.getInstance(), TransactionStore.getInstance());
        this.buys = OrderSetStore.getInstance().getBuyOrderStore();
        this.sells = OrderSetStore.getInstance().getSellOrderStore();
    }

    @AfterEach
    void cleanup() {
        this.repo.cleanup();
    }

    @Test
    void addValidOrders() {
        Stock test = new Stock("test");

        List<Order> orders = new ArrayList<>();
        Order o = new Order("#1", LocalTime.parse("10:00:00"), BUY, 100, test, new BigDecimal("10.01"));
        orders.add(o);

        Order o1 = new Order("#2", LocalTime.parse("09:01:00"), SELL, 100, test, new BigDecimal("10.01"));
        orders.add(o1);

        assertDoesNotThrow(() -> {
            repo.addOrders(orders);
        });

        assertEquals(buys.get(test).getOrderSet().size(), 1);
        assertEquals(sells.get(test).getOrderSet().size(), 1);
        assertTrue(sells.get(test).getOrderSet().contains(o1));
        assertTrue(buys.get(test).getOrderSet().contains(o));
    }

    @Test
    void addInvalidOrders() {
        List<Order> orders = new ArrayList<>();
        Order o = new Order("#1", LocalTime.parse("10:00:00"), BUY, 100, null, new BigDecimal("10.01"));
        orders.add(o);

        assertThrows(AddOrderException.class, () -> {
            repo.addOrders(orders);
        });

        assertTrue(buys.isEmpty());
        assertTrue(sells.isEmpty());
    }

    @Test
    void addDuplicateOrders() {
        Stock test = new Stock("test");

        List<Order> orders = new ArrayList<>();
        Order o = new Order("#1", LocalTime.parse("10:00:00"), BUY, 100, test, new BigDecimal("10.01"));
        orders.add(o);

        Order o1 = new Order("#1", LocalTime.parse("09:01:00"), BUY, 100, test, new BigDecimal("10.01"));
        orders.add(o1);

        assertThrows(AddOrderException.class, () -> {
            repo.addOrders(orders);
        });

        assertEquals(buys.get(test).getOrderSet().size(), 1);
        assertNull(sells.get(test));
    }

    @Test
    void addEmptyOrder() {
        List<Order> orders = new ArrayList<>();

        assertDoesNotThrow(() -> {
            repo.addOrders(orders);
        });

        assertTrue(buys.isEmpty());
        assertTrue(sells.isEmpty());
    }

    @Test
    void testCleanup() {
        Stock test = new Stock("test");
        BuyOrderSet buy = new BuyOrderSet();
        buys.put(test, buy);

        Order o = new Order("#1", LocalTime.parse("10:00:00"), BUY, 100, test, new BigDecimal("10.01"));
        buy.getOrderSet().add(o);

        SellOrderSet sell = new SellOrderSet();
        sells.put(test, sell);

        Order s = new Order("#2", LocalTime.parse("09:01:00"), SELL, 100, test, new BigDecimal("10.01"));
        sell.getOrderSet().add(s);

        repo.cleanup();

        assertTrue(buys.isEmpty());
        assertTrue(sells.isEmpty());
        assertTrue(TransactionStore.getInstance().getOrderEntries().isEmpty());
    }

    @Test
    void processEmptyOrders() {
        List<OrderEntry> result = repo.processOrders();
        assertEquals(result.size(), 0);
        assertTrue(result.isEmpty());
    }

    @Test
    void processBuySellComplete() {
        Stock test = new Stock("test");
        BuyOrderSet buy = new BuyOrderSet();
        buys.put(test, buy);

        Order o = new Order("#1", LocalTime.parse("10:00:00"), BUY, 100, test, new BigDecimal("10.01"));
        buy.getOrderSet().add(o);

        SellOrderSet sell = new SellOrderSet();
        sells.put(test, sell);

        Order s = new Order("#2", LocalTime.parse("09:01:00"), SELL, 100, test, new BigDecimal("10.01"));
        sell.getOrderSet().add(s);

        List<OrderEntry> result = repo.processOrders();

        assertTrue(result.size() > 0);
        assertEquals(result.get(0).getParty().getId(), "#2");
        assertEquals(result.get(0).getCounterParty().getId(), "#1");
        assertEquals(result.get(0).getExecutionPrice().compareTo(new BigDecimal("10.01")), 0);
        assertEquals(result.get(0).getQuantity(), 100);
    }

    @Test
    void processBuySellSplitOrders() {
        Stock test = new Stock("test");
        BuyOrderSet buy = new BuyOrderSet();
        buys.put(test, buy);

        Order o = new Order("#1", LocalTime.parse("10:00:00"), BUY, 100, test, new BigDecimal("10.01"));
        buy.getOrderSet().add(o);

        SellOrderSet sell = new SellOrderSet();
        sells.put(test, sell);

        Order s = new Order("#2", LocalTime.parse("09:01:00"), SELL, 90, test, new BigDecimal("10.01"));
        sell.getOrderSet().add(s);
        Order s2 = new Order("#3", LocalTime.parse("09:11:00"), SELL, 100, test, new BigDecimal("10.01"));
        sell.getOrderSet().add(s2);

        List<OrderEntry> result = repo.processOrders();

        assertTrue(result.size() == 2);
        assertEquals(result.get(0).getParty().getId(), "#2");
        assertEquals(result.get(0).getCounterParty().getId(), "#1");
        assertEquals(result.get(0).getExecutionPrice().compareTo(new BigDecimal("10.01")), 0);
        assertEquals(result.get(0).getQuantity(), 90);

        assertEquals(result.get(1).getParty().getId(), "#3");
        assertEquals(result.get(1).getCounterParty().getId(), "#1");
        assertEquals(result.get(1).getExecutionPrice().compareTo(new BigDecimal("10.01")), 0);
        assertEquals(result.get(1).getQuantity(), 10);
    }

    @Test
    void processBuyOrderPriority() {
        Stock test = new Stock("test");
        BuyOrderSet buy = new BuyOrderSet();
        buys.put(test, buy);

        Order o = new Order("#1", LocalTime.parse("10:00:00"), BUY, 100, test, new BigDecimal("10.01"));
        buy.getOrderSet().add(o);
        Order o1 = new Order("#2", LocalTime.parse("09:59:00"), BUY, 100, test, new BigDecimal("10.01"));
        buy.getOrderSet().add(o1);

        SellOrderSet sell = new SellOrderSet();
        sells.put(test, sell);

        Order s = new Order("#3", LocalTime.parse("10:01:00"), SELL, 90, test, new BigDecimal("10.01"));
        sell.getOrderSet().add(s);

        List<OrderEntry> result = repo.processOrders();

        assertTrue(result.size() == 1);
        assertEquals(result.get(0).getParty().getId(), "#3");
        assertEquals(result.get(0).getCounterParty().getId(), "#2");
        assertEquals(result.get(0).getExecutionPrice().compareTo(new BigDecimal("10.01")), 0);
        assertEquals(result.get(0).getQuantity(), 90);
    }

    @Test
    void processSellOrderPriority() {
        Stock test = new Stock("test");
        BuyOrderSet buy = new BuyOrderSet();
        buys.put(test, buy);

        Order o = new Order("#1", LocalTime.parse("10:00:00"), BUY, 100, test, new BigDecimal("10.01"));
        buy.getOrderSet().add(o);

        SellOrderSet sell = new SellOrderSet();
        sells.put(test, sell);

        Order s = new Order("#2", LocalTime.parse("09:01:00"), SELL, 90, test, new BigDecimal("9.59"));
        sell.getOrderSet().add(s);
        Order s2 = new Order("#3", LocalTime.parse("09:11:00"), SELL, 90, test, new BigDecimal("9.58"));
        sell.getOrderSet().add(s2);

        List<OrderEntry> result = repo.processOrders();

        assertTrue(result.size() == 2);
        assertEquals(result.get(0).getParty().getId(), "#3");
        assertEquals(result.get(0).getCounterParty().getId(), "#1");
        assertEquals(result.get(0).getExecutionPrice().compareTo(new BigDecimal("9.58")), 0);
        assertEquals(result.get(0).getQuantity(), 90);

        assertEquals(result.get(1).getParty().getId(), "#2");
        assertEquals(result.get(1).getCounterParty().getId(), "#1");
        assertEquals(result.get(1).getExecutionPrice().compareTo(new BigDecimal("9.59")), 0);
        assertEquals(result.get(1).getQuantity(), 10);
    }

    @Test
    void processMultiStockOrders() {
        Stock test = new Stock("test");
        Stock test1 = new Stock("test1");

        BuyOrderSet buy = new BuyOrderSet();
        BuyOrderSet buy1 = new BuyOrderSet();
        buys.put(test, buy);
        buys.put(test1, buy1);

        SellOrderSet sell = new SellOrderSet();
        sells.put(test, sell);
        SellOrderSet sell1 = new SellOrderSet();
        sells.put(test1, sell1);

        Order o = new Order("#1", LocalTime.parse("10:00:00"), BUY, 100, test, new BigDecimal("10.01"));
        buy.getOrderSet().add(o);

        Order o1 = new Order("#1a", LocalTime.parse("10:00:00"), BUY, 100, test1, new BigDecimal("10.01"));
        buy1.getOrderSet().add(o1);

        Order s = new Order("#2", LocalTime.parse("09:01:00"), SELL, 90, test, new BigDecimal("10.01"));
        sell.getOrderSet().add(s);
        Order s1 = new Order("#2a", LocalTime.parse("09:01:00"), SELL, 100, test1, new BigDecimal("9.01"));
        sell1.getOrderSet().add(s1);

        List<OrderEntry> result = repo.processOrders();

        assertTrue(result.size() == 2);
        assertEquals(result.get(0).getParty().getId(), "#2");
        assertEquals(result.get(0).getCounterParty().getId(), "#1");
        assertEquals(result.get(0).getExecutionPrice().compareTo(new BigDecimal("10.01")), 0);
        assertEquals(result.get(0).getQuantity(), 90);

        assertEquals(result.get(1).getParty().getId(), "#2a");
        assertEquals(result.get(1).getCounterParty().getId(), "#1a");
        assertEquals(result.get(1).getExecutionPrice().compareTo(new BigDecimal("9.01")), 0);
        assertEquals(result.get(1).getQuantity(), 100);
    }
}