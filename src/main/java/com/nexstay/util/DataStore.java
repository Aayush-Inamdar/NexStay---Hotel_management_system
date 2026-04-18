package com.nexstay.util;

import com.nexstay.model.Billing;
import com.nexstay.model.Customer;
import com.nexstay.model.Room;

import java.io.*;
import java.util.ArrayList;

/**
 * DataStore – centralised persistence layer for NexStay.
 *
 * Each list is saved to / loaded from a separate binary .dat file
 * located in the working directory.  On the very first launch (when
 * rooms.dat does not yet exist) a set of 6 sample rooms is created
 * so the app is immediately usable.
 */
public class DataStore {

    // ── File paths ────────────────────────────────────────────────────────
    private static final String ROOMS_FILE     = "rooms.dat";
    private static final String CUSTOMERS_FILE = "customers.dat";
    private static final String BILLING_FILE   = "billing.dat";

    // ── Rooms ──────────────────────────────────────────────────────────────

    /**
     * Saves the given list of rooms to disk.
     *
     * @param rooms list to persist
     */
    public static void saveRooms(ArrayList<Room> rooms) {
        saveObject(rooms, ROOMS_FILE);
    }

    /**
     * Loads rooms from disk.  If no file exists the 6 sample rooms are
     * created, persisted, and returned.
     *
     * @return list of rooms
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<Room> loadRooms() {
        File file = new File(ROOMS_FILE);
        if (!file.exists()) {
            ArrayList<Room> sample = createSampleRooms();
            saveRooms(sample);
            return sample;
        }
        Object obj = loadObject(ROOMS_FILE);
        return (obj instanceof ArrayList<?>) ? (ArrayList<Room>) obj : new ArrayList<>();
    }

    // ── Customers ──────────────────────────────────────────────────────────

    /**
     * Saves the given list of customers to disk.
     *
     * @param customers list to persist
     */
    public static void saveCustomers(ArrayList<Customer> customers) {
        saveObject(customers, CUSTOMERS_FILE);
    }

    /**
     * Loads customers from disk, or returns an empty list if no file exists.
     *
     * @return list of customers
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<Customer> loadCustomers() {
        Object obj = loadObject(CUSTOMERS_FILE);
        return (obj instanceof ArrayList<?>) ? (ArrayList<Customer>) obj : new ArrayList<>();
    }

    // ── Billing ────────────────────────────────────────────────────────────

    /**
     * Saves the given list of billing records to disk.
     *
     * @param billings list to persist
     */
    public static void saveBillings(ArrayList<Billing> billings) {
        saveObject(billings, BILLING_FILE);
    }

    /**
     * Loads billing records from disk, or returns an empty list.
     *
     * @return list of billing records
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<Billing> loadBillings() {
        Object obj = loadObject(BILLING_FILE);
        return (obj instanceof ArrayList<?>) ? (ArrayList<Billing>) obj : new ArrayList<>();
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    /** Serializes any object to the given file path. */
    private static void saveObject(Object obj, String path) {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(obj);
        } catch (IOException e) {
            System.err.println("[DataStore] Failed to save " + path + ": " + e.getMessage());
        }
    }

    /** Deserializes an object from the given file path; returns null on failure. */
    private static Object loadObject(String path) {
        File file = new File(path);
        if (!file.exists()) return null;
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(file))) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[DataStore] Failed to load " + path + ": " + e.getMessage());
            return null;
        }
    }

    // ── Sample data ────────────────────────────────────────────────────────

    /**
     * Builds 6 pre-configured sample rooms covering all four room types.
     *
     * Room layout:
     *  101 – Single,  1 guest,  ₹1 500 / night
     *  102 – Single,  1 guest,  ₹1 500 / night
     *  201 – Double,  2 guests, ₹2 500 / night
     *  202 – Double,  2 guests, ₹2 500 / night
     *  301 – Deluxe,  3 guests, ₹4 000 / night
     *  401 – Suite,   4 guests, ₹7 500 / night
     */
    private static ArrayList<Room> createSampleRooms() {
        ArrayList<Room> rooms = new ArrayList<>();
        rooms.add(new Room(101, "Single",  1500.0, false, 1));
        rooms.add(new Room(102, "Single",  1500.0, false, 1));
        rooms.add(new Room(201, "Double",  2500.0, false, 2));
        rooms.add(new Room(202, "Double",  2500.0, false, 2));
        rooms.add(new Room(301, "Deluxe",  4000.0, false, 3));
        rooms.add(new Room(401, "Suite",   7500.0, false, 4));
        return rooms;
    }
}
