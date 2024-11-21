package Server;

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
    private static int countNoName = 1;
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
            ServerApplicationGUI.addMessage(new Message("Server", "Banned phrases:"));
            for(String phrase : bannedPhrases){
                ServerApplicationGUI.addMessage(new Message("Server", phrase));
            }
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
                username = "No name " + countNoName;
            }
            countNoName++;
            ClientInfo newClient = new ClientInfo(username, clientSocket.getPort(), clientSocket);
            synchronized (clients) {
                clients.add(newClient);
            }
            broadcastMessage(new Message("Server", username + " has connected"));
            ServerApplicationGUI.addMessage(new Message("Server", username + " has connected"));
            manageImportantInfo();
            clientManaging(clientSocket);
        } catch (Exception e) {
            System.err.println("Error handling new client: " + e.getMessage());
        }finally {
            removeClient(clientSocket);
        }
    }

    public void manageImportantInfo() {
        synchronized (clients) {
            broadcastImportantInfo("Welcome to the chat!");
            broadcastImportantInfo("Current connected clients:");
            for (ClientInfo client : clients) {
                broadcastImportantInfo(client.getClientName());
            }
            String[] instructions = {
                    "Instructions:",
                    "To disconnect enter: /quit",
                    "To show banned phrases: /banned",
                    "To send a message to every other client:",
                    "/new <your message>",
                    "To send a message to a specific person:",
                    "/new /onlyto1 username <your message>",
                    "To send a message to multiple specific people:",
                    "/new /onlytomany username1, username2 <your message>",
                    "To send a message to every other connected client, with exception to some people:",
                    "/new /without username1, username2 <your message>"
            };
            for (String instruction : instructions) {
                broadcastImportantInfo(instruction);
            }
        }
    }

    public void broadcastImportantInfo(String message) {
        broadcastMessage(new Message("Important Info", message));
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

    public void broadcastMessageOnlyTo1(Message message, String username) {
        synchronized (clients) {
            for (ClientInfo client : clients) {
                if(client.getClientName().equals(username)){
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
        String usernameDisconnect = null;
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
                message = message.trim();
                ServerApplicationGUI.addMessage(new Message(currentClient.getClientName(), message));
                Boolean isContainsBanned = false;
                for (String phrase : bannedPhrases) {
                    if (message.contains(phrase)) {
                        broadcastMessageOnlyTo1(new Message("Server", "Message blocked due to banned phrase: " + phrase), currentClient.getClientName());
                        isContainsBanned = true;
                    }
                }
                if(!isContainsBanned){
                    if (message.equals("/quit")) {
                        usernameDisconnect = currentClient.getClientName();
                        break;
                    }else {

                        broadcastMessage(new Message(currentClient.getClientName(), message), socket);
                    }
                }
            }
        }catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            handleClientDisconnection(socket, usernameDisconnect);
        }
    }

    public void removeClient(Socket socket) {
        for (ClientInfo client : clients){
            if(client.getClientSocket() == socket){
                clients.remove(client);
            }
        }
        manageImportantInfo();
    }
    public void handleClientDisconnection(Socket socket, String username) {
        try {
            broadcastMessage(new Message("Server", username + " has disconnected"));
            ServerApplicationGUI.addMessage(new Message("Server", username + " has disconnected"));
            socket.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        removeClient(socket);
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
            if (out != null) out.close();
            if (in != null) in.close();
            System.exit(0);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(()-> new ServerApplicationGUI());

        ServerApplication server = new ServerApplication("src/Server/ConfigurationFile.txt");
        server.startServer();
    }
}
