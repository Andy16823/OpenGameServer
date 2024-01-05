package org.ad.gameserver.behaviors;

import org.ad.gameserver.ClientHandler;
import org.ad.gameserver.GameServer;
import org.ad.gameserver.ServerBehavior;
import org.json.JSONObject;

import java.net.Socket;
import java.util.UUID;

public class ClientsBehavior implements ServerBehavior {
    @Override
    public void ParseCommand(GameServer gameServer, ClientHandler client, JSONObject object, String rawmsg) {
        if(object.get("cmd").toString().equals("SET_CLIENT_DATA")) {
            System.out.println("Reciving client data from: " + object.get("clientID"));
            gameServer.UpdateClient(object.get("clientID").toString(), object.get("promt").toString());
            System.out.println("Send clients data to: " + object.get("clientID"));
            System.out.println("Connections: " + gameServer.GetClients().size());
            client.SendMessage(gameServer.GetCleintsJson());
        }
        else if(object.get("cmd").toString().equals("GENERATE_CLIENT_ID")) {
            long currentTimeMillis = System.currentTimeMillis();
            UUID uuid = UUID.randomUUID();
            client.SendMessage(uuid.toString());
        }
    }
}
