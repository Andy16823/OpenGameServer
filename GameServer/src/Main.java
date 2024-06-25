import org.ad.gameserver.GameServer;
import org.ad.gameserver.ServerCallbacks;
import org.ad.gameserver.behaviors.ClientsBehavior;
import org.ad.gameserver.behaviors.Debug;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TimerTask;

public class Main implements ServerCallbacks, WindowListener {
    private JTextArea textArea;
    private GameServer gameServer;

    public Main() {
        gameServer = new GameServer();
        gameServer.AddBehavior(new ClientsBehavior());
        gameServer.AddBehavior(new Debug());

        JFrame jFrame = new JFrame("TestServer");
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setSize(400, 300);

        jFrame.addWindowListener(this);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JToolBar toolBar = new JToolBar();
        JTextField portField = new JTextField("9091");
        toolBar.add(portField);
        JButton connectBtn = new JButton("Start");
        toolBar.add(connectBtn);

        textArea = new JTextArea();
        mainPanel.add(textArea, BorderLayout.CENTER);

        gameServer.addCallback(this);

        connectBtn.addActionListener(e -> {
            if(connectBtn.getText() == "Start") {
                connectBtn.setText("Stop");
                Thread thread = new Thread(() -> {
                    gameServer.SetPort(Integer.parseInt(portField.getText()));
                    gameServer.AddTestClient("Test", 1f, 0f, 1f);
                    gameServer.StartServer();
                });
                thread.start();
            }else {
                connectBtn.setText("Start");
                gameServer.stopServer();
            }
        });

        mainPanel.add(toolBar, BorderLayout.NORTH);
        jFrame.add(mainPanel);
        jFrame.setVisible(true);
    }

    public static void main(String[] args) {
        Main main = new Main();
    }

    @Override
    public void onServerLog(GameServer server, String message) {
        Thread thread = new Thread(() -> {
           this.textArea.append(message + "\n");
        });
        thread.start();
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        gameServer.stopServer();
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
