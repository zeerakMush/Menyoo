package com.dd.menyoo.model;

/**
 * Created by Administrator on 24-Feb-16.
 */
public class OrderModelForPost {

    private int ItemId ;
    private int ItemQuaintity;
    private String ItemComments="";
    private double UnitPrice ;

    public int getItemId() {
        return ItemId;
    }

    public void setItemId(int itemId) {
        ItemId = itemId;
    }

    public int getItemQuaintity() {
        return ItemQuaintity;
    }

    public void setItemQuaintity(int itemQuaintity) {
        ItemQuaintity = itemQuaintity;
    }

    public String getItemComments() {
        return ItemComments;
    }

    public void setItemComments(String itemComments) {
        ItemComments = itemComments;
    }

    public double getUnitPrice() {
        return UnitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        UnitPrice = unitPrice;
    }
}
