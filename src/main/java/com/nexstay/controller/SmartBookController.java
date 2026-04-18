package com.nexstay.controller;


import com.nexstay.model.Customer;
import com.nexstay.model.Room;
import com.nexstay.util.DataStore;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;

public class SmartBookController implements Initializable {

    // ── Search form ───────────────────────────────────────────────────────
    @FXML private TextField tfBudget;
    @FXML private TextField tfGuests;
    @FXML private Label     lblSearchStatus;

    // ── Result card ───────────────────────────────────────────────────────
    @FXML private VBox  resultCard;
    @FXML private Label lblResultRoomNumber;
    @FXML private Label lblResultRoomType;
    @FXML private Label lblResultPrice;
    @FXML private Label lblResultMaxGuests;

    // ── Booking form ──────────────────────────────────────────────────────
    @FXML private VBox       bookingSection;
    @FXML private TextField  tfGuestName;
    @FXML private Label      lblNameErr;
    @FXML private TextField  tfContact;
    @FXML private Label      lblContactErr;
    @FXML private DatePicker dpCheckIn;
    @FXML private DatePicker dpCheckOut;
    @FXML private Label      lblDateErr;
    @FXML private Label      lblBookStatus;

    // Date format used throughout the app
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Currently selected best-match room
    private Room selectedRoom = null;

    // ── Lifecycle ─────────────────────────────────────────────────────────

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Attach live-validation listeners so errors clear as the user types
        tfGuestName.textProperty().addListener((obs, o, n) -> validateName(n, false));
        tfContact  .textProperty().addListener((obs, o, n) -> validateContact(n, false));
        dpCheckIn  .valueProperty().addListener((obs, o, n) -> validateDates(false));
        dpCheckOut .valueProperty().addListener((obs, o, n) -> validateDates(false));
    }

    // ── Find handler ──────────────────────────────────────────────────────

    @FXML
    private void handleFind() {
        lblSearchStatus.setText("");
        hideResultSections();

        // Validate search inputs
        String budgetStr = tfBudget.getText().trim();
        String guestsStr = tfGuests.getText().trim();

        if (budgetStr.isEmpty() || guestsStr.isEmpty()) {
            setSearchStatus("⚠  Please enter both Budget and Number of Guests.", false);
            return;
        }

        if (!budgetStr.matches("\\d+(\\.\\d+)?")) {
            setSearchStatus("⚠  Budget cannot be alphabetical or negative.", false);
            return;
        }
        double budget = Double.parseDouble(budgetStr);

        if (!guestsStr.matches("\\d+")) {
            setSearchStatus("⚠  Guests cannot be alphabetical or negative.", false);
            return;
        }
        int guests = Integer.parseInt(guestsStr);

        if (budget == 0 || guests == 0) {
            setSearchStatus("⚠  Budget and guests must be strictly positive.", false);
            return;
        }

        // Filter: available, within budget, enough capacity; pick best (highest price)
        ArrayList<Room> rooms = DataStore.loadRooms();

        Optional<Room> best = rooms.stream()
                .filter(r -> !r.isBooked())
                .filter(r -> r.getPricePerNight() <= budget)
                .filter(r -> r.getMaxGuests() >= guests)
                .max(Comparator.comparingDouble(Room::getPricePerNight));

        if (best.isEmpty()) {
            setSearchStatus("No rooms available matching your criteria.", false);
            return;
        }

        selectedRoom = best.get();

        lblResultRoomNumber.setText("Room " + selectedRoom.getRoomNumber());
        lblResultRoomType  .setText("Type:        " + selectedRoom.getRoomType());
        lblResultPrice     .setText("Price/Night: ₹ " + String.format("%,.2f", selectedRoom.getPricePerNight()));
        lblResultMaxGuests .setText("Max Guests:  " + selectedRoom.getMaxGuests());

        showResultSections();
        setSearchStatus("✔  Best match found! Fill in your details and click Book Now.", true);
    }

    // ── Book Now handler ──────────────────────────────────────────────────

    @FXML
    private void handleBookNow() {
        lblBookStatus.setText("");

        if (selectedRoom == null) {
            lblBookStatus.setText("⚠  No room selected. Please search first.");
            lblBookStatus.setStyle("-fx-text-fill: #cc0000; -fx-font-size: 13px;");
            return;
        }

        // Run all field validations (strict mode = show errors)
        boolean nameOk    = validateName   (tfGuestName.getText().trim(), true);
        boolean contactOk = validateContact(tfContact  .getText().trim(), true);
        boolean datesOk   = validateDates  (true);

        if (!nameOk || !contactOk || !datesOk) return;

        // All validated — proceed
        LocalDate inDate  = dpCheckIn .getValue();
        LocalDate outDate = dpCheckOut.getValue();
        long   nights = ChronoUnit.DAYS.between(inDate, outDate);
        double total  = nights * selectedRoom.getPricePerNight();

        // Persist Customer
        ArrayList<Customer> customers = DataStore.loadCustomers();
        int customerId = customers.size() + 1;
        customers.add(new Customer(
                customerId,
                tfGuestName.getText().trim(),
                tfContact  .getText().trim(),
                selectedRoom.getRoomNumber(),
                DATE_FMT.format(inDate),
                DATE_FMT.format(outDate)));
        DataStore.saveCustomers(customers);

        // Mark room booked
        ArrayList<Room> rooms = DataStore.loadRooms();
        rooms.stream()
             .filter(r -> r.getRoomNumber() == selectedRoom.getRoomNumber())
             .findFirst()
             .ifPresent(r -> r.setBooked(true));
        DataStore.saveRooms(rooms);

        // Show booking confirmation dialog (blocks until closed)
        showBookingDialog(
                tfGuestName.getText().trim(),
                tfContact.getText().trim(),
                selectedRoom,
                DATE_FMT.format(inDate),
                DATE_FMT.format(outDate),
                (int) nights,
                total);

        // Reset form after dialog is closed
        selectedRoom = null;
        clearBookingForm();
        hideResultSections();
        tfBudget.clear();
        tfGuests.clear();
        setSearchStatus("", true);
    }

    // ── Validation helpers ────────────────────────────────────────────────

    /**
     * Guest Name: alphabets and spaces only.
     * @param strict if true, shows red error; if false, only clears previous error.
     */
    private boolean validateName(String value, boolean strict) {
        if (value.isEmpty()) {
            if (strict) setFieldError(lblNameErr, "Name is required.");
            else        lblNameErr.setText("");
            return false;
        }
        if (!value.matches("[a-zA-Z ]+")) {
            setFieldError(lblNameErr, "Name must contain only letters and spaces.");
            return false;
        }
        lblNameErr.setText("");
        return true;
    }

    /**
     * Contact: exactly 10 numeric digits.
     * @param strict if true, shows red error; if false, only clears previous error.
     */
    private boolean validateContact(String value, boolean strict) {
        if (value.isEmpty()) {
            if (strict) setFieldError(lblContactErr, "Contact is required.");
            else        lblContactErr.setText("");
            return false;
        }
        if (!value.matches("\\d{10}")) {
            setFieldError(lblContactErr, "Contact must be exactly 10 numeric digits.");
            return false;
        }
        lblContactErr.setText("");
        return true;
    }

    /**
     * Dates: both must be selected; check-out must be strictly after check-in.
     * @param strict if true, shows red error; if false, only clears previous error.
     */
    private boolean validateDates(boolean strict) {
        LocalDate in  = dpCheckIn .getValue();
        LocalDate out = dpCheckOut.getValue();

        if (in == null || out == null) {
            if (strict) setFieldError(lblDateErr, "Both check-in and check-out dates are required.");
            else        lblDateErr.setText("");
            return false;
        }
        if (in.isBefore(LocalDate.now())) {
            setFieldError(lblDateErr, "Check-in date cannot be in the past.");
            return false;
        }
        if (!out.isAfter(in)) {
            setFieldError(lblDateErr, "Check-out must be strictly after check-in.");
            return false;
        }
        lblDateErr.setText("");
        return true;
    }

    // ── UI state helpers ──────────────────────────────────────────────────

    private void showResultSections() {
        resultCard    .setVisible(true);  resultCard    .setManaged(true);
        bookingSection.setVisible(true);  bookingSection.setManaged(true);
    }

    private void hideResultSections() {
        resultCard    .setVisible(false); resultCard    .setManaged(false);
        bookingSection.setVisible(false); bookingSection.setManaged(false);
    }

    private void clearBookingForm() {
        tfGuestName.clear();
        tfContact  .clear();
        dpCheckIn  .setValue(null);
        dpCheckOut .setValue(null);
        lblNameErr   .setText("");
        lblContactErr.setText("");
        lblDateErr   .setText("");
    }

    private void setSearchStatus(String msg, boolean success) {
        lblSearchStatus.setText(msg);
        lblSearchStatus.setStyle(success
                ? "-fx-text-fill: #2d6a2d; -fx-font-size: 13px;"
                : "-fx-text-fill: #cc0000; -fx-font-size: 13px;");
    }

    private void setFieldError(Label lbl, String msg) {
        lbl.setText(msg);
        lbl.setStyle("-fx-text-fill: #cc0000; -fx-font-size: 11px;");
    }

    // ── Booking confirmation dialog ───────────────────────────────────────

    private void showBookingDialog(String guestName, String contact, Room room,
                                   String checkIn, String checkOut,
                                   int nights, double total) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.setTitle("Booking Confirmed — NexStay");

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

        Label subtitle = new Label("Booking Confirmed ✔");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #2d6a2d;");

        header.getChildren().addAll(title, subtitle);

        // ── Body
        VBox body = new VBox(10);
        body.setPadding(new Insets(20, 28, 10, 28));
        body.getChildren().addAll(
            bookDetailRow("Guest Name",   guestName),
            bookDetailRow("Contact",      contact),
            bookDetailRow("Room Number",  String.valueOf(room.getRoomNumber())),
            bookDetailRow("Room Type",    room.getRoomType()),
            bookDetailRow("Check-In",     checkIn),
            bookDetailRow("Check-Out",    checkOut),
            bookDetailRow("Nights",       String.valueOf(nights))
        );

        // ── Footer
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
        dialog.showAndWait();
    }

    /** Single label-value row used in the booking receipt body. */
    private HBox bookDetailRow(String label, String value) {
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
}
