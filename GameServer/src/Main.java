import org.ad.gameserver.GameServer;
import org.ad.gameserver.behaviors.ClientsBehavior;
import org.ad.gameserver.behaviors.Debug;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringJoiner;

public class Main {

    public static void main(String[] args) {
        GameServer gameServer = new GameServer();
        gameServer.AddBehavior(new ClientsBehavior());
        gameServer.AddBehavior(new Debug());
        gameServer.AddTestClient("Test", 1f, 0f, 1f);
        gameServer.StartServer();
    }
}

/**
 * Create a new instance for the game server
 */
