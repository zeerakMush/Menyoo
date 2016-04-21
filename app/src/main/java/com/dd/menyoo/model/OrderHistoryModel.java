package com.dd.menyoo.model;

import java.util.Date;

/**
 * Created by Administrator on 01-Mar-16.
 */
public class OrderHistoryModel {
    String resName;
    int billId;
    String createdDate;
    private double gst;
    private double seviceTax;

    public OrderHistoryModel(String resName, int billId, String createdDate,double gst,double seviceTax) {
        this.resName = resName;
        this.billId = billId;
        this.createdDate = createdDate;
        this.gst = gst;
        this.seviceTax = seviceTax;
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

    public double getGst() {
        return gst;
    }

    public double getSeviceTax() {
        return seviceTax;
    }
}
