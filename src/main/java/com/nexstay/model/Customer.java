package com.nexstay.model;

import java.io.Serializable;

public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    // ── Fields ────────────────────────────────────────────────────────────
    private int    customerId;
    private String name;
    private String contact;
    private int    roomNumber;
    private String checkInDate;
    private String checkOutDate;

    // ── Constructors ──────────────────────────────────────────────────────
    public Customer() {}

    public Customer(int customerId, String name, String contact,
                    int roomNumber, String checkInDate, String checkOutDate) {
        this.customerId   = customerId;
        this.name         = name;
        this.contact      = contact;
        this.roomNumber   = roomNumber;
        this.checkInDate  = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    // ── Getters ───────────────────────────────────────────────────────────
    public int    getCustomerId()   { return customerId; }
    public String getName()         { return name; }
    public String getContact()      { return contact; }
    public int    getRoomNumber()   { return roomNumber; }
    public String getCheckInDate()  { return checkInDate; }
    public String getCheckOutDate() { return checkOutDate; }

    // ── Setters ───────────────────────────────────────────────────────────
    public void setCustomerId(int customerId)     { this.customerId   = customerId; }
    public void setName(String name)              { this.name         = name; }
    public void setContact(String contact)        { this.contact      = contact; }
    public void setRoomNumber(int roomNumber)     { this.roomNumber   = roomNumber; }
    public void setCheckInDate(String checkInDate)   { this.checkInDate  = checkInDate; }
    public void setCheckOutDate(String checkOutDate) { this.checkOutDate = checkOutDate; }

    // ── toString ──────────────────────────────────────────────────────────
    @Override
    public String toString() {
        return "Customer{" +
               "customerId="    + customerId    +
               ", name='"       + name          + '\'' +
               ", contact='"    + contact       + '\'' +
               ", roomNumber="  + roomNumber    +
               ", checkInDate='"  + checkInDate  + '\'' +
               ", checkOutDate='" + checkOutDate + '\'' +
               '}';
    }
}
