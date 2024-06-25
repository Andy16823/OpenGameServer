package org.ad.gameserver;

import org.ad.gameserver.math.Vec3;
import org.json.JSONObject;

import java.util.Hashtable;

public class GameElement extends IGameElement{

    public  GameElement(String uuid, JSONObject jsonObject) {
        this.uuid = uuid;
        this.scale = new Vec3(1);
        this.extra = new Hashtable<>();

        this.location = new Vec3((JSONObject) jsonObject.get("position"));
        this.rotation = new Vec3((JSONObject) jsonObject.get("rotation"));
        if(jsonObject.has("extra")) {
            this.parseExtra((JSONObject) jsonObject.get("extra"));
        }
    }

    public GameElement(String uuid, Vec3 loaction, Vec3 rotation, Vec3 scale) {
        this.uuid = uuid;
        this.location = loaction;
        this.rotation = rotation;
        this.scale = scale;
        this.extra = new Hashtable<>();
    }

    @Override
    public JSONObject toJson() {
        JSONObject response = new JSONObject();
        response.put("position", location.toJson());
        response.put("rotation", rotation.toJson());
        response.put("scale", scale.toJson());
        response.put("uuid", uuid);
        response.put("lastUpdate", lastUpdate);

        JSONObject extrasJson = new JSONObject();
        for(var entry : extra.entrySet()) {
            extrasJson.put(entry.getKey(), entry.getValue());
        }
        response.put("extra", extrasJson);
        return response;
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }

    @Override
    public void parseJson(JSONObject jsonObject) {
        this.location.setData((JSONObject) jsonObject.get("position"));
        this.rotation.setData((JSONObject) jsonObject.get("rotation"));
        if(jsonObject.has("extra")) {
            this.parseExtra((JSONObject) jsonObject.get("extra"));
        }
    }

    @Override
    public void parseJson(String json) {
        JSONObject jsonObject = new JSONObject(json);
        this.parseJson(jsonObject);
    }

    @Override
    public void updateElement(JSONObject jsonObject) {
        this.location.setData((JSONObject) jsonObject.get("position"));
        this.rotation.setData((JSONObject) jsonObject.get("rotation"));
        this.lastUpdate = System.currentTimeMillis();

        if(jsonObject.has("extra")) {
            this.parseExtra((JSONObject) jsonObject.get("extra"));
        }
    }

    private void parseExtra(JSONObject extras)
    {
        this.extra.clear();
        for(var key : extras.keySet()) {
            Object object = extras.get(key);
            this.extra.put(key, object);
        }
    }

}
