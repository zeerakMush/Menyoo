package com.dd.menyoo.model;

/**
 * Created by Administrator on 11-Mar-16.
 */
public class GuestRequestModel extends BaseClassModel {

    int TableNumber ;
    int BillId ;
    String UserName;
    int RequestId;

    public int getTableNumber() {
        return TableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.TableNumber = tableNumber;
    }

    public int getBillId() {
        return BillId;
    }

    public void setBillId(int billId) {
        this.BillId = billId;
    }

    public String getname() {
        return UserName;
    }

    public void setname(String hostName) {
        this.UserName = hostName;
    }

    public int getRequestId() {
        return RequestId;
    }

    public void setRequestId(int requestId) {
        this.RequestId = requestId;
    }

    public GuestRequestModel(int tableNumber, int billId, String hostName, int requestId) {
        this.TableNumber = tableNumber;
        this.BillId = billId;
        this.UserName = hostName;
        this.RequestId = requestId;
    }
}
