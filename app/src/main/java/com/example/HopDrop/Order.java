package com.example.HopDrop;

import java.io.Serializable;

public class Order implements Serializable {
    private final String orderID;
    private final String customer_name;
    private final String fromLocation;
    private final String dest;
    private final float fee;

    private final String notes;

    private int state;


    public Order(String customer_name, String fromLocation, String dest, float fee, String notes, String orderID) {
        this.customer_name = customer_name;
        this.fromLocation = fromLocation;
        this.dest = dest;
        this.fee = fee;
        this.notes = notes;
        state = 0;
        this.orderID = orderID;

    }

    public String getCustomer_name() { return customer_name; }
    public String getFromLocation() { return fromLocation; }
    public String getDest() { return dest; }
    public Float getFee() { return fee; }
    public String getNotes() { return notes; }
    public int getState() {return state;}

    public void setState(int new_state) {
        state = new_state;
    }
    public void updateState() {
        state++;
    }
    public String getOrderID() {
        return orderID;
    }
}

