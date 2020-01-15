package com.sample.stockexchange.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

import com.google.common.base.Splitter;
import com.sample.stockexchange.entity.Order;
import com.sample.stockexchange.entity.OrderEntry;
import com.sample.stockexchange.entity.OrderType;
import com.sample.stockexchange.entity.Stock;
import com.sample.stockexchange.usecase.AddOrderException;
import com.sample.stockexchange.usecase.OrderUsecasesRepo;

public class CLIController {
    private final OrderUsecasesRepo repo;

    public CLIController(OrderUsecasesRepo repo) {
        this.repo = repo;
    }

    /**
     * parse parsers a single order from a string. format:<order-id> <time> <stock>
     * <buy/sell> <qty> <price>
     */
    public Order parse(String orderLine) {
        Splitter spaceSplitter = Splitter.on(' ').omitEmptyStrings().trimResults();
        Iterator<String> tokenItr = spaceSplitter.split(orderLine).iterator();

        String orderId = tokenItr.next();

        String timeStr = tokenItr.next();
        LocalTime orderTime = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault()));

        String stockName = tokenItr.next();
        Stock stock = new Stock(stockName);

        String typeStr = tokenItr.next();
        OrderType type = OrderType.valueOf(typeStr.toUpperCase());

        int quantity = Integer.parseInt(tokenItr.next());
        BigDecimal price = new BigDecimal(tokenItr.next());

        return new Order(orderId, orderTime, type, quantity, stock, price);
    }

    public List<Order> readFromCLI() {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        List<Order> orders = new ArrayList<>();
        try {
            String line = null;
            while (!(line = input.readLine()).equals("EOF")) {
                orders.add(parse(line));
            }
        } catch (DateTimeParseException | NoSuchElementException | NumberFormatException e) {
            System.out.println("Invalid input format! Exception: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Failed to get input! Exception: " + e.getMessage());
        }

        return orders;
    }

    public void writeToCLI(List<OrderEntry> entries) {
        entries.forEach((entry) -> {
            String output = String.format("%s %d %.2f %s", entry.getParty().getId(), entry.getQuantity(),
                    entry.getExecutionPrice(), entry.getCounterParty().getId());
            System.out.println(output);
        });
    }

    public void run() {
        repo.cleanup();
        System.out.println(
                "Enter orders below in this format: <order-id> <time> <stock> <buy/sell> <qty> <price>,  type EOF to finish input");

        try {
            repo.addOrders(readFromCLI());

            writeToCLI(repo.processOrders());
        } catch (AddOrderException e) {
            System.out.println("Invalid input orders! Exception: " + e.getMessage());
        }
    }
}