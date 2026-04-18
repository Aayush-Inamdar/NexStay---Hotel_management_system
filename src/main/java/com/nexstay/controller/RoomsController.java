package com.nexstay.controller;

import com.nexstay.model.Room;
import com.nexstay.util.DataStore;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class RoomsController implements Initializable {

    // ── Form inputs ───────────────────────────────────────────────────────
    @FXML private TextField  tfRoomNumber;
    @FXML private ComboBox<String> cbRoomType;
    @FXML private TextField  tfPrice;
    @FXML private TextField  tfMaxGuests;
    @FXML private Label      lblStatus;

    // ── Table ─────────────────────────────────────────────────────────────
    @FXML private TableView<RoomRow>      roomTable;
    @FXML private TableColumn<RoomRow, Integer> colRoomNumber;
    @FXML private TableColumn<RoomRow, String>  colRoomType;
    @FXML private TableColumn<RoomRow, String>  colPrice;
    @FXML private TableColumn<RoomRow, Integer> colMaxGuests;
    @FXML private TableColumn<RoomRow, String>  colStatus;

    private final ObservableList<RoomRow> tableData = FXCollections.observableArrayList();

    // ── Lifecycle ─────────────────────────────────────────────────────────

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Populate ComboBox
        cbRoomType.setItems(FXCollections.observableArrayList(
                "Single", "Double", "Deluxe", "Suite"));
                
        cbRoomType.valueProperty().addListener((obs, oldVal, newVal) -> {
            if ("Single".equals(newVal)) {
                tfMaxGuests.setText("1");
                tfMaxGuests.setEditable(false);
                tfMaxGuests.setStyle("-fx-opacity: 0.8; -fx-background-color: #f5f5f5;");
            } else if ("Double".equals(newVal)) {
                tfMaxGuests.setText("2");
                tfMaxGuests.setEditable(false);
                tfMaxGuests.setStyle("-fx-opacity: 0.8; -fx-background-color: #f5f5f5;");
            } else {
                tfMaxGuests.setEditable(true);
                tfMaxGuests.setStyle("");
                if ("Single".equals(oldVal) || "Double".equals(oldVal)) {
                    tfMaxGuests.clear();
                }
            }
        });

        cbRoomType.getSelectionModel().selectFirst();

        // Bind table columns
        colRoomNumber.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        colRoomType  .setCellValueFactory(new PropertyValueFactory<>("roomType"));
        colPrice     .setCellValueFactory(new PropertyValueFactory<>("price"));
        colMaxGuests .setCellValueFactory(new PropertyValueFactory<>("maxGuests"));
        colStatus    .setCellValueFactory(new PropertyValueFactory<>("status"));

        // Style table rows – white text on navy
        roomTable.setStyle(
            "-fx-background-color: #ffffff;" +
            "-fx-control-inner-background: #ffffff;" +
            "-fx-text-fill: #1a1a1a;"
        );
        roomTable.setRowFactory(tv -> {
            TableRow<RoomRow> row = new TableRow<>() {
                @Override
                protected void updateItem(RoomRow item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setStyle("-fx-background-color: #ffffff;");
                    } else {
                        if (getIndex() % 2 == 0) {
                            setStyle("-fx-background-color: #ffffff; -fx-text-fill: #1a1a1a;");
                        } else {
                            setStyle("-fx-background-color: #f5e6d0; -fx-text-fill: #1a1a1a;");
                        }
                    }
                }
            };
            return row;
        });

        // Style each column's cells
        styleCellsWhite(colRoomNumber);
        styleCellsWhite(colRoomType);
        styleCellsWhite(colPrice);
        styleCellsWhite(colMaxGuests);
        styleCellsWhite(colStatus);

        roomTable.setItems(tableData);
        refreshTable();
    }

    // ── Handlers ──────────────────────────────────────────────────────────

    @FXML
    private void handleAddRoom() {
        lblStatus.setText("");

        // ── Validation ────────────────────────────────────────────────────
        String roomNumberStr = tfRoomNumber.getText().trim();
        String roomType      = cbRoomType.getValue();
        String priceStr      = tfPrice.getText().trim();
        String maxGuestsStr  = tfMaxGuests.getText().trim();

        if (roomNumberStr.isEmpty() || priceStr.isEmpty() || maxGuestsStr.isEmpty()) {
            setStatus("All fields are required.", false);
            return;
        }

        if (!roomNumberStr.matches("\\d+")) {
            setStatus("Room Number cannot be alphabetical or negative.", false);
            return;
        }
        int    roomNumber = Integer.parseInt(roomNumberStr);

        if (!priceStr.matches("\\d+(\\.\\d+)?")) {
            setStatus("Price cannot be alphabetical or negative.", false);
            return;
        }
        double price = Double.parseDouble(priceStr);

        if (!maxGuestsStr.matches("\\d+")) {
            setStatus("Max Guests cannot be alphabetical or negative.", false);
            return;
        }
        int    maxGuests = Integer.parseInt(maxGuestsStr);

        if (roomNumber == 0 || price == 0 || maxGuests == 0) {
            setStatus("Values must be strictly greater than zero.", false);
            return;
        }

        // ── Duplicate check ───────────────────────────────────────────────
        ArrayList<Room> rooms = DataStore.loadRooms();
        final int rn = roomNumber;
        boolean exists = rooms.stream().anyMatch(r -> r.getRoomNumber() == rn);
        if (exists) {
            setStatus("Room " + roomNumber + " already exists.", false);
            return;
        }

        // ── Save ──────────────────────────────────────────────────────────
        rooms.add(new Room(roomNumber, roomType, price, false, maxGuests));
        DataStore.saveRooms(rooms);

        clearForm();
        refreshTable();
        setStatus("Room " + roomNumber + " added successfully.", true);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private void refreshTable() {
        tableData.clear();
        ArrayList<Room> rooms = DataStore.loadRooms();
        for (Room r : rooms) {
            tableData.add(new RoomRow(r));
        }
    }

    private void clearForm() {
        tfRoomNumber.clear();
        tfPrice.clear();
        tfMaxGuests.clear();
        cbRoomType.getSelectionModel().selectFirst();
    }

    private void setStatus(String msg, boolean success) {
        lblStatus.setText(msg);
        lblStatus.setStyle(
            success
                ? "-fx-text-fill: #2d6a2d; -fx-font-size: 13px;"  // green
                : "-fx-text-fill: #cc0000; -fx-font-size: 13px;"  // red
        );
    }

    private <T> void styleCellsWhite(TableColumn<RoomRow, T> col) {
        col.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                    setStyle("-fx-text-fill: #1a1a1a; -fx-background-color: transparent;");
                }
            }
        });
    }

    // ══════════════════════════════════════════════════════════════════════
    // Inner view-model – wraps Room for TableView binding
    // ══════════════════════════════════════════════════════════════════════
    public static class RoomRow {
        private final int    roomNumber;
        private final String roomType;
        private final String price;
        private final int    maxGuests;
        private final String status;

        public RoomRow(Room r) {
            this.roomNumber = r.getRoomNumber();
            this.roomType   = r.getRoomType();
            this.price      = String.format("₹ %,.2f", r.getPricePerNight());
            this.maxGuests  = r.getMaxGuests();
            this.status     = r.isBooked() ? "Booked" : "Available";
        }

        public int    getRoomNumber() { return roomNumber; }
        public String getRoomType()   { return roomType; }
        public String getPrice()      { return price; }
        public int    getMaxGuests()  { return maxGuests; }
        public String getStatus()     { return status; }
    }
}
