package Client;

import Server.ServerApplicationGUI;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientApplication implements Runnable {

    private ClientApplicationGUI app;
    private String username;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    public ClientApplication(ClientApplicationGUI app, String host, int port) {
        this.app = app;
        //this.username = app.getUsername();
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            new Thread(this).start();

            username = JOptionPane.showInputDialog("Enter your username:");
            if (username == null || username.trim().isEmpty()) {
                username = "No name";
            }
        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        /*try {


        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            close();
        }*/
    }

    public void close() {
        try {
            if (socket != null) socket.close();
            if (out != null) out.close();
            if (in != null) in.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        ClientApplicationGUI app = new ClientApplicationGUI();
        SwingUtilities.invokeLater(app);

        ClientApplication client = new ClientApplication(app, "localhost", 11111);
    }
}
