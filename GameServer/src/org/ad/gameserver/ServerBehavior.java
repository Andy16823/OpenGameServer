package org.ad.gameserver;

import org.ad.gameserver.GameServer;
import org.json.JSONObject;

import java.net.Socket;

/**
 * ServerBehavior Class
 * This class is used to handle the client commands.
 * You can inherit from this interface to create own command parsers.
 */
public interface ServerBehavior {
    public void ParseCommand(GameServer gameServer, ClientHandler client, JSONObject request, String rawmsg);
}
