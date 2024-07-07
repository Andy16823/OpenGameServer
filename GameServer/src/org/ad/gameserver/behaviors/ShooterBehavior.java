package org.ad.gameserver.behaviors;

import org.ad.gameserver.*;
import org.ad.gameserver.math.Vec3;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.management.relation.RelationNotFoundException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

public class ShooterBehavior implements ServerBehavior {

    private void checkRespawn(GameServer gameServer, JSONObject behaviorResponse) {
        JSONArray respawns = new JSONArray();

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
                            var spawns = (Vector<Vec3>) gameServer.getProperty("spawns");
                            if(spawns != null) {
                                Random random = new Random();
                                var spawnId = random.nextInt(spawns.size());
                                var spawn = spawns.get(spawnId);
                                //e.location = spawn;

                                /// Check if its working with 1 entity
                                JSONObject respawn = new JSONObject();
                                respawn.put("UUID", e.uuid);
                                respawn.put("POSITION", spawn.toJson());
                                respawns.put(respawn);
                            }
                        }
                    }
                }
            }
        }
        behaviorResponse.put("respawns", respawns);
    }

    private String getJarLocation() {
        try {
            var path =  new File(ShooterBehavior.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
            return path;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private float hexToFloat(String hexString) {
        int intBits = Integer.parseUnsignedInt(hexString.substring(2), 16);
        float floatValue = Float.intBitsToFloat(intBits);

        return floatValue;
    }

    private void loadMap(String mapName, GameServer server) {
        var mapFile = new File(this.getJarLocation() + "/" + mapName);
        try {
            var spawns = new Vector<>();
            String fileContent = Files.readString(mapFile.toPath(), StandardCharsets.ISO_8859_1);
            JSONObject jsonObject = new JSONObject(fileContent);
            JSONObject sceneTree = (JSONObject) jsonObject.get("scene");
            JSONArray entityTree = (JSONArray) sceneTree.get("entities");

            for(int i = 0; i < entityTree.length(); i++) {
                JSONObject entity = entityTree.getJSONObject(i);
                if(entity.has("tags")) {
                    JSONArray tags = (JSONArray) entity.get("tags");
                    for(var tag : tags) {
                        if(tag.toString().equals("spawn")) {
                            JSONArray position = (JSONArray) entity.get("position");
                            float x = hexToFloat(position.get(0).toString());
                            float y = hexToFloat(position.get(1).toString());
                            float z = hexToFloat(position.get(2).toString());
                            Vec3 spawnLocation = new Vec3(x, y, z);
                            spawns.add(spawnLocation);
                        }
                    }
                }
            }
            server.addProperty("spawns", spawns);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void OnServerStart(GameServer server) {
        server.log(this.getJarLocation());
        this.loadMap("map.ultra", server);
    }

    private void hitTarget(GameServer server, String uuid, int damage) {
        var element = server.getElement(uuid);
        if(element != null) {
            int currentHealth = (int) element.properties.get("hp");
            currentHealth -= damage;
            System.out.println(damage);
            if(currentHealth <= 0) {
                currentHealth = 0;
                element.properties.put("alive", false);
                element.properties.put("time_died", System.currentTimeMillis());
            }
            element.properties.put("hp", currentHealth);
            System.out.println("hit3");
        }
    }

    @Override
    public void ParseCommand(GameServer gameServer, ClientHandler client, JSONObject object, JSONObject response, String rawmsg) {

        JSONObject behaviorResponse = new JSONObject();

        checkRespawn(gameServer, behaviorResponse);

        if(object.get("cmd").toString().equals("SAY_HELLO")) {
            System.out.println("hello client");
        }
        else if(object.get("cmd").toString().equals("SET_CLIENT_DATA")) {
            JSONObject promt = (JSONObject) object.get("promt");
            JSONObject extras = (JSONObject) promt.get("extra");
            boolean isHit = (boolean) extras.get("isHit");
            if(isHit) {
                String hitTarget = (String) extras.get("hitTarget");
                int damage = (int) extras.get("damage");
                hitTarget(gameServer, hitTarget, damage);
            }
        }
        else if(object.get("cmd").toString().equals("ENEMY_HIT")) {
            JSONObject dataTable = (JSONObject) object.get("data");
            String enemyUuid = (String) dataTable.get("enemyUuid");

        }
        response.put("shooterBehavior", behaviorResponse);
    }

}
