package client;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoginController {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField     passwordTextField;
    @FXML private Button        togglePasswordBtn;
    @FXML private Button        loginBtn;
    @FXML private Label         messageLabel;
    @FXML private VBox          card;

    private boolean isPasswordVisible = false;

    @FXML
    public void initialize() {
        // --- NEW: Slide-up & Fade-in parallel animation on load ---
        card.setOpacity(0);
        card.setTranslateY(45);
        
        FadeTransition ft = new FadeTransition(Duration.millis(750), card);
        ft.setFromValue(0);
        ft.setToValue(1.0);
        
        TranslateTransition tt = new TranslateTransition(Duration.millis(750), card);
        tt.setToY(0);
        
        ParallelTransition pt = new ParallelTransition(ft, tt);
        pt.play();

        // --- NEW: Bidirectional text binding to sync password inputs in real-time ---
        passwordField.textProperty().bindBidirectional(passwordTextField.textProperty());

        // Enter key on either password field triggers login
        passwordField.setOnAction(e -> handleLogin());
        passwordTextField.setOnAction(e -> handleLogin());
    }

    @FXML
    private void handleTogglePassword() {
        isPasswordVisible = !isPasswordVisible;
        if (isPasswordVisible) {
            // Reveal password
            passwordTextField.setVisible(true);
            passwordField.setVisible(false);
            togglePasswordBtn.setText("🙈"); // eye closed icon
        } else {
            // Mask password
            passwordField.setVisible(true);
            passwordTextField.setVisible(false);
            togglePasswordBtn.setText("👁"); // eye open icon
        }
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showMsg("⚠  Please fill in both fields.", "warn");
            return;
        }

        loginBtn.setDisable(true);
        loginBtn.setText("Connecting...");

        // Network call off UI thread — same approach as lab but threaded
        new Thread(() -> {
            String response = SocketClient.send("LOGIN:" + username + ":" + password);

            javafx.application.Platform.runLater(() -> {
                loginBtn.setDisable(false);
                loginBtn.setText("Sign In");

                switch (response) {
                    case "SUCCESS" -> openShop();
                    case "FAIL"    -> {
                        showMsg("✗  Invalid username or password.", "error");
                        shake(usernameField);
                        shake(passwordField);
                        shake(passwordTextField);
                        passwordField.clear();
                    }
                    default        -> showMsg("✗  Cannot reach server.", "error");
                }
            });
        }).start();
    }

    private void openShop() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/shop.fxml"));
            Scene scene = new Scene(loader.load(), 940, 680);
            Stage stage  = (Stage) loginBtn.getScene().getWindow();
            stage.setTitle("Grocery Shop — Market");
            stage.setResizable(true);
            stage.setScene(scene);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showMsg(String text, String type) {
        messageLabel.setText(text);
        messageLabel.getStyleClass().removeAll("msg-error","msg-warn","msg-ok");
        messageLabel.getStyleClass().add("msg-" + type);
        messageLabel.setVisible(true);
    }

    private void shake(Control node) {
        TranslateTransition t = new TranslateTransition(Duration.millis(55), node);
        t.setByX(8); t.setCycleCount(6); t.setAutoReverse(true); t.play();
    }
}
