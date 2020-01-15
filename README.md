# Sample Stock Exchange

A toy implementation of a stock exchange console app.

# Problem definition

Implement an order matching system for a stock exchange.

Traders place Buy or Sell orders for a stock indicating the quantity and price.

These orders get entered into the exchange’s order-book and remain there until they are matched or until the trading day ends.

The exchange follows a FirstInFirstOut Price-Time order-matching rule, which states that: “The first order in the order-book at a price level is the first order matched. All orders at the same price level are filled according to time priority”.

The exchange works like a market, lower selling prices and higher buying prices get priority.

A trade is executed when a buy price is greater than or equal to a sell price. The trade is recorded at the price of the sell order regardless of the price of the buy order.

Write a program that accepts a list of orders from standard input and writes out to standard output the trades that were executed as shown below.

e.g. The following input (format:<order-id> <time> <stock> <buy/sell> <qty> <price>):

```
#1 09:45 BAC sell 100 240.10
#2 09:45 BAC sell 90 237.45
#3 09:47 BAC buy 80 238.10
#5 09:48 BAC sell 220 241.50
#6 09:49 BAC buy 50 238.50
#7 09:52 TCS buy 10 1001.10
#8 10:01 BAC sell 20 240.10
#9 10:02 BAC buy 150 242.70
```

Should produce the following output to the console: (format:<sell-order-id> <qty> <sell-price> <buy-order-id>):


```
#2 80 237.45 #3
#2 10 237.45 #6
#1 100 240.10 #9
#8 20 240.10 #9
#5 30 241.50 #9
```

# Design Notes

* This is a console app following a simple variation of [clean architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) pattern. 
* This is a single module project, since multi-modules don't make sense for this simple single app(modules are not going to be shared).
* Each executed order is registered in a in-memory order entry system with party/counterparty. 
* Each order is identified by it's stock and type(BUY/SELL) and sorted and stored accordingly. 
* Extending functionality such as finding pending orders or persistence should be simple. Adding a new order type may not be simple as new entities might be needed.
* BigDecimal is used for computing prices, since double/floats don't produce reproducible results. 

## What can be better?

* Concurrency support: this app is not meant to be executed across threads/processes/systems
* Persistence: this app only uses in-memory structures
* Better order placement system: single instance console is very limited :) order placement system should be more robust, API/RPC based etc. Order execution could also be either in batches(backed by persistent queues) or by on-demand basis. 
* Performance optimization: depends on what the performance criteria is? Faster order completion, better memory/CPU usage?
* Better test coverage 
    * for controllers/adapters: since this is app deals with CLI for input and in-memory structures for storage, test coverage was not added
    * more comprehensive test coverage: race conditions(based on time, price), benchmarking/performance measuring?

# Installation

### Pre-requisites 
* Java >= 1.8
* Gradle >= 2.2, gradle binary needs to be added to the path.
* internet connection to download dependencies

### Usage

1. Go to the unzipped folder root and type `gradle run`
2. To create a zip file of the source code, run `gradle assemble`. Source zip folder will be found at `build/distributions/StockExchangeApp-src.zip`
3. To run tests, type `gradle test`

