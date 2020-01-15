package com.sample.stockexchange.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;

import com.sample.stockexchange.adapter.OrderSetStore;
import com.sample.stockexchange.adapter.TransactionStore;
import com.sample.stockexchange.entity.Order;
import com.sample.stockexchange.usecase.OrderUsecasesRepo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CLIControllerTest {
    private CLIController controller;

    @BeforeEach
    void instantiateController() {
        OrderUsecasesRepo repo = new OrderUsecasesRepo(OrderSetStore.getInstance(), TransactionStore.getInstance());
        this.controller = new CLIController(repo);
    }

    @Test
    void parseValidInputFormat() {
        Order o = controller.parse("   #9   10:02 BAC buy 150 242.70     ");

        assertEquals("#9", o.getId());
        assertEquals("10:02", o.getTime().toString());
        assertEquals("BAC", o.getStock().getName());
        assertEquals("buy", o.getType().name().toLowerCase());
        assertEquals(150, o.getQuantity());
        assertEquals(new BigDecimal("242.70"), o.getAskingPrice());
    }

    @Test
    void parseInvalidInputFormat() {
        assertThrows(NoSuchElementException.class, () -> {
            controller.parse("#9 10:02 BAC buy");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            controller.parse("#9 10:02 BAC 1234");
        });

        assertThrows(DateTimeParseException.class, () -> {
            controller.parse("#9 BAC 1234");
        });

        assertThrows(NumberFormatException.class, () -> {
            controller.parse("#9 12:12 BAC buy asdf 1234");
        });
    }

    @Test
    void parseEmptyInput() {
        assertThrows(NoSuchElementException.class, () -> {
            controller.parse("");
        });
    }
}