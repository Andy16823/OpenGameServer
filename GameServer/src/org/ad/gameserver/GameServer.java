package org.ad.gameserver;

import org.ad.gameserver.behaviors.ClientsBehavior;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringJoiner;
import java.util.Vector;

/**
 * This class handles the client connections.
 */
public class GameServer {
    private int port = 9091;
    private Map<String, String> clients;
    private Vector<ServerBehavior> behaviors;

    /**
     * Creates a new game server instance
     */
    public GameServer() {
        this.clients = new Hashtable<>();
        this.behaviors = new Vector<>();
    }

    /**
     * Creats a new game server instance
     * @param port
     */
    public  GameServer(int port) {
        this.clients = new Hashtable<>();
        this.port = port;
        this.behaviors = new Vector<>();
    }

    /**
     * Starts the game server
     */
    public void StartServer() {
        System.out.println("Starting Server");
        try {
            ServerSocket socket = new ServerSocket(port);
            while(true)
            {
                Socket client = socket.accept();
                System.out.println("New connection");
                ClientHandler handler = new ClientHandler(this, client);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the client messages
     * @return the client messages hash map
     */
    public Map<String, String> GetClients() {
        return this.clients;
    }

    /**
     * Updates the message from a client
     * @param uuid the client
     * @param data the message
     */
    public void UpdateClient(String uuid, String data) {
        this.clients.put(uuid, data);
    }

    /**
     * Creates an test client
     * @param name
     * @param x
     * @param y
     * @param z
     */
    public void AddTestClient(String name, float x, float y, float z) {
        JSONObject client = new JSONObject();

        JSONObject positon = new JSONObject();
        positon.put("x", x);
        positon.put("y", y);
        positon.put("z", z);
        client.put("position", positon);

        JSONObject rotation = new JSONObject();
        positon.put("x", 0f);
        positon.put("y", 0f);
        positon.put("z", 0f);
        client.put("rotation", rotation);

        client.put("uuid", name);

        this.clients.put(name, client.toString());
    }

    /**
     * Get the client messages as string seperated by ";"
     * @return the client messages as string
     */
    public String GetClientsStr() {
        StringJoiner builder = new StringJoiner(";");
        for ( Map.Entry<String, String> entry : clients.entrySet() ) {
            builder.add(entry.getValue());
        }
        return builder.toString();
    }

    /**
     * Get the client messages as json
     * @return the client messages as json object string
     */
    public String GetCleintsJson() {
        JSONArray array = new JSONArray();
        for ( Map.Entry<String, String> entry : clients.entrySet() ) {
            JSONObject object = new JSONObject(entry.getValue());
            array.put(object);
        }
        JSONObject root = new JSONObject();
        root.put("Clients", array);

        return root.toString();
    }

    /**
     * Adds an new behavior to the game server
     * @param behavior
     */
    public void AddBehavior(ServerBehavior behavior) {
        this.behaviors.add(behavior);
    }

    /**
     * Gets all behaviors from the game server
     * @return
     */
    public Vector<ServerBehavior> GetBehaviors() {
        return behaviors;
    }

    /**
     * Gets the behavior with the class name n
     * @param className the class name from the behavior
     * @return
     */
    public ServerBehavior GetBehavior(String className) {
        for(var b : this.behaviors) {
            if(b.getClass().getName().equals(className)) {
                return b;
            }
        }
        return null;
    }

}