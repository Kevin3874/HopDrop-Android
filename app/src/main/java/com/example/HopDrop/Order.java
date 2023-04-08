package com.example.HopDrop;

import java.io.Serializable;

public class Order implements Serializable {
    private String customer_name;
    private String src;
    private String dest;
    private float fee;

    private String notes;

    public Order(String customer_name, String src, String dest, float fee, String notes) {
        this.customer_name = customer_name;
        this.src = src;
        this.dest = dest;
        this.fee = fee;
        this.notes = notes;
    }

    public String getCustomer_name() { return customer_name; }
    public String getSrc() { return src; }
    public String getDest() { return dest; }
    public Float getFee() { return fee; }
    public String getNotes() { return notes; }
}

