package Client;

public class ClientInfo {
    private String clientName;
    private int clientPort;
    public ClientInfo(String clientName, int clientPort) {
        this.clientName = clientName;
        this.clientPort = clientPort;
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

}
