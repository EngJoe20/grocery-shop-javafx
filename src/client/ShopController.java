package client;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ShopController {

    // Quantity TextFields — one per item (making them fully editable!)
    @FXML private TextField lblQtyApples;
    @FXML private TextField lblQtyBanana;
    @FXML private TextField lblQtyOranges;
    @FXML private TextField lblQtyTomatoes;
    @FXML private TextField lblQtyPotatoes;
    @FXML private TextField lblQtyGrapes;

    @FXML private Label  totalLabel;
    @FXML private Button checkoutBtn;
    @FXML private HBox   bottomBar;

    @FXML
    public void initialize() {
        // Slide-up animation for bottom bar
        bottomBar.setTranslateY(60);
        TranslateTransition tt = new TranslateTransition(Duration.millis(500), bottomBar);
        tt.setToY(0);
        tt.setDelay(Duration.millis(200));
        tt.play();

        // Bind interactive numeric listener to each text field
        java.util.List<TextField> fields = java.util.List.of(
            lblQtyApples, lblQtyBanana, lblQtyOranges,
            lblQtyTomatoes, lblQtyPotatoes, lblQtyGrapes
        );

        for (TextField tf : fields) {
            tf.textProperty().addListener((observable, oldValue, newValue) -> {
                // Ensure only digits can be typed
                if (!newValue.matches("\\d*")) {
                    tf.setText(newValue.replaceAll("[^\\d]", ""));
                }
                // Cap quantity to 3 digits (max 999) to keep visual layout perfect
                if (tf.getText().length() > 3) {
                    tf.setText(tf.getText().substring(0, 3));
                }
            });

            // If left empty on focus lost, default back to "0"
            tf.focusedProperty().addListener((obs, out, isFocused) -> {
                if (!isFocused && tf.getText().trim().isEmpty()) {
                    tf.setText("0");
                }
            });
        }
    }

    // Generic quantity alteration helper
    private void changeQty(TextField textField, int delta) {
        try {
            int val = Integer.parseInt(textField.getText().trim());
            int newVal = Math.max(0, Math.min(999, val + delta));
            textField.setText(String.valueOf(newVal));
        } catch (NumberFormatException e) {
            textField.setText("0");
        }
    }

    // --- Quantity Actions (Minus/Plus) ---
    @FXML private void decApples()   { changeQty(lblQtyApples, -1); }
    @FXML private void incApples()   { changeQty(lblQtyApples, 1); }

    @FXML private void decBanana()   { changeQty(lblQtyBanana, -1); }
    @FXML private void incBanana()   { changeQty(lblQtyBanana, 1); }

    @FXML private void decOranges()  { changeQty(lblQtyOranges, -1); }
    @FXML private void incOranges()  { changeQty(lblQtyOranges, 1); }

    @FXML private void decTomatoes() { changeQty(lblQtyTomatoes, -1); }
    @FXML private void incTomatoes() { changeQty(lblQtyTomatoes, 1); }

    @FXML private void decPotatoes() { changeQty(lblQtyPotatoes, -1); }
    @FXML private void incPotatoes() { changeQty(lblQtyPotatoes, 1); }

    @FXML private void decGrapes()   { changeQty(lblQtyGrapes, -1); }
    @FXML private void incGrapes()   { changeQty(lblQtyGrapes, 1); }

    // Helper to get integer value safely from TextFields
    private int getQty(TextField textField) {
        try {
            String text = textField.getText().trim();
            if (text.isEmpty()) return 0;
            return Integer.parseInt(text);
        } catch (Exception e) {
            return 0;
        }
    }

    @FXML
    private void handleCheckout() {
        Map<String, Integer> cart = new LinkedHashMap<>();
        cart.put("Apples",   getQty(lblQtyApples));
        cart.put("Banana",   getQty(lblQtyBanana));
        cart.put("Oranges",  getQty(lblQtyOranges));
        cart.put("Tomatoes", getQty(lblQtyTomatoes));
        cart.put("Potatoes", getQty(lblQtyPotatoes));
        cart.put("Grapes",   getQty(lblQtyGrapes));

        boolean hasItems = cart.values().stream().anyMatch(v -> v > 0);
        if (!hasItems) {
            totalLabel.setText("Select at least one item.");
            return;
        }

        StringBuilder sb = new StringBuilder("CHECKOUT:");
        cart.forEach((name, qty) -> {
            if (qty > 0) sb.append(name).append(",").append(qty).append(";");
        });

        checkoutBtn.setDisable(true);
        checkoutBtn.setText("Processing...");
        String payload = sb.toString();

        new Thread(() -> {
            String response = SocketClient.send(payload);
            javafx.application.Platform.runLater(() -> {
                checkoutBtn.setDisable(false);
                checkoutBtn.setText("Checkout  ✓");

                if (response != null && response.startsWith("TOTAL:")) {
                    String amount = response.split(":")[1];
                    totalLabel.setText("Total:  " + amount + "  LE");
                    
                    // Scale Transition on total label
                    ScaleTransition st = new ScaleTransition(Duration.millis(200), totalLabel);
                    st.setFromX(1); st.setFromY(1);
                    st.setToX(1.15); st.setToY(1.15);
                    st.setCycleCount(2); st.setAutoReverse(true); st.play();

                    // Automatically print receipt
                    handlePrintReceipt();

                    // Reset all quantity TextFields to "0"
                    lblQtyApples.setText("0");
                    lblQtyBanana.setText("0");
                    lblQtyOranges.setText("0");
                    lblQtyTomatoes.setText("0");
                    lblQtyPotatoes.setText("0");
                    lblQtyGrapes.setText("0");

                } else {
                    totalLabel.setText("Server error - try again.");
                }
            });
        }).start();
    }

    @FXML
    private void handleLogout() {
        // Send logout with username to server on background thread
        new Thread(() -> SocketClient.send("LOGOUT:Youssef Ebrahim")).start();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Scene scene = new Scene(loader.load(), 460, 560);
            Stage stage = (Stage) totalLabel.getScene().getWindow();
            stage.setTitle("Grocery Shop — Sign In");
            stage.setScene(scene);
            stage.setResizable(false);
            
            // Fade transition
            scene.getRoot().setOpacity(0);
            FadeTransition ft = new FadeTransition(Duration.millis(300), scene.getRoot());
            ft.setToValue(1.0);
            ft.play();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleShowSales() {
        new Thread(() -> {
            String response = SocketClient.send("GET_TOTAL_SALES");
            javafx.application.Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                if (response != null && response.startsWith("TOTAL_SALES:")) {
                    String totalStr = response.split(":")[1];
                    alert.setTitle("Total Daily Sales");
                    alert.setHeaderText("📊 Live Daily Sales Tracking");
                    alert.setContentText("The total sales across all checkout transactions today is:\n\n👉  " + totalStr + "  LE");
                } else {
                    alert.setTitle("Connection Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Failed to retrieve sales data from the server. Make sure the server is running!");
                }
                styleAlert(alert);
                alert.showAndWait();
            });
        }).start();
    }

    @FXML
    private void handleResetSales() {
        new Thread(() -> {
            String response = SocketClient.send("RESET_TOTAL_SALES");
            javafx.application.Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                if (response != null && response.equals("RESET_SUCCESS")) {
                    alert.setTitle("Register Cleared");
                    alert.setHeaderText("🔄 Register Cleared Successfully");
                    alert.setContentText("The daily sales tracking register has been reset to 0.00 LE on the server!");
                } else {
                    alert.setTitle("Connection Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Failed to send reset command to the server. Make sure the server is running!");
                }
                styleAlert(alert);
                alert.showAndWait();
            });
        }).start();
    }

    // Keep printer generation for automatic trigger on checkout
    private void handlePrintReceipt() {
        Map<String, Integer> cart = new LinkedHashMap<>();
        cart.put("Apples",   getQty(lblQtyApples));
        cart.put("Banana",   getQty(lblQtyBanana));
        cart.put("Oranges",  getQty(lblQtyOranges));
        cart.put("Tomatoes", getQty(lblQtyTomatoes));
        cart.put("Potatoes", getQty(lblQtyPotatoes));
        cart.put("Grapes",   getQty(lblQtyGrapes));

        double total = 0;
        StringBuilder receipt = new StringBuilder();
        receipt.append("========================================\n");
        receipt.append("              JOE MARKET                \n");
        receipt.append("        Premium Grocery Store           \n");
        receipt.append("========================================\n");
        receipt.append(String.format("Date: %s\n", java.time.LocalDate.now()));
        receipt.append(String.format("Time: %s\n", java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"))));
        receipt.append("----------------------------------------\n");
        receipt.append(String.format("%-15s %-10s %-12s\n", "Item", "Qty", "Price (LE)"));
        receipt.append("----------------------------------------\n");

        for (Map.Entry<String, Integer> entry : cart.entrySet()) {
            String name = entry.getKey();
            int qty = entry.getValue();
            if (qty > 0) {
                double price = getPrice(name);
                double sub = price * qty;
                total += sub;
                receipt.append(String.format("%-15s %-10d %-12.2f\n", name, qty, sub));
            }
        }
        receipt.append("----------------------------------------\n");
        receipt.append(String.format("%-25s %-12.2f\n", "TOTAL:", total));
        receipt.append("========================================\n");
        receipt.append("       Thank you for shopping!          \n");
        receipt.append("========================================\n");

        File file = new File("receipt.txt");
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.print(receipt.toString());
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Receipt Printed");
            alert.setHeaderText("Checkout Complete & Receipt Printed!");
            alert.setContentText("The receipt has been automatically written to 'receipt.txt' and opened in Notepad.");
            styleAlert(alert);
            alert.show();

            // Open the file in Notepad on Windows
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                new ProcessBuilder("notepad.exe", "receipt.txt").start();
            } else {
                java.awt.Desktop.getDesktop().open(file);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private double getPrice(String name) {
        return switch (name) {
            case "Apples"   -> 20.0;
            case "Banana"   -> 30.0;
            case "Oranges"  -> 10.0;
            case "Tomatoes" -> 15.0;
            case "Potatoes" -> 12.0;
            case "Grapes"   -> 25.0;
            default         -> 0.0;
        };
    }

    private void styleAlert(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-font-family: 'Aldrich';");
        Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/images/icon.png")));
    }
}