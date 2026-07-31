package com.tradestock.model;

public class Trader {
    private int id;
    private String name;
    private String username;
    private String password;
    private int age;
    private double balance;

    public Trader() {}

    public Trader(int id, String name, String username, String password, int age, double balance) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.age = age;
        this.balance = balance;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
}
