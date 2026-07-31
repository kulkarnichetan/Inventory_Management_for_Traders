package com.tradestock.model;

public class Order {
    private int id;
    private int traderId;
    private int stockId;
    private String orderType; // BUY or SELL
    private int quantity;
    private double priceAtOrder;
    private String timestamp;

    // Joined/display fields
    private String stockName;
    private String traderUsername;

    public Order() {}

    public Order(int id, int traderId, int stockId, String orderType, int quantity, double priceAtOrder, String timestamp) {
        this.id = id;
        this.traderId = traderId;
        this.stockId = stockId;
        this.orderType = orderType;
        this.quantity = quantity;
        this.priceAtOrder = priceAtOrder;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTraderId() { return traderId; }
    public void setTraderId(int traderId) { this.traderId = traderId; }

    public int getStockId() { return stockId; }
    public void setStockId(int stockId) { this.stockId = stockId; }

    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPriceAtOrder() { return priceAtOrder; }
    public void setPriceAtOrder(double priceAtOrder) { this.priceAtOrder = priceAtOrder; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getStockName() { return stockName; }
    public void setStockName(String stockName) { this.stockName = stockName; }

    public String getTraderUsername() { return traderUsername; }
    public void setTraderUsername(String traderUsername) { this.traderUsername = traderUsername; }

    public double getTotalAmount() {
        return quantity * priceAtOrder;
    }
}
