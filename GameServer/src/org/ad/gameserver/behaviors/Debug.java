package org.ad.gameserver.behaviors;

import org.ad.gameserver.ClientHandler;
import org.ad.gameserver.GameServer;
import org.ad.gameserver.ServerBehavior;
import org.json.JSONObject;

import java.net.Socket;
import java.util.UUID;

/**
 * Creates an behavior for the debuging
 */
public class Debug implements ServerBehavior {

    /**
     * Parse the commands from the client
     * @param gameServer the server class
     * @param client the client handler
     * @param request the request as JObject
     * @param rawmsg the raw client message
     */
    @Override
    public void ParseCommand(GameServer gameServer, ClientHandler client, JSONObject request, String rawmsg) {
        if(request.get("cmd").toString().equals("GET_CLIENTS")) {
            System.out.println("Sending Client data");
            client.SendMessage(gameServer.GetCleintsJson());
        }
        else if(request.get("cmd").toString().equals("CREATE_PLAYER")) {
            JSONObject promt = (JSONObject) request.get("promt");
            JSONObject pos = (JSONObject) promt.get("position");
            JSONObject rot = (JSONObject) promt.get("rotation");
            UUID uuid = UUID.randomUUID();
            float posX = Float.parseFloat(pos.get("x").toString());
            float posY = Float.parseFloat(pos.get("y").toString());
            float posZ = Float.parseFloat(pos.get("z").toString());
            float rotX = Float.parseFloat(rot.get("x").toString());
            float rotY = Float.parseFloat(rot.get("y").toString());
            float rotZ = Float.parseFloat(rot.get("z").toString());
            String playerString = CreatePlayerString(uuid.toString(), posX, posY, posZ, rotX, rotY, rotZ);
            gameServer.UpdateClient(uuid.toString(), playerString);
            client.SendMessage("Player Created");
        }
    }

    /**
     * Creates an client string
     * @param uuid the uuid for the "virtual" client
     * @param locX the x location
     * @param locY the y location
     * @param locZ the z location
     * @param rotX the x rotation
     * @param rotY the y rotation
     * @param rotZ the z rotation
     * @return
     */
    public static String CreatePlayerString(String uuid, float locX, float locY, float locZ, float rotX, float rotY, float rotZ) {
        JSONObject client = new JSONObject();

        JSONObject positon = new JSONObject();
        positon.put("x", locX);
        positon.put("y", locY);
        positon.put("z", locZ);
        client.put("position", positon);

        JSONObject rotation = new JSONObject();
        positon.put("x", rotX);
        positon.put("y", rotY);
        positon.put("z", rotZ);
        client.put("rotation", rotation);

        client.put("uuid", uuid);

        return client.toString();
    }

}
