package com.dd.menyoo.model;

import java.util.ArrayList;

/**
 * Created by Administrator on 24-Feb-16.
 */
public class OrderModelForPost {

    private int ItemId ;
    private int ItemQuaintity;
    private String ItemComments="";
    private double UnitPrice ;
    private ArrayList<Integer> optionids;

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

    public ArrayList<Integer> getVaraintIds() {
        return optionids;
    }

    public void setVaraintIds(ArrayList<Integer> varaintIds) {
        this.optionids = varaintIds;
    }
}
