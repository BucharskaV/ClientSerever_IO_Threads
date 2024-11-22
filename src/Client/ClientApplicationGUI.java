package Client;

import Server.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ClientApplicationGUI extends JFrame implements Runnable{
    private List<String> messages = new ArrayList<String>();;
    private JList<String> messageList;
    private DefaultListModel<String> messageDefaultListModel = new DefaultListModel<>();
    private JTextArea importantInfoArea;
    private String mylastMessage;
    private Boolean isNewMyMessageAppeared;
    private String importantInfo = "";
    public ClientApplicationGUI() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(600, 400);
        this.setLocation(0, 0);
        this.setTitle("Client Application");
        this.setLayout(new BorderLayout());

        fillGUI();
        isNewMyMessageAppeared = false;
    }
    public void fillGUI(){
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());

        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BorderLayout());
        JLabel messageLabel = new JLabel("Message:");
        JTextField messageField = new JTextField();
        messagePanel.add(messageLabel, BorderLayout.WEST);
        messagePanel.add(messageField, BorderLayout.CENTER);
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
        JLabel importantInfoLabel = new JLabel("Important Info:");
        importantInfoArea = new JTextArea(importantInfo);
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
                mylastMessage = messageField.getText();
                setNewMyMessageAppeared(true);
                messageField.setText("");
            }
        });
        this.add(topPanel, BorderLayout.NORTH);
        this.add(mainPanel, BorderLayout.CENTER);
        this.add(submitButton, BorderLayout.SOUTH);
    }

    public void addMessage(String m){
        messages.add(m);
        messageDefaultListModel.addElement(m);
        importantInfoArea.setText(importantInfo);
    }

    public String getMylastMessage() {
        return mylastMessage;
    }

    public void setMylastMessage(String mylastMessage) {
        this.mylastMessage = mylastMessage;
    }

    public String getImportantInfo() {
        return importantInfo;
    }

    public void setImportantInfo(String importantInfo) {
        this.importantInfo += importantInfo;
        importantInfoArea.setText(this.importantInfo);
    }

    public void clearImportantInfo(){
        this.importantInfo = "";
    }


    public void setNewMyMessageAppeared(Boolean newMyMessageAppeared) {
        isNewMyMessageAppeared = newMyMessageAppeared;
    }

    public Boolean getNewMyMessageAppeared() {
        return isNewMyMessageAppeared;
    }

    @Override
    public void run() {
        this.setVisible(true);
    }
}
