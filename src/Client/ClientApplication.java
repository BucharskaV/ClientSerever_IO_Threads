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
                Boolean flag = app.getNewMyMessageAppeared();
                if (flag) {
                    out.println(app.getMylastMessage());
                    app.setNewMyMessageAppeared(false);
                    app.setMylastMessage("");
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        }
    }

    private class MessageHandler implements Runnable {
        @Override
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("Important Info:")) {
                        if(message.substring(16).equals("Welcome to the chat!"))
                            app.clearImportantInfo();
                        app.setImportantInfo(message.substring(16)+"\n");
                    } else {
                        app.addMessage(message);
                    }
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            } finally {
                close();
            }
        }
    }

    public void close() {
        try {
            if (socket != null) socket.close();
            if (out != null) out.close();
            if (in != null) in.close();
            System.exit(0);
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
