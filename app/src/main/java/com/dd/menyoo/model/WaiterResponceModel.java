package com.dd.menyoo.model;

/**
 * Created by Administrator on 03-Mar-16.
 */
public class WaiterResponceModel {
    int UserID;
    int BillID;
    int TableID;
    int RequestID;

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public int getBillID() {
        return BillID;
    }

    public void setBillID(int billID) {
        BillID = billID;
    }

    public int getTableID() {
        return TableID;
    }

    public void setTableID(int tableID) {
        TableID = tableID;
    }

    public int getRequestID() {
        return RequestID;
    }

    public void setRequestID(int requestID) {
        RequestID = requestID;
    }

    public WaiterResponceModel(int userID, int billID, int tableID, int requestID) {
        UserID = userID;
        BillID = billID;
        TableID = tableID;
        RequestID = requestID;
    }
    public WaiterResponceModel(){

    }
}
