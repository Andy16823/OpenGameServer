package org.ad.gameserver;

import org.json.JSONObject;
import java.io.*;
import java.net.Socket;
import java.util.UUID;

/**
 * Creates a new task for the client connection
 */
public class ClientHandler implements Runnable {
    private Socket client;
    private GameServer server;
    private String uuid;
    private long lastTick;

    /**
     * Creates a new instance of the client handler
     * @param server
     * @param client
     */
    public ClientHandler(GameServer server, Socket client) {
        this.client = client;
        this.server = server;
        this.lastTick = server.lastServerTick();
    }

    public String getUuid() {
        return uuid;
    }

    /**
     * Runs the message
     */
    @Override
    public void run() {

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String message;

            while(server.isConnected()) {
                long now = System.currentTimeMillis();
                if(now >= server.nextTick(lastTick)) {
                    var ellapsed = now - lastTick;
                    server.log("Tick Ellapsed time " + ellapsed);
                    message = reader.readLine();

                    //server.log(message);
                    JSONObject object = new JSONObject(message);

                    //Command parse
                    if(object.get("cmd").toString().equals("GENERATE_CLIENT_ID")) {
                        this.uuid = UUID.randomUUID().toString();
                        this.SendMessage(uuid);
                    }

                    for(var behavior : this.server.GetBehaviors()) {
                        behavior.ParseCommand(this.server, this, object, message);
                    }

                    lastTick = System.currentTimeMillis();
                }
            }
            client.close();
        } catch (IOException e) {
            //throw new RuntimeException(e);
            if(!this.uuid.isEmpty()) {
                server.removeElement(this.uuid);
            }
        }
    }

    /**
     * Sends a message to the client
     * @param message
     */
    public void SendMessage(String message) {
        try {
            OutputStream outputStream = client.getOutputStream();
            PrintWriter writer = new PrintWriter(outputStream, true);
            writer.println(message);
            writer.flush();
        } catch (IOException e) {
            //throw new RuntimeException(e);
        }
    }

    /**
     * Gets the client socket
     * @return
     */
    public Socket GetSocket() {
        return this.client;
    }
}