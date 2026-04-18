package com.nexstay.controller;

import com.nexstay.model.Billing;
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

public class BillingController implements Initializable {

    // ── Table ─────────────────────────────────────────────────────────────
    @FXML private TableView<BillRow>           billingTable;
    @FXML private TableColumn<BillRow, Integer> colBillId;
    @FXML private TableColumn<BillRow, String>  colCustomerName;
    @FXML private TableColumn<BillRow, String>  colContact;
    @FXML private TableColumn<BillRow, Integer> colRoomNumber;
    @FXML private TableColumn<BillRow, String>  colRoomType;
    @FXML private TableColumn<BillRow, String>  colPricePerNight;
    @FXML private TableColumn<BillRow, Integer> colNights;
    @FXML private TableColumn<BillRow, String>  colTotal;
    @FXML private TableColumn<BillRow, String>  colCheckIn;
    @FXML private TableColumn<BillRow, String>  colCheckOut;

    // ── Revenue label ─────────────────────────────────────────────────────
    @FXML private Label lblTotalRevenue;

    private final ObservableList<BillRow> tableData = FXCollections.observableArrayList();

    // ── Lifecycle ─────────────────────────────────────────────────────────

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Bind columns
        colBillId       .setCellValueFactory(new PropertyValueFactory<>("billId"));
        colCustomerName .setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colContact      .setCellValueFactory(new PropertyValueFactory<>("contact"));
        colRoomNumber   .setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        colRoomType     .setCellValueFactory(new PropertyValueFactory<>("roomType"));
        colPricePerNight.setCellValueFactory(new PropertyValueFactory<>("pricePerNight"));
        colNights       .setCellValueFactory(new PropertyValueFactory<>("numberOfNights"));
        colTotal        .setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        colCheckIn      .setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        colCheckOut     .setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));

        // White text in every cell
        styleCellsWhite(colBillId);
        styleCellsWhite(colCustomerName);
        styleCellsWhite(colContact);
        styleCellsWhite(colRoomNumber);
        styleCellsWhite(colRoomType);
        styleCellsWhite(colPricePerNight);
        styleCellsWhite(colNights);
        styleCellsWhite(colTotal);
        styleCellsWhite(colCheckIn);
        styleCellsWhite(colCheckOut);

        // Row background
        billingTable.setRowFactory(tv -> {
            TableRow<BillRow> row = new TableRow<>() {
                @Override
                protected void updateItem(BillRow item, boolean empty) {
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

        billingTable.setItems(tableData);
        refreshTable();
    }

    // ── Data loading ──────────────────────────────────────────────────────

    /**
     * Reads all billing records fresh from DataStore every time the screen is opened.
     * Billing records are only created at checkout (CustomersController), never at booking.
     */
    private void refreshTable() {
        tableData.clear();

        ArrayList<Billing> billings = DataStore.loadBillings();
        double revenue = 0;

        for (Billing b : billings) {
            tableData.add(new BillRow(b));
            revenue += b.getTotalAmount();
        }

        lblTotalRevenue.setText(String.format("₹ %,.2f", revenue));
    }

    // ── Cell styling helper ───────────────────────────────────────────────

    private <T> void styleCellsWhite(TableColumn<BillRow, T> col) {
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
    // Inner view-model — wraps Billing for TableView binding
    // ══════════════════════════════════════════════════════════════════════
    public static class BillRow {
        private final int    billId;
        private final String customerName;
        private final String contact;
        private final int    roomNumber;
        private final String roomType;
        private final String pricePerNight;
        private final int    numberOfNights;
        private final String totalAmount;
        private final String checkInDate;
        private final String checkOutDate;

        public BillRow(Billing b) {
            this.billId         = b.getBillId();
            this.customerName   = b.getCustomerName();
            this.contact        = b.getContact()        != null ? b.getContact()  : "";
            this.roomNumber     = b.getRoomNumber();
            this.roomType       = b.getRoomType()       != null ? b.getRoomType() : "";
            this.pricePerNight  = String.format("₹ %,.2f", b.getPricePerNight());
            this.numberOfNights = b.getNumberOfNights();
            this.totalAmount    = String.format("₹ %,.2f", b.getTotalAmount());
            this.checkInDate    = b.getCheckInDate()    != null ? b.getCheckInDate()  : "";
            this.checkOutDate   = b.getCheckOutDate()   != null ? b.getCheckOutDate() : "";
        }

        public int    getBillId()         { return billId; }
        public String getCustomerName()   { return customerName; }
        public String getContact()        { return contact; }
        public int    getRoomNumber()     { return roomNumber; }
        public String getRoomType()       { return roomType; }
        public String getPricePerNight()  { return pricePerNight; }
        public int    getNumberOfNights() { return numberOfNights; }
        public String getTotalAmount()    { return totalAmount; }
        public String getCheckInDate()    { return checkInDate; }
        public String getCheckOutDate()   { return checkOutDate; }
    }
}
