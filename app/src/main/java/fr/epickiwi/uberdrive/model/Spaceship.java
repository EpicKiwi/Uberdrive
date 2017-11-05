package fr.epickiwi.uberdrive.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Spaceship {

    private String name;
    private int speed;
    private double price;

    public void fromJson (JSONObject jsonObject){
        try{
            this.setName(jsonObject.getString("name"));
        } catch (JSONException ignored) {}
        try{
            this.setSpeed(jsonObject.getInt("speed"));
        } catch (JSONException ignored) {}
        try{
            this.setPrice(jsonObject.getDouble("price"));
        } catch (JSONException ignored) {}
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
