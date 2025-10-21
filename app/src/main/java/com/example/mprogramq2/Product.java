package com.example.mprogramq2;

public class Product {
    private int id;
    private String name;
    private String serial;
    private String description;
    private double price;

    public Product() {}

    public Product(int id, String name, String serial, String description, double price) {
        this.id = id;
        this.name = name;
        this.serial = serial;
        this.description = description;
        this.price = price;
    }

    public Product(String name, String serial, String description, double price) {
        this.name = name;
        this.serial = serial;
        this.description = description;
        this.price = price;
    }

    // getters & setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSerial() { return serial; }
    public void setSerial(String serial) { this.serial = serial; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}