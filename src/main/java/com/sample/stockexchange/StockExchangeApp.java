package com.sample.stockexchange;

import com.sample.stockexchange.adapter.OrderSetStore;
import com.sample.stockexchange.adapter.TransactionStore;
import com.sample.stockexchange.controller.CLIController;
import com.sample.stockexchange.usecase.OrderUsecasesRepo;

public class StockExchangeApp {
    public static void main(String[] args) {
        // initialize usecase repo
        OrderUsecasesRepo repo = new OrderUsecasesRepo(OrderSetStore.getInstance(), TransactionStore.getInstance());

        // initialize controller
        CLIController controller = new CLIController(repo);

        // execute
        controller.run();
    }
}
