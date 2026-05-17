package client;

import java.io.*;
import java.net.*;

/**
 * SocketClient — utility class.
 *
 * Uses EXACTLY the same pattern as TCPClient.java from Socket_Lab:
 *
 * Socket client = new Socket("localhost", port);
 * DataOutputStream out = new DataOutputStream(client.getOutputStream());
 * DataInputStream in = new DataInputStream(client.getInputStream());
 * out.writeUTF(message);
 * String reply = in.readUTF();
 */
public class SocketClient {

    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    /**
     * Sends one request to the server, returns the response.
     * Mirrors the lab's TCPClient.java flow exactly.
     */
    public static String send(String request) {
        try {
            Socket client = new Socket(HOST, PORT);
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            DataInputStream in = new DataInputStream(client.getInputStream());

            out.writeUTF(request);
            String response = in.readUTF();

            in.close();
            out.close();
            client.close();

            return response;

        } catch (IOException e) {
            return "ERROR:" + e.getMessage();
        }
    }
}
