package org.ad.gameserver;

import org.ad.gameserver.math.Vec3;
import org.json.JSONObject;

import java.util.Map;

public abstract class IGameElement {
    public String uuid;
    public Vec3 location;
    public Vec3 rotation;
    public Vec3 scale;
    public Map<String, Object> extra;
    public Map<String, Object> properties;
    public long lastUpdate = 0;

    public abstract JSONObject toJson();
    public abstract String toString();
    public abstract void parseJson(JSONObject jsonObject);
    public abstract void parseJson(String json);
    public abstract void updateElement(JSONObject jsonObject);
    public abstract void addProperty(String key, Object object);
}
