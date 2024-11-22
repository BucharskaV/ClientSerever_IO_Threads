package Client;

import java.net.Socket;

public class ClientInfo {
    private String clientName;
    private int clientPort;
    private Socket clientSocket;

    public ClientInfo(String clientName, int clientPort, Socket socket) {
        this.clientName = clientName;
        this.clientPort = clientPort;
        this.clientSocket = socket;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public int getClientPort() {
        return clientPort;
    }

    public void setClientPort(int clientPort) {
        this.clientPort = clientPort;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
}
