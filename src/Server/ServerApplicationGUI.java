package Server;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ServerApplicationGUI extends JFrame {
    private static List<Message> messages = new ArrayList<Message>();;
    private static JList<Message> messageList;
    private static DefaultListModel<Message> messageDefaultListModel = new DefaultListModel<>();

    public ServerApplicationGUI() {
        this.setSize(new Dimension(600, 350));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setTitle("Server Application");

        messageList = new JList<>(messageDefaultListModel);
        messageList.setForeground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(messageList);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        this.add(scrollPane, BorderLayout.CENTER);
    }

    public static void addMessage(Message m){
        messages.add(m);
        messageDefaultListModel.addElement(m);
    }
}
