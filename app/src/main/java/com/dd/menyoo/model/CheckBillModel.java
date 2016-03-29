package com.dd.menyoo.model;


/**{"ItemState":false,"OrderId":96,"ItemId":1,"ItemQuaintity":4,"ItemCode":null,
        "ItemName":"Spicy broast Burger","ItemComments":"","UnitPrice":50,"OrderRequestStateId":1},**/


public class CheckBillModel {
    boolean itemState;
    String UserName;
    Integer orderId;
    Integer itemId;
    Integer quantity;
    String itemCode;
    String itemName ;
    String itemComment;
    double unitPrice;
    Integer orderRequestStateId;

    public boolean isItemState() {
        return itemState;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getItemCode() {
        return itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemComment() {
        return itemComment;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public Integer getOrderRequestStateId() {
        return orderRequestStateId;
    }

    public String getUserName() {
        return UserName;
    }

    public CheckBillModel(boolean itemState, Integer orderId,
                          Integer itemId, Integer quantity,
                          String itemCode, String itemName,
                          String itemComment, double unitPrice, Integer orderRequestStateId) {
        this.itemState = itemState;
        this.orderId = orderId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.itemComment = itemComment;
        this.unitPrice = unitPrice;
        this.orderRequestStateId = orderRequestStateId;
    }

    public CheckBillModel(boolean isItemDeleverred, String userName,
                          Integer orderId, Integer itemId, Integer quantity,
                          String itemCode, String itemName, String itemComment,
                          double unitPrice, Integer orderRequestStateId) {
        this.itemState = isItemDeleverred;
        UserName = userName;
        this.orderId = orderId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.itemComment = itemComment;
        this.unitPrice = unitPrice;
        this.orderRequestStateId = orderRequestStateId;
    }
}
