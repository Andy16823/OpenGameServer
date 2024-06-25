package org.ad.gameserver.behaviors;

import org.ad.gameserver.ClientHandler;
import org.ad.gameserver.GameElement;
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
            client.SendMessage(gameServer.buildResponse(gameServer.GetElementsJson()));
        }
        else if(request.get("cmd").toString().equals("CREATE_PLAYER")) {
            JSONObject promt = (JSONObject) request.get("promt");
            UUID uuid = UUID.randomUUID();
            var gameElement = new GameElement(uuid.toString(), promt);
            gameServer.addGameElement(gameElement);
            client.SendMessage("Player Created");
        }
    }
}
