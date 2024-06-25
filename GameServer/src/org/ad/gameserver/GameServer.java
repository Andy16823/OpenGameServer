package org.ad.gameserver;

import org.ad.gameserver.behaviors.ClientsBehavior;
import org.ad.gameserver.math.Vec3;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringJoiner;
import java.util.Vector;

/**
 * This class handles the client connections.
 */
public class GameServer {
    private int port = 9091;
    private Map<String, IGameElement> gameElements;
    private Vector<ServerBehavior> behaviors;
    private boolean run;
    private boolean debug = false;
    private StringBuilder serverLog;
    public Vector<ServerCallbacks> callbacks;

    /**
     * Creates a new game server instance
     */
    public GameServer() {
        this.behaviors = new Vector<>();
        this.gameElements = new Hashtable<>();
        this.serverLog = new StringBuilder();
        this.callbacks = new Vector<>();
    }

    /**
     * Creats a new game server instance
     * @param port
     */
    public  GameServer(int port) {
        this.port = port;
        this.behaviors = new Vector<>();
        this.serverLog = new StringBuilder();
        this.callbacks = new Vector<>();
    }

    public void SetPort(int port) {
        this.port = port;
    }

    public boolean isConnected() {
        return run;
    }

    public void stopServer() {
        this.run = false;
    }

    public void addCallback(ServerCallbacks callback) {
        this.callbacks.add(callback);
    }

    public StringBuilder getServerLog() {
        return serverLog;
    }

    /**
     * Starts the game server
     */
    public void StartServer() {
        log("Starting Server");
        try {
            run = true;
            ServerSocket socket = new ServerSocket(port);
            while(run)
            {
                Socket client = socket.accept();
                log("New connection");
                ClientHandler handler = new ClientHandler(this, client);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, IGameElement> GetElements() {
        return this.gameElements;
    }

    public IGameElement getElement(String uuid) {
        return gameElements.get(uuid);
    }

    public void AddTestClient(String name, float x, float y, float z) {
        var location = new Vec3(x,y,z);
        var rotation = new Vec3(0,0,0);
        var scale = new Vec3(1, 1, 1);
        var gameElement = new GameElement(name, location, rotation, scale);
        this.gameElements.put(name, gameElement);
    }

    public void addGameElement(IGameElement gameElement) {
        this.gameElements.put(gameElement.uuid, gameElement);
    }

    public void removeElement(String uuid) {
        if(gameElements.containsKey(uuid)) {
            gameElements.remove(uuid);
        }
    }

    /**
     * Get the client messages as json
     * @return the client messages as json object string
     */
    public String GetElementsString() {
        return GetElementsJson().toString();
    }

    public JSONObject GetElementsJson() {
        JSONArray array = new JSONArray();
        for (var entry : gameElements.entrySet() ) {
            var object = entry.getValue().toJson();
            array.put(object);
        }
        JSONObject root = new JSONObject();
        root.put("clients", array);

        return root;
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

    public String buildResponse(JSONObject response) {
        JSONObject responseObject = new JSONObject();
        responseObject.put("timestamp", System.currentTimeMillis());
        responseObject.put("data", response);
        return responseObject.toString();
    }

    public void log(String message) {
        if(debug) {
            LocalDateTime now = LocalDateTime.now();
            String time = now.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));

            message = "[" + time + "]:" + message;

            this.serverLog.append(message + "\n");
            System.out.println(message);

            for(var callback : this.callbacks) {
                callback.onServerLog(this, message);
            }
        }
    }

}