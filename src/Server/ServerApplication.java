package Server;

import Client.ClientApplication;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ServerApplication {
    private ServerSocket serverSocket;
    private int port;
    private String name;
    private Set<String> bannedPhrases = new HashSet<>();
    private Map<String, ClientApplication> clients = new HashMap<>();

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
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(port);
            ServerApplicationGUI.addMessage(new Message("Server " + name + " started on port " + port));
            /*while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientApplication clientHandler = new ClientApplication(clientSocket, this);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }*/
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            closeServerSocket();
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

    public synchronized void addClient(String name, ClientApplication m) {
        clients.put(name, m);
    }

    public synchronized void removeClient(String name) {
        clients.remove(name);
    }

    public synchronized Map<String, ClientApplication> getClients() {
        return new HashMap<>(clients);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(()-> new ServerApplicationGUI());

        ServerApplication server = new ServerApplication("src/Server/ConfigurationFile.txt");
        server.startServer();
    }
}
