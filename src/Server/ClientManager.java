package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientManager implements Runnable {
    private int clientPort;
    private String clientUsername;
    private Socket socket;
    private PrintWriter out;
    private ServerApplication server;

    public ClientManager(Socket socket, ServerApplication server) {
        this.socket = socket;
        this.clientPort = socket.getPort();
        this.server = server;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("Enter your username:");
            clientUsername = in.readLine();
            if (clientUsername == null || clientUsername.isEmpty()) {
                out.println("Invalid name");
                socket.close();
                return;
            }
            server.addClient(clientUsername, this);
            System.out.println("Client registered: " + clientUsername + " (Port: " + clientPort + ")");
            //broadcast("User " + clientName + " joined.");

            //sendClientList();

            String message;
            while ((message = in.readLine()) != null) {
                //handleClientMessage(message);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            disconnectClient();
        }
    }
    private void disconnectClient() {
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
