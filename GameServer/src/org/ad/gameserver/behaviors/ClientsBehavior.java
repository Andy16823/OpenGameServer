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
    public void ParseCommand(GameServer gameServer, ClientHandler client, JSONObject object, String rawmsg) {
        if(object.get("cmd").toString().equals("SET_CLIENT_DATA")) {
            gameServer.log("Reciving client data from: " + object.get("clientID"));

            var uuid = (String) object.get("clientID");
            var gameElement = gameServer.getElement(uuid);
            if(gameElement != null) {
                gameElement.updateElement((JSONObject) object.get("promt"));
            }
            else {
                gameElement = new GameElement(uuid, (JSONObject) object.get("promt"));
                gameServer.addGameElement(gameElement);
            }

            gameServer.log("Send clients data to: " + object.get("clientID"));
            gameServer.log("Connections: " + gameServer.GetElements().size());
            var sendData = gameServer.GetElementsJson();
            client.SendMessage(gameServer.buildResponse(sendData));
        }
    }
}
