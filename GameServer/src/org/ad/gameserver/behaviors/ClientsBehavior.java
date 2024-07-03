package org.ad.gameserver.behaviors;

import org.ad.gameserver.ClientHandler;
import org.ad.gameserver.GameElement;
import org.ad.gameserver.GameServer;
import org.ad.gameserver.ServerBehavior;
import org.json.JSONObject;

import java.net.Socket;
import java.util.UUID;

public class ClientsBehavior implements ServerBehavior {
    @Override
    public void OnServerStart(GameServer server) {

    }

    @Override
    public void ParseCommand(GameServer gameServer, ClientHandler client, JSONObject requestObject, JSONObject response, String rawmsg) {
        if(requestObject.get("cmd").toString().equals("SET_CLIENT_DATA")) {
            gameServer.log("Reciving client data from: " + requestObject.get("clientID"));

            var uuid = (String) requestObject.get("clientID");
            var gameElement = gameServer.getElement(uuid);
            if(gameElement != null) {
                gameElement.updateElement((JSONObject) requestObject.get("promt"));
            }
            else {
                gameElement = new GameElement(uuid, (JSONObject) requestObject.get("promt"));
                // example server properties
                gameElement.addProperty("hp", 100);
                gameElement.addProperty("alive", true);
                gameElement.addProperty("time_died", 0);
                gameElement.addProperty("allow_respawn", true);
                gameElement.addProperty("last_hit", 0);
                gameServer.addGameElement(gameElement);
            }

            gameServer.log("Send clients data to: " + requestObject.get("clientID"));
            gameServer.log("Connections: " + gameServer.GetElements().size());
            var sendData = gameServer.GetElementsJson();
            response.put("clientsBehavior", sendData);
        }
    }
}
