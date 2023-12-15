package Server;

import Client.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Server extends JFrame {

    private static final int WIDTH = 450;
    private static final int HEIGHT = 550;
    private static final int POS_X = 540;
    private static final int POS_Y = 360;

    private static final String TITLE = "SERVER";
    private static final String MSG_BTN_START = "START SERVER";
    private static final String MSG_BTN_STOP = "STOP SERVER";
    private static final String MSG_BTN_EXIT = "EXIT THE PROGRAM";
    private static final String MSG_SERVER_STARTED = "SERVER IS STARTED";
    private static final String MSG_SERVER_STARTED_YET = "SERVER IS STARTED YET";
    private static final String MSG_SERVER_STOPPED = "SERVER IS STOPPED";
    private static final String MSG_SERVER_STOPPED_YET = "SERVER IS STOPPED YET";
    private static final String FILE_LOG = "src/files/log.txt";
    private static final String FILE_EXCEPTION = "src/files/exceptions.txt";

    private JTextArea areaLog;
    private boolean isWork;
    private final List<Client> clients;

    public Server() {
        clients = new ArrayList<>();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLocation(POS_X, POS_Y);
        setSize(WIDTH, HEIGHT);
        setTitle(TITLE);

        createMainPanel();

        setVisible(true);
    }

    public String getLog() {
        return readLog();
    }

    private String readLog() {
        StringBuilder stringBuilder = new StringBuilder();
        try (FileReader fileReader = new FileReader(FILE_LOG)) {
            int c;
            while ((c = fileReader.read()) != -1) {
                stringBuilder.append((char) c);
            }
            stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveLogInFile(String text) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(FILE_LOG, true);
            fileWriter.write(text);
            fileWriter.write("\n");
        } catch (IOException e) {
            PrintWriter printWriter = null;
            try {
                printWriter = new PrintWriter(FILE_EXCEPTION);
            } catch (IOException ex) {
                ex.printStackTrace(printWriter);
            }
            e.printStackTrace(printWriter);
        } finally {
            try {
                assert fileWriter != null;
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void message(String text) {
        if (!isWork) {
            return;
        }
        appendInLog(text);
        saveLogInFile(text);
        answerAllClient(text);
    }

    private void answerAllClient(String text) {
        for (Client user : clients) {
            user.answer(text);
        }
    }

    public void disconnectClient(Client client) {
        clients.remove(client);
        if (client != null) {
            client.disconnectFromServer();
        }
    }

    public boolean connectClient(Client client) {
        if (!isWork) {
            return false;
        }
        clients.add(client);
        return true;
    }

    private void createMainPanel() {
        areaLog = new JTextArea();
        areaLog.append("LOG:" + "\n");
        saveLogInFile("LOG:");
        add(areaLog);
        add(createButtonsPanel(), BorderLayout.SOUTH);
    }

    private void appendInLog(String text) {
        areaLog.append(text + "\n");
    }

    private Component createButtonsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3));
        panel.add(createBtnStart());
        panel.add(createBtnStop());
        panel.add(createBtnExit());
        return panel;
    }

    private Component createBtnStart() {
        JButton btnStart = new JButton(MSG_BTN_START);
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isWork) {
                    appendInLog(MSG_SERVER_STARTED_YET);
                    saveLogInFile(MSG_SERVER_STARTED_YET);
                } else {
                    isWork = true;
                    appendInLog(MSG_SERVER_STARTED);
                    saveLogInFile(MSG_SERVER_STARTED);
                }
            }
        });
        return btnStart;
    }

    private Component createBtnStop() {
        JButton btnStop = new JButton(MSG_BTN_STOP);
        btnStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isWork) {
                    isWork = false;
                    for (Client user : clients) {
                        disconnectClient(user);
                    }
                    appendInLog(MSG_SERVER_STOPPED);
                } else {
                    appendInLog(MSG_SERVER_STOPPED_YET);
                    saveLogInFile(MSG_SERVER_STOPPED_YET);
                }
            }
        });
        return btnStop;
    }

    private Component createBtnExit() {
        JButton btnExit = new JButton(MSG_BTN_EXIT);
        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        return btnExit;
    }
}
