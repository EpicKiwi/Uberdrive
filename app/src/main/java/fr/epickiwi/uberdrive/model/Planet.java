package fr.epickiwi.uberdrive.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Planet {

    private String name;
    private String climate;
    private String terrain;
    private int diameter;
    private double gravity;
    private int population;
    private int distance;

    public Planet(){};

    public Planet(String name, String climate, String terrain, int diameter, double gravity, int population) {
        this.name = name;
        this.climate = climate;
        this.terrain = terrain;
        this.diameter = diameter;
        this.gravity = gravity;
        this.population = population;
    }

    public void fromJson(JSONObject jsplanet){
        try{
            this.setName(jsplanet.getString("name"));
        } catch (JSONException ignored) {}
        try{
            this.setClimate(jsplanet.getString("climate"));
        } catch (JSONException ignored) {}
        try{
            this.setTerrain(jsplanet.getString("terrain"));
        } catch (JSONException ignored) {}
        try{
            this.setDiameter(jsplanet.getInt("diameter"));
        } catch (JSONException ignored) {}
        try{
            this.setGravity(jsplanet.getDouble("gravity"));
        } catch (JSONException ignored) {}
        try{
            this.setPopulation(jsplanet.getInt("population"));
        } catch (JSONException ignored) {}
        try{
            this.setDistance(jsplanet.getInt("distance"));
        } catch (JSONException ignored) {}
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClimate() {
        return climate;
    }

    public void setClimate(String climate) {
        this.climate = climate;
    }

    public String getTerrain() {
        return terrain;
    }

    public void setTerrain(String terrain) {
        this.terrain = terrain;
    }

    public int getDiameter() {
        return diameter;
    }

    public void setDiameter(int diameter) {
        this.diameter = diameter;
    }

    public double getGravity() {
        return gravity;
    }

    public void setGravity(double gravity) {
        this.gravity = gravity;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
