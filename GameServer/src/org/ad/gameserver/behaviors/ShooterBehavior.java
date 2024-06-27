package org.ad.gameserver.behaviors;

import org.ad.gameserver.*;
import org.json.JSONObject;

public class ShooterBehavior implements ServerBehavior {

    private void checkRespawn(GameServer gameServer) {
        long now = System.currentTimeMillis();
        for(var entry : gameServer.GetElements().entrySet()) {
            GameElement e = (GameElement) entry.getValue();
            if(e.properties != null) {
                if((boolean) e.properties.get("alive") == false) {
                    if((boolean) e.properties.get("allow_respawn")) {
                        long diedTime = (long) e.properties.get("time_died");
                        if(now > diedTime + 10000) {
                            e.properties.put("hp", 100);
                            e.properties.put("alive", true);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void ParseCommand(GameServer gameServer, ClientHandler client, JSONObject object, String rawmsg) {
        checkRespawn(gameServer);

        if(object.get("cmd").toString().equals("SAY_HELLO")) {
            System.out.println("hello client");
        }
        else if(object.get("cmd").toString().equals("ENEMY_HIT")) {
            JSONObject dataTable = (JSONObject) object.get("data");
            String enemyUuid = (String) dataTable.get("enemyUuid");
            var element = gameServer.getElement(enemyUuid);
            if(element != null) {
                int currentHealth = (int) element.properties.get("hp");
                int damage = dataTable.getInt("damage");
                currentHealth -= damage;
                if(currentHealth <= 0) {
                    currentHealth = 0;
                    element.properties.put("alive", false);
                    element.properties.put("time_died", System.currentTimeMillis());
                }
                element.properties.put("hp", currentHealth);
            }
        }
    }

}
