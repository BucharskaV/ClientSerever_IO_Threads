package Client;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientApplication {
    private String host;
    private int serverPort;
    private ClientApplicationGUI app;
    private String username;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    public ClientApplication(ClientApplicationGUI app, String host, int serverPort) {
        this.app = app;
        this.host = host;
        this.serverPort = serverPort;
    }
    public void startClient(){
        try {
            socket = new Socket(host, serverPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            new Thread(new MessageHandler()).start();

            while (true) {
                if(app.getNewMessageAppeared()){
                    out.println(app.getMylastMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        } finally {
            close();
        }
    }
    private class MessageHandler implements Runnable {
        @Override
        public void run() {
            try {
                String message;
                String importantInfo = "";

                while ((message = in.readLine()) != null) {
                    System.out.println("Received: " + message);
                    String prefix = "Important Info: ";
                    if (message.startsWith(prefix)) {
                        do {
                            importantInfo += message.substring(prefix.length()) + "\n";
                        } while ((message = in.readLine()) != null && message.startsWith(prefix));

                        app.setImportantInfo(importantInfo.trim());
                        importantInfo = "";
                    } else {
                        app.addMessage(message);
                    }
                }
                /*String message;
                while ((message = in.readLine()) != null) {
                    String prefix = "Important Info: ";
                    if (message.startsWith(prefix)) {
                        app.setImportantInfo(message.substring(prefix.length()));
                    } else {
                        app.addMessage(message);
                    }
                }*/
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void sendMessage(String message) {
        out.println(message);
        out.flush();
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
        client.startClient();
    }
}
