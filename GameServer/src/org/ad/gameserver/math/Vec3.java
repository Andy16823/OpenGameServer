package org.ad.gameserver.math;

import org.json.JSONObject;

import java.math.BigDecimal;

public class Vec3 {
    public float x;
    public float y;
    public float z;

    public Vec3() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Vec3(JSONObject jsonObject) {
        this.setData(jsonObject);
    }

    public Vec3(int value) {
        this.x = value;
        this.y = value;
        this.z = value;
    }

    public Vec3(float x,float y,float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public JSONObject toJson() {
        JSONObject vecJson = new JSONObject();
        vecJson.put("x", x);
        vecJson.put("y", y);
        vecJson.put("z", z);
        return vecJson;
    }

    public void setData(JSONObject object)
    {
        this.x = getFloatFromObject(object.get("x"));
        this.y = getFloatFromObject(object.get("y"));
        this.z = getFloatFromObject(object.get("z"));
    }

    public Vec3 lerp(Vec3 target, float t) {
        float newX = this.x + (target.x - this.x) * t;
        float newY = this.y + (target.y - this.y) * t;
        float newZ = this.z + (target.z - this.z) * t;
        return new Vec3(newX, newY, newZ);
    }

    private float getFloatFromObject(Object obj) {
        if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).floatValue();
        } else if (obj instanceof Number) {
            return ((Number) obj).floatValue();
        } else {
            throw new IllegalArgumentException("Invalid type for conversion to float: " + obj.getClass().getName());
        }
    }

}
