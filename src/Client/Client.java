package Client;

import javax.swing.*;
import Server.Server;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Objects;

public class Client extends JFrame {

    private static final int WIDTH = 450;
    private static final int HEIGHT = 550;

    private static final String TITLE = "CHAT";
    private static final String MSG_BTN_LOGIN = "LOGIN";
    private static final String MSG_BTN_SEND_MESSAGE = "SEND";
    private static final String MSG_DISCONNECT_CLIENT = "YOU'RE DISCONNECTED";
    private static final String MSG_CONNECTED_CLIENT = "YOU'RE CONNECTED";
    private static final String MSG_NO_CONNECTED_CLIENT = "YOU'RE NOT CONNECTED";
    private static final String MSG_NO_SERVER_CONNECTION = "NO SERVER CONNECTION";

    private final Server server;
    private JPanel header;
    private JTextArea areaLog;
    private JTextField tfLogin, tfPort, tfIPAddress, tfMessage;
    private JPasswordField jPasswordField;
    private boolean isConnected;
    private String clientName;

    public Client(Server server) {
        this.server = server;
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(server);
        setTitle(TITLE);

        createMainPanel();
        setVisible(true);
    }

    public void answer(String text) {
        appendInLog(text);
    }

    private void appendInLog(String text) {
        areaLog.append(text + "\n");
    }

    private void connectToServer() {
        if (server.connectClient(this)) {
            isConnected = true;
            appendInLog(MSG_CONNECTED_CLIENT);
            header.setVisible(false);
            clientName = tfLogin.getText();
            String log = server.getLog();
            appendInLog(Objects.requireNonNullElse(log, MSG_NO_CONNECTED_CLIENT));
        }
    }

    public void disconnectFromServer() {
        if (isConnected) {
            header.setVisible(true);
            isConnected = false;
            appendInLog(MSG_DISCONNECT_CLIENT);
            server.disconnectClient(this);
        }
    }

    private void createMainPanel() {
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createLog());
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private Component createLog() {
        areaLog = new JTextArea();
        areaLog.setEditable(false);
        return new JScrollPane(areaLog);
    }

    private void message() {
        if (isConnected) {
            String textMessage = tfMessage.getText();
            if (!textMessage.equals("")) {
                server.message(String.format("%s: %s", clientName, textMessage));
                tfMessage.setText("");
            }
        } else {
            appendInLog(MSG_NO_SERVER_CONNECTION);
        }
    }

    private Component createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        tfMessage = new JTextField();
        tfMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == '\n') {
                    message();
                }
            }
        });
        panel.add(tfMessage);
        panel.add(createBtnSendMessage(), BorderLayout.EAST);
        return panel;
    }

    private Component createHeaderPanel() {
        header = new JPanel(new GridLayout(2, 3));
        tfLogin = new JTextField("enter login");
        tfPort = new JTextField();
        tfIPAddress = new JTextField("193.0.1.1");
        jPasswordField = new JPasswordField();

        header.add(tfIPAddress);
        header.add(tfPort);
        header.add(tfLogin);
        header.add(jPasswordField);
        header.add(createBtnLogin());
        return header;
    }

    private Component createBtnSendMessage() {
        JButton btnSendMessage = new JButton(MSG_BTN_SEND_MESSAGE);
        btnSendMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                message();
            }
        });
        return btnSendMessage;
    }

    private Component createBtnLogin() {
        JButton btnLogin = new JButton(MSG_BTN_LOGIN);
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectToServer();
            }
        });
        return btnLogin;
    }
}
