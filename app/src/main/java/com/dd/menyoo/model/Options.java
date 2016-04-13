package com.dd.menyoo.model;

/**
 * Created by Administrator on 4/12/2016.
 */
public class Options {
    private String name;
    private double price;
    private int id;

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getID(){
        return id;
    }

    public Options(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public Options(String name, double price, int id) {
        this.name = name;
        this.price = price;
        this.id = id;
    }
}
