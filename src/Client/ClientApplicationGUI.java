package Client;

import Server.Message;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ClientApplicationGUI extends JFrame {
    private static List<Message> messages = new ArrayList<Message>();;
    private static JList<Message> messageList;
    private static DefaultListModel<Message> messageDefaultListModel = new DefaultListModel<>();

    public ClientApplicationGUI() {
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(600, 400);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setTitle("Client Application");
        this.setLayout(new BorderLayout());

        fillGUI();
    }
    public void fillGUI(){
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        JPanel usernamePanel = new JPanel();
        usernamePanel.setLayout(new GridLayout(1, 3));
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        JButton submitButton = new JButton("Submit");
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameField);
        usernamePanel.add(submitButton);
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BorderLayout());
        JLabel messageLabel = new JLabel("Message:");
        JTextField messageField = new JTextField();
        messagePanel.add(messageLabel, BorderLayout.WEST);
        messagePanel.add(messageField, BorderLayout.CENTER);
        topPanel.add(usernamePanel, BorderLayout.NORTH);
        topPanel.add(messagePanel, BorderLayout.SOUTH);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        JLabel chatLabel = new JLabel("Chat");
        messageList = new JList<>(messageDefaultListModel);
        JScrollPane chatScrollPane = new JScrollPane(messageList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        leftPanel.add(chatLabel, BorderLayout.NORTH);
        leftPanel.add(chatScrollPane, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        JLabel importantInfoLabel = new JLabel("Important info");
        JTextArea importantInfoArea = new JTextArea();
        importantInfoArea.setEditable(false);
        JScrollPane importantScrollPane = new JScrollPane(importantInfoArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        rightPanel.add(importantInfoLabel, BorderLayout.NORTH);
        rightPanel.add(importantScrollPane, BorderLayout.CENTER);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1, 2));
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        this.add(topPanel, BorderLayout.NORTH);
        this.add(mainPanel, BorderLayout.CENTER);
    }

    public static void addMessage(Message m){
        messages.add(m);
        messageDefaultListModel.addElement(m);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(()-> new ClientApplicationGUI());
    }
}
