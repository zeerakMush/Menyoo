package com.dd.menyoo.model;

import android.view.Menu;

/**
 * Created by Administrator on 24-Feb-16.
 */
public class OrderModel extends BaseClassModel{
    MenuModel menu;
    String comment;
    int quantity;

    public MenuModel getMenu() {
        return menu;
    }

    public void setMenu(MenuModel menu) {
        this.menu = menu;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void addQuantity(int addQuantity){
        this.quantity+=addQuantity;
    }

    public OrderModel(MenuModel menu, String comment, int quantity) {
        this.menu = menu;
        this.comment = comment;
        this.quantity = quantity;
    }
}
