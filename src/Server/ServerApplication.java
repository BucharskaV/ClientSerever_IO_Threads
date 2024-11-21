package Server;

import Client.ClientApplication;
import Client.ClientInfo;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ServerApplication {
    private ServerSocket serverSocket;
    private int port;
    private String name;
    private PrintWriter out;
    private BufferedReader in;
    private List<String> bannedPhrases = new ArrayList<>();
    private List<ClientInfo> clients = new ArrayList<>();

    public ServerApplication(String fileConfigName) {
        loadConfigurationFile(fileConfigName);
    }

    public void loadConfigurationFile(String fileConfigName){
        try (BufferedReader reader = new BufferedReader(new FileReader(fileConfigName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] settings = line.split(": ", 2);
                switch (settings[0]){
                    case "Port": this.port = Integer.parseInt(settings[1]); break;
                    case "Name": this.name = settings[1]; break;
                    case "Banned phrases":
                        bannedPhrases.addAll(Arrays.asList(settings[1].split(", ")));
                        break;
                    default: throw new IOException("Invalid setting: " + settings[0]);
                }
            }
            ServerApplicationGUI.addMessage(new Message("Server", "Loading configuration file is done"));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(port);
            ServerApplicationGUI.addMessage(new Message("Server", "Server " + name + " started on port " + port));
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleNewClient(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Connection can't be established: " + e.getMessage());
        } finally {
            closeServerSocket();
        }
    }

    public void handleNewClient(Socket clientSocket) {
        try {
            String username = JOptionPane.showInputDialog("Enter your username:");
            if (username == null || username.trim().isEmpty()) {
                username = "No name";
            }
            ClientInfo newClient = new ClientInfo(username, clientSocket.getPort(), clientSocket);
            synchronized (clients) {
                clients.add(newClient);
            }
            broadcastMessage(new Message("Server", username + " has connected"));
            manageImportantInfo();
            clientManaging(clientSocket);
        } catch (Exception e) {
            System.err.println("Error handling new client: " + e.getMessage());
        }
    }

    public void manageImportantInfo() {
        synchronized (clients) {
            broadcastMessage(new Message("Important Info", "Welcome to the chat!"));
            broadcastMessage(new Message("Important Info", "Current connected clients:"));
            for (ClientInfo c : clients) {
                broadcastMessage(new Message("Important Info", c.getClientName()));
            }
        }
        broadcastMessage(new Message("Important Info", "Instructions:"));
    }

    public void broadcastMessage(Message message) {
        synchronized (clients) {
            for (ClientInfo client : clients) {
                try {
                    PrintWriter out = new PrintWriter(client.getClientSocket().getOutputStream(), true);
                    out.println(message.toString());
                } catch (IOException e) {
                    System.err.println("Error broadcasting message: " + e.getMessage());
                }
            }
        }
    }
    public void broadcastMessage(Message message, Socket socketAvoid) {
        synchronized (clients){
            for (ClientInfo client : clients) {
                if(client.getClientSocket() != socketAvoid){
                    try {
                        PrintWriter out = new PrintWriter(client.getClientSocket().getOutputStream(), true);
                        out.println(message.toString());
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                    }
                }

            }
        }
    }
    public void clientManaging(Socket socket) {
        ClientInfo currentClient = null;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            synchronized (clients) {
                for (ClientInfo client : clients) {
                    if (client.getClientSocket().equals(socket)) {
                        currentClient = client;
                        break;
                    }
                }
            }

            if (currentClient == null) {
                System.err.println("Unable to find client information for the socket.");
                return;
            }

            String message;
            while ((message = in.readLine()) != null) {
                message = message.trim(); // Remove leading and trailing whitespace
                if (!message.isEmpty()) {
                    if (message.equalsIgnoreCase("quit")) {
                        // Handle client disconnection
                        break;
                    }

                    // Broadcast received message to all other clients
                    broadcastMessage(new Message(currentClient.getClientName(), message));
                }
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            close(socket);
        }
    }



    public void close(Socket socket) {
        try {
            if (socket != null) socket.close();
            if (out != null) out.close();
            if (in != null) in.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void closeServerSocket() {
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
                System.out.println("Server socket closed.");
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public synchronized void addClient(ClientInfo c) {
        clients.add(c);
    }

    public synchronized void removeClient(String name) {
        clients.remove(name);
    }

    public synchronized ArrayList<ClientInfo> getClients() {
        return new ArrayList<>(clients);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(()-> new ServerApplicationGUI());

        ServerApplication server = new ServerApplication("src/Server/ConfigurationFile.txt");
        server.startServer();
    }
}
