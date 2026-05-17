package server;

import java.net.*;
import java.io.*;

public class GroceryServer {

    private static final int PORT = 5000;

    private static final String VALID_USERNAME = "Youssef Ebrahim";
    private static final String VALID_PASSWORD = "Youssef_33263051";

    private static double dailySalesTotal = 0.0;

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════╗");
        System.out.println("║   Joe Market Server — Port " + PORT + "  ║");
        System.out.println("╚══════════════════════════════════╝");

        try {
            ServerSocket serverSocket = new ServerSocket(PORT); // same as lab
            System.out.println("✅ Waiting for clients on port: "
                    + serverSocket.getLocalPort() + " .......");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("🔗 Just Connected to "
                        + clientSocket.getRemoteSocketAddress());

                Thread worker = new Thread(() -> handleClient(clientSocket));
                worker.setDaemon(true);
                worker.start();
            }

        } catch (IOException e) {
            System.out.println("Server error: " + e.toString());
        }
    }

    private static void handleClient(Socket socket) {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            String request = in.readUTF();
            System.out.println("📨 Received: " + request);

            String response;

            if (request.startsWith("LOGIN:")) {
                response = handleLogin(request);
            } else if (request.startsWith("CHECKOUT:")) {
                response = handleCheckout(request);
            } else if (request.equals("GET_TOTAL_SALES")) {
                response = String.format("TOTAL_SALES:%.2f", dailySalesTotal);
            } else if (request.equals("RESET_TOTAL_SALES")) {
                dailySalesTotal = 0.0;
                response = "RESET_SUCCESS";
            } else if (request.startsWith("LOGOUT:")) {
                response = handleLogout(request);
            } else {
                response = "ERROR:Unknown request";
            }

            out.writeUTF(response);
            System.out.println("📤 Sent: " + response);

            socket.close();

        } catch (IOException e) {
            System.out.println("Client handler error: " + e.toString());
        }
    }

    // ─────────────────────────────────────────────
    // LOGIN Protocol: "LOGIN:username:password"
    // Response: "SUCCESS" | "FAIL"
    // ─────────────────────────────────────────────
    private static String handleLogin(String request) {
        String[] parts = request.split(":", 3);
        if (parts.length == 3) {
            String username = parts[1].trim();
            String password = parts[2].trim();
            if (username.equals(VALID_USERNAME) &&
                    password.equals(VALID_PASSWORD)) {
                return "SUCCESS";
            }
        }
        return "FAIL";
    }

    // ─────────────────────────────────────────────
    // CHECKOUT Protocol: "CHECKOUT:Apple,2;Banana,1;..."
    // Response: "TOTAL:85.00"
    // ─────────────────────────────────────────────
    private static String handleCheckout(String request) {
        String data = request.substring("CHECKOUT:".length());
        String[] entries = data.split(";");
        double total = 0;

        for (String entry : entries) {
            String[] kv = entry.split(",");
            if (kv.length == 2) {
                String itemName = kv[0].trim();
                int qty = Integer.parseInt(kv[1].trim());
                total += getPrice(itemName) * qty;
            }
        }

        // Add to daily sales total
        dailySalesTotal += total;

        return String.format("TOTAL:%.2f", total);
    }

    private static double getPrice(String name) {
        return switch (name) {
            case "Apples" -> 20.0;
            case "Banana" -> 30.0;
            case "Oranges" -> 10.0;
            case "Tomatoes" -> 15.0;
            case "Potatoes" -> 12.0;
            case "Grapes" -> 25.0;
            default -> 0.0;
        };
    }

    // ─────────────────────────────────────────────
    // LOGOUT Protocol: "LOGOUT:username"
    // Response: "SUCCESS"
    // ─────────────────────────────────────────────
    private static String handleLogout(String request) {
        String[] parts = request.split(":", 2);
        String username = parts.length > 1 ? parts[1].trim() : "User";
        String time = java.time.LocalTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
        System.out.println("🚪 Logged Out: [" + username + "] at " + time);
        return "SUCCESS";
    }
}
