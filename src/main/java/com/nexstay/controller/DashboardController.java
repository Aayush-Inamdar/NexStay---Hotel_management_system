package com.nexstay.controller;

import com.nexstay.model.Billing;
import com.nexstay.model.Room;
import com.nexstay.util.DataStore;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML private Label lblTotalRooms;
    @FXML private Label lblAvailable;
    @FXML private Label lblOccupied;
    @FXML private Label lblRevenue;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadStats();
    }

    // ── Data loading ──────────────────────────────────────────────────────

    private void loadStats() {
        // ── Room counts ───────────────────────────────────────────────────
        ArrayList<Room> rooms = DataStore.loadRooms();

        int total     = rooms.size();
        int occupied  = (int) rooms.stream().filter(Room::isBooked).count();
        int available = total - occupied;

        lblTotalRooms.setText(String.valueOf(total));
        lblAvailable .setText(String.valueOf(available));
        lblOccupied  .setText(String.valueOf(occupied));

        // ── Revenue ───────────────────────────────────────────────────────
        ArrayList<Billing> billings = DataStore.loadBillings();

        double revenue = billings.stream()
                                 .mapToDouble(Billing::getTotalAmount)
                                 .sum();

        lblRevenue.setText(String.format("₹ %,.2f", revenue));
    }
}
