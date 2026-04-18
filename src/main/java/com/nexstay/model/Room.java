package com.nexstay.model;

import java.io.Serializable;

public class Room implements Serializable {

    private static final long serialVersionUID = 1L;

    // ── Fields ────────────────────────────────────────────────────────────
    private int    roomNumber;
    private String roomType;      // Single / Double / Deluxe / Suite
    private double pricePerNight;
    private boolean isBooked;
    private int    maxGuests;

    // ── Constructors ──────────────────────────────────────────────────────
    public Room() {}

    public Room(int roomNumber, String roomType, double pricePerNight,
                boolean isBooked, int maxGuests) {
        this.roomNumber    = roomNumber;
        this.roomType      = roomType;
        this.pricePerNight = pricePerNight;
        this.isBooked      = isBooked;
        this.maxGuests     = maxGuests;
    }

    // ── Getters ───────────────────────────────────────────────────────────
    public int getRoomNumber()    { return roomNumber; }
    public String getRoomType()   { return roomType; }
    public double getPricePerNight() { return pricePerNight; }
    public boolean isBooked()     { return isBooked; }
    public int getMaxGuests()     { return maxGuests; }

    // ── Setters ───────────────────────────────────────────────────────────
    public void setRoomNumber(int roomNumber)       { this.roomNumber    = roomNumber; }
    public void setRoomType(String roomType)        { this.roomType      = roomType; }
    public void setPricePerNight(double price)      { this.pricePerNight = price; }
    public void setBooked(boolean booked)           { this.isBooked      = booked; }
    public void setMaxGuests(int maxGuests)         { this.maxGuests     = maxGuests; }

    // ── toString ──────────────────────────────────────────────────────────
    @Override
    public String toString() {
        return "Room{" +
               "roomNumber="    + roomNumber    +
               ", roomType='"   + roomType      + '\'' +
               ", pricePerNight=" + pricePerNight +
               ", isBooked="    + isBooked      +
               ", maxGuests="   + maxGuests     +
               '}';
    }
}
