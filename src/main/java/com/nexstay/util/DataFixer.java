package com.nexstay.util;

import com.nexstay.model.Billing;
import com.nexstay.model.Room;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * A handy terminal utility to manually edit the binary DataStore files.
 * Because the data is saved as binary Java Objects using ObjectOutputStream,
 * you cannot edit it in a text editor. You have to deserialize it, modify the list,
 * and serialize it back.
 *
 * To run this manually from your terminal, compile and execute it using Maven:
 * mvn exec:java -Dexec.mainClass="com.nexstay.util.DataFixer"
 */
public class DataFixer {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== NexStay Data Fixer ===");
        System.out.println("1. Delete a Billing Record by Bill ID");
        System.out.println("2. Delete a Room by Room Number");
        System.out.println("3. Cancel / Exit");
        System.out.print("Select an option: ");
        
        int option = scanner.nextInt();
        if (option == 1) {
            deleteBillingRecord(scanner);
        } else if (option == 2) {
            deleteRoomRecord(scanner);
        } else {
            System.out.println("Exiting...");
        }
    }

    private static void deleteBillingRecord(Scanner scanner) {
        System.out.print("Enter the Bill ID you want to delete: ");
        int targetId = scanner.nextInt();

        // 1. Load the existing binary data into ArrayList
        ArrayList<Billing> billings = DataStore.loadBillings();
        
        Billing toDelete = null;
        for (Billing b : billings) {
            if (b.getBillId() == targetId) {
                toDelete = b;
                break;
            }
        }

        if (toDelete != null) {
            // 2. Modify the ArrayList
            billings.remove(toDelete);
            
            // 3. Save the ArrayList back to the binary file
            DataStore.saveBillings(billings);
            
            System.out.println("Success! Billing record ID " + targetId + " has been permanently deleted.");
        } else {
            System.out.println("Error: No billing record found with ID " + targetId + ".");
        }
    }

    private static void deleteRoomRecord(Scanner scanner) {
        System.out.print("Enter the Room Number you want to delete: ");
        int targetRoom = scanner.nextInt();

        ArrayList<Room> rooms = DataStore.loadRooms();
        
        Room toDelete = null;
        for (Room r : rooms) {
            if (r.getRoomNumber() == targetRoom) {
                toDelete = r;
                break;
            }
        }

        if (toDelete != null) {
            if (toDelete.isBooked()) {
                System.out.println("Error: Cannot delete room " + targetRoom + " because it is currently booked.");
                return;
            }
            
            rooms.remove(toDelete);
            DataStore.saveRooms(rooms);
            
            System.out.println("Success! Room " + targetRoom + " has been permanently deleted.");
        } else {
            System.out.println("Error: No room found with Room Number " + targetRoom + ".");
        }
    }
}
