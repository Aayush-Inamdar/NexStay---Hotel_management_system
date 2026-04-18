package com.nexstay.model;

import java.io.Serializable;

public class Billing implements Serializable {

    private static final long serialVersionUID = 2L;   // bumped – new fields added

    // ── Fields ────────────────────────────────────────────────────────────
    private int    billId;
    private int    customerId;
    private String customerName;
    private String contact;
    private int    roomNumber;
    private String roomType;
    private double pricePerNight;
    private int    numberOfNights;
    private double totalAmount;
    private String checkInDate;
    private String checkOutDate;

    // ── Constructors ──────────────────────────────────────────────────────
    public Billing() {}

    public Billing(int billId, int customerId, String customerName, String contact,
                   int roomNumber, String roomType, double pricePerNight,
                   int numberOfNights, double totalAmount,
                   String checkInDate, String checkOutDate) {
        this.billId         = billId;
        this.customerId     = customerId;
        this.customerName   = customerName;
        this.contact        = contact;
        this.roomNumber     = roomNumber;
        this.roomType       = roomType;
        this.pricePerNight  = pricePerNight;
        this.numberOfNights = numberOfNights;
        this.totalAmount    = totalAmount;
        this.checkInDate    = checkInDate;
        this.checkOutDate   = checkOutDate;
    }

    // ── Getters ───────────────────────────────────────────────────────────
    public int    getBillId()         { return billId; }
    public int    getCustomerId()     { return customerId; }
    public String getCustomerName()   { return customerName; }
    public String getContact()        { return contact; }
    public int    getRoomNumber()     { return roomNumber; }
    public String getRoomType()       { return roomType; }
    public double getPricePerNight()  { return pricePerNight; }
    public int    getNumberOfNights() { return numberOfNights; }
    public double getTotalAmount()    { return totalAmount; }
    public String getCheckInDate()    { return checkInDate; }
    public String getCheckOutDate()   { return checkOutDate; }

    // ── Setters ───────────────────────────────────────────────────────────
    public void setBillId(int billId)                 { this.billId         = billId; }
    public void setCustomerId(int customerId)         { this.customerId     = customerId; }
    public void setCustomerName(String customerName)  { this.customerName   = customerName; }
    public void setContact(String contact)            { this.contact        = contact; }
    public void setRoomNumber(int roomNumber)         { this.roomNumber     = roomNumber; }
    public void setRoomType(String roomType)          { this.roomType       = roomType; }
    public void setPricePerNight(double price)        { this.pricePerNight  = price; }
    public void setNumberOfNights(int nights)         { this.numberOfNights = nights; }
    public void setTotalAmount(double totalAmount)    { this.totalAmount    = totalAmount; }
    public void setCheckInDate(String checkInDate)    { this.checkInDate    = checkInDate; }
    public void setCheckOutDate(String checkOutDate)  { this.checkOutDate   = checkOutDate; }

    // ── toString ──────────────────────────────────────────────────────────
    @Override
    public String toString() {
        return "Billing{" +
               "billId="           + billId         +
               ", customerId="     + customerId     +
               ", customerName='"  + customerName   + '\'' +
               ", contact='"       + contact        + '\'' +
               ", roomNumber="     + roomNumber     +
               ", roomType='"      + roomType       + '\'' +
               ", pricePerNight="  + pricePerNight  +
               ", numberOfNights=" + numberOfNights +
               ", totalAmount="    + totalAmount    +
               ", checkInDate='"   + checkInDate    + '\'' +
               ", checkOutDate='"  + checkOutDate   + '\'' +
               '}';
    }
}
