package com.nexstay.controller;

import com.nexstay.model.Billing;
import com.nexstay.model.Customer;
import com.nexstay.model.Room;
import com.nexstay.util.DataStore;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CustomersController implements Initializable {

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ── Summary ───────────────────────────────────────────────────────────
    @FXML private Label lblTotalCustomers;

    // ── Table ─────────────────────────────────────────────────────────────
    @FXML private TableView<CustomerRow>            customersTable;
    @FXML private TableColumn<CustomerRow, Integer>  colCustomerId;
    @FXML private TableColumn<CustomerRow, String>   colName;
    @FXML private TableColumn<CustomerRow, String>   colContact;
    @FXML private TableColumn<CustomerRow, Integer>  colRoomNumber;
    @FXML private TableColumn<CustomerRow, String>   colRoomType;
    @FXML private TableColumn<CustomerRow, String>   colPricePerNight;
    @FXML private TableColumn<CustomerRow, String>   colCheckIn;
    @FXML private TableColumn<CustomerRow, String>   colCheckOut;

    // ── Details panel ─────────────────────────────────────────────────────
    @FXML private VBox  detailsPanel;
    @FXML private Label detCustomerId;
    @FXML private Label detName;
    @FXML private Label detContact;
    @FXML private Label detRoomNumber;
    @FXML private Label detRoomType;
    @FXML private Label detPrice;
    @FXML private Label detCheckIn;
    @FXML private Label detCheckOut;

    // ── Status ────────────────────────────────────────────────────────────
    @FXML private Label lblStatus;

    private final ObservableList<CustomerRow> tableData = FXCollections.observableArrayList();

    // ── Lifecycle ─────────────────────────────────────────────────────────

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Bind columns
        colCustomerId  .setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colName        .setCellValueFactory(new PropertyValueFactory<>("name"));
        colContact     .setCellValueFactory(new PropertyValueFactory<>("contact"));
        colRoomNumber  .setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        colRoomType    .setCellValueFactory(new PropertyValueFactory<>("roomType"));
        colPricePerNight.setCellValueFactory(new PropertyValueFactory<>("pricePerNight"));
        colCheckIn     .setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        colCheckOut    .setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));

        // White text in every cell
        styleCellsWhite(colCustomerId);
        styleCellsWhite(colName);
        styleCellsWhite(colContact);
        styleCellsWhite(colRoomNumber);
        styleCellsWhite(colRoomType);
        styleCellsWhite(colPricePerNight);
        styleCellsWhite(colCheckIn);
        styleCellsWhite(colCheckOut);

        // Row background
        customersTable.setRowFactory(tv -> {
            TableRow<CustomerRow> row = new TableRow<>() {
                @Override
                protected void updateItem(CustomerRow item, boolean empty) {
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

        // Row-selection listener → populate details panel
        customersTable.getSelectionModel()
                      .selectedItemProperty()
                      .addListener((obs, old, selected) -> {
            if (selected != null) {
                populateDetails(selected);
            } else {
                hideDetails();
            }
        });

        customersTable.setItems(tableData);
        refreshTable();
    }

    // ── Checkout action ───────────────────────────────────────────────────

    @FXML
    private void handleCheckout() {
        lblStatus.setText("");

        CustomerRow selected = customersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setStatus("Please select a customer row to checkout.", false);
            return;
        }

        // ── Load live data
        ArrayList<Customer> customers = DataStore.loadCustomers();
        ArrayList<Room>     rooms     = DataStore.loadRooms();

        // ── Find the full Customer object
        Customer target = null;
        for (Customer c : customers) {
            if (c.getCustomerId() == selected.getCustomerId()) {
                target = c;
                break;
            }
        }
        if (target == null) {
            setStatus("Customer record not found in DataStore.", false);
            return;
        }

        // ── Find the booked Room to get type and price
        Room bookedRoom = null;
        for (Room r : rooms) {
            if (r.getRoomNumber() == target.getRoomNumber()) {
                bookedRoom = r;
                break;
            }
        }

        // ── Calculate nights and total based on actual checkout date
        int    nights = 0;
        double total  = 0;
        
        LocalDate actualOutDate = LocalDate.now();
        String actualOutDateStr = actualOutDate.format(DATE_FMT);

        if (bookedRoom != null) {
            try {
                LocalDate inDate  = LocalDate.parse(target.getCheckInDate(),  DATE_FMT);
                nights = (int) ChronoUnit.DAYS.between(inDate, actualOutDate);
                if (nights <= 0) nights = 1; // Minimum charge of 1 night
                total  = nights * bookedRoom.getPricePerNight();
            } catch (Exception ignored) {
                // If dates are unparseable just leave nights/total as 0
            }
        }

        // ── Show receipt dialog FIRST (blocking); only proceed after closed
        showBillDialog(target, bookedRoom, nights, total, actualOutDateStr);

        // ── Free the room
        if (bookedRoom != null) {
            bookedRoom.setBooked(false);
        }

        // ── Save billing record
        ArrayList<Billing> billings = DataStore.loadBillings();
        int billId = billings.size() + 1;
        billings.add(new Billing(
                billId,
                target.getCustomerId(),
                target.getName(),
                target.getContact(),
                target.getRoomNumber(),
                bookedRoom != null ? bookedRoom.getRoomType() : "",
                bookedRoom != null ? bookedRoom.getPricePerNight() : 0,
                nights,
                total,
                target.getCheckInDate(),
                actualOutDateStr));
        DataStore.saveBillings(billings);

        // ── Remove customer and persist
        customers.remove(target);
        DataStore.saveCustomers(customers);
        DataStore.saveRooms(rooms);

        // ── Refresh UI
        hideDetails();
        refreshTable();
        setStatus("Customer checked out successfully.", true);
    }

    // ── Bill receipt dialog ───────────────────────────────────────────────

    private void showBillDialog(Customer c, Room room, int nights, double total, String actualOutDateStr) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.setTitle("Checkout Bill — NexStay");

        // ── Root card
        VBox root = new VBox(0);
        root.setStyle(
            "-fx-background-color: #ffffff;" +
            "-fx-border-color: #c9a84c;" +
            "-fx-border-width: 3;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;"
        );
        root.setPrefWidth(420);

        // ── Header strip
        VBox header = new VBox(4);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(22, 24, 18, 24));
        header.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 7 7 0 0; -fx-border-color: #c9a84c; -fx-border-width: 0 0 1 0;");

        Label title = new Label("NexStay Hotel");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");

        Label subtitle = new Label("Checkout Receipt");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #666666;");

        header.getChildren().addAll(title, subtitle);

        // ── Body
        VBox body = new VBox(10);
        body.setPadding(new Insets(20, 28, 10, 28));
        body.setStyle("-fx-background-color: #ffffff;");

        body.getChildren().addAll(
            detailRow("Customer Name",  c.getName()),
            detailRow("Contact",        c.getContact()),
            detailRow("Room Number",    String.valueOf(c.getRoomNumber())),
            detailRow("Room Type",      room != null ? room.getRoomType() : "—"),
            detailRow("Check-In",       c.getCheckInDate()),
            detailRow("Check-Out",      actualOutDateStr),
            detailRow("Nights Stayed",  String.valueOf(nights)),
            detailRow("Price / Night",  room != null
                    ? String.format("₹ %,.2f", room.getPricePerNight()) : "—")
        );

        // ── Divider
        javafx.scene.control.Separator sep = new javafx.scene.control.Separator();
        sep.setStyle("-fx-background-color: #c9a84c;");
        VBox.setMargin(sep, new Insets(6, 0, 6, 0));
        body.getChildren().add(sep);

        // ── Total row
        HBox totalRow = new HBox();
        totalRow.setAlignment(Pos.CENTER_LEFT);
        Label totalLbl = new Label("TOTAL AMOUNT");
        totalLbl.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");
        Label totalVal = new Label(String.format("  ₹ %,.2f", total));
        totalVal.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #6b1a1a;");
        totalRow.getChildren().addAll(totalLbl, totalVal);
        body.getChildren().add(totalRow);

        // ── Footer with Close button
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(16, 24, 20, 24));
        footer.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 0 0 7 7; -fx-border-color: #c9a84c; -fx-border-width: 1 0 0 0;");

        Button closeBtn = new Button("Close");
        closeBtn.setStyle(
            "-fx-background-color: #c9a84c;" +
            "-fx-text-fill: #000000;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 8 40;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;"
        );
        closeBtn.setOnAction(e -> dialog.close());
        footer.getChildren().add(closeBtn);

        root.getChildren().addAll(header, body, footer);

        Scene scene = new Scene(root);
        dialog.setScene(scene);
        dialog.showAndWait();   // blocks until user clicks Close
    }

    /** Creates a single label-pair row for the receipt body. */
    private HBox detailRow(String label, String value) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);

        Label lbl = new Label(label + ":");
        lbl.setPrefWidth(130);
        lbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #1a1a1a;");

        Label val = new Label(value);
        val.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");

        row.getChildren().addAll(lbl, val);
        return row;
    }

    // ── Details panel helpers ─────────────────────────────────────────────

    private void populateDetails(CustomerRow r) {
        detCustomerId .setText(String.valueOf(r.getCustomerId()));
        detName       .setText(r.getName());
        detContact    .setText(r.getContact());
        detRoomNumber .setText(String.valueOf(r.getRoomNumber()));
        detRoomType   .setText(r.getRoomType());
        detPrice      .setText(r.getPricePerNight());
        detCheckIn    .setText(r.getCheckInDate());
        detCheckOut   .setText(r.getCheckOutDate());
        detailsPanel.setVisible(true);
        detailsPanel.setManaged(true);
    }

    private void hideDetails() {
        detailsPanel.setVisible(false);
        detailsPanel.setManaged(false);
        customersTable.getSelectionModel().clearSelection();
    }

    // ── Data loading ──────────────────────────────────────────────────────

    private void refreshTable() {
        tableData.clear();

        ArrayList<Customer>  customers = DataStore.loadCustomers();
        ArrayList<Room>      rooms     = DataStore.loadRooms();

        for (Customer c : customers) {
            // Look up room for type + price
            Room room = rooms.stream()
                    .filter(r -> r.getRoomNumber() == c.getRoomNumber())
                    .findFirst().orElse(null);
            tableData.add(new CustomerRow(c, room));
        }

        lblTotalCustomers.setText(String.valueOf(customers.size()));
    }

    // ── Status helper ─────────────────────────────────────────────────────

    private void setStatus(String message, boolean success) {
        lblStatus.setText(message);
        lblStatus.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + (success ? "#2d6a2d" : "#cc0000") + ";"
        );
    }

    // ── Cell styling helper ───────────────────────────────────────────────

    private <T> void styleCellsWhite(TableColumn<CustomerRow, T> col) {
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
    // Inner view-model — wraps Customer + Room for TableView binding
    // ══════════════════════════════════════════════════════════════════════
    public static class CustomerRow {
        private final int    customerId;
        private final String name;
        private final String contact;
        private final int    roomNumber;
        private final String roomType;
        private final String pricePerNight;
        private final String checkInDate;
        private final String checkOutDate;

        public CustomerRow(Customer c, Room room) {
            this.customerId    = c.getCustomerId();
            this.name          = c.getName();
            this.contact       = c.getContact();
            this.roomNumber    = c.getRoomNumber();
            this.roomType      = room != null ? room.getRoomType()                              : "—";
            this.pricePerNight = room != null ? String.format("₹ %,.2f", room.getPricePerNight()) : "—";
            this.checkInDate   = c.getCheckInDate();
            this.checkOutDate  = c.getCheckOutDate();
        }

        public int    getCustomerId()   { return customerId; }
        public String getName()         { return name; }
        public String getContact()      { return contact; }
        public int    getRoomNumber()   { return roomNumber; }
        public String getRoomType()     { return roomType; }
        public String getPricePerNight(){ return pricePerNight; }
        public String getCheckInDate()  { return checkInDate; }
        public String getCheckOutDate() { return checkOutDate; }
    }
}
