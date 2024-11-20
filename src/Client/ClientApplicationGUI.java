package Client;

import Server.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ClientApplicationGUI extends JFrame implements Runnable{
    private static List<Message> messages = new ArrayList<Message>();;
    private static JList<Message> messageList;
    private static DefaultListModel<Message> messageDefaultListModel = new DefaultListModel<>();
    private String username;
    private Message lastMessage;
    private Boolean isNewMessageAppeared = false;
    public ClientApplicationGUI() {
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(600, 400);
        this.setLocationRelativeTo(null);
        this.setTitle("Client Application");
        this.setLayout(new BorderLayout());

        fillGUI();
    }
    public void fillGUI(){
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        /*JPanel usernamePanel = new JPanel();
        usernamePanel.setLayout(new GridLayout(1, 3));
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                username = usernameField.getText();
            }
        });*/
        /*usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameField);
        usernamePanel.add(submitButton);*/
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BorderLayout());
        JLabel messageLabel = new JLabel("Message:");
        JTextField messageField = new JTextField();
        messagePanel.add(messageLabel, BorderLayout.WEST);
        messagePanel.add(messageField, BorderLayout.CENTER);
        //topPanel.add(usernamePanel, BorderLayout.NORTH);
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

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lastMessage = new Message(username, messageField.getText());
                messages.add(lastMessage);
                isNewMessageAppeared = true;
                messageField.setText("");
            }
        });
        this.add(topPanel, BorderLayout.NORTH);
        this.add(mainPanel, BorderLayout.CENTER);
        this.add(submitButton, BorderLayout.SOUTH);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static void addMessage(Message m){
        messages.add(m);
        messageDefaultListModel.addElement(m);
    }

    public Boolean getNewMessageAppeared() {
        return isNewMessageAppeared;
    }

    public void setNewMessageAppeared(Boolean newMessageAppeared) {
        isNewMessageAppeared = newMessageAppeared;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    @Override
    public void run() {
        this.setVisible(true);
    }
}
