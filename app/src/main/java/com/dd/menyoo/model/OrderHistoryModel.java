package com.dd.menyoo.model;

import java.util.Date;

/**
 * Created by Administrator on 01-Mar-16.
 */
public class OrderHistoryModel {
    String resName;
    int billId;
    String createdDate;

    public OrderHistoryModel(String resName, int billId, String createdDate) {
        this.resName = resName;
        this.billId = billId;
        this.createdDate = createdDate;
    }

    public String getResName() {
        return resName;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
