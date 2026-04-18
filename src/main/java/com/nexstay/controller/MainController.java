package com.nexstay.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private StackPane contentArea;

    // ── Lifecycle ─────────────────────────────────────────────────────────

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Show Dashboard by default on launch
        loadFxml("Dashboard");
    }

    // ── Button handlers ───────────────────────────────────────────────────

    @FXML
    private void handleDashboard() {
        loadFxml("Dashboard");
    }

    @FXML
    private void handleRooms() {
        loadFxml("Rooms");
    }

    @FXML
    private void handleSmartBook() {
        loadFxml("SmartBook");
    }

    @FXML
    private void handleBilling() {
        loadFxml("Billing");
    }

    @FXML
    private void handleCustomers() {
        loadFxml("Customers");
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    /**
     * Loads an FXML file from /com/nexstay/<name>.fxml into the contentArea.
     * Falls back to a placeholder label on load failure.
     */
    private void loadFxml(String name) {
        try {
            URL resource = getClass().getResource("/com/nexstay/" + name + ".fxml");
            if (resource == null) {
                showPlaceholder(name + " (FXML not found)");
                return;
            }
            Node view = FXMLLoader.load(resource);
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
            showPlaceholder(name + " (load error)");
        }
    }

    /**
     * Fallback: shows a centred gold label with the screen name.
     * Used for screens not yet implemented as FXML.
     */
    private void showPlaceholder(String screenName) {
        Label placeholder = new Label(screenName);
        placeholder.setStyle(
            "-fx-font-size: 32px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #c9a84c;"
        );
        StackPane.setAlignment(placeholder, Pos.CENTER);
        contentArea.getChildren().setAll(placeholder);
    }
}
