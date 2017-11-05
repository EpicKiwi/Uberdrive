package fr.epickiwi.uberdrive;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import fr.epickiwi.uberdrive.View.PlanetAdapter;
import fr.epickiwi.uberdrive.api.ApiRequest;
import fr.epickiwi.uberdrive.model.Planet;

public class MainActivity extends AppCompatActivity {

    private String username;
    private String token;
    private Planet userLocation;

    private TextView usernameTextView;
    private TextView locationTextView;

    private RecyclerView planetsRecyclerView;
    private PlanetAdapter recyclerAdapter;

    private FrameLayout loadingPanel;
    private FrameLayout userPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getSharedPreferences(getString(R.string.sharedPreferenceName),Context.MODE_PRIVATE);
        this.username = preferences.getString("username",null);
        this.token = preferences.getString("token",null);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.usernameTextView = findViewById(R.id.usernameTextView);
        this.locationTextView = findViewById(R.id.locationTextView);
        this.planetsRecyclerView = findViewById(R.id.planetRecyclerView);
        this.loadingPanel = findViewById(R.id.loadingPanel);
        this.userPanel = findViewById(R.id.userPanel);

        this.planetsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerAdapter = new PlanetAdapter();
        this.planetsRecyclerView.setAdapter(this.recyclerAdapter);

        this.userPanel.setOnClickListener(new OnClickUserPanel());

        new UserInfoRequestTask().execute();
    }

    private void disconnectUser(){
        SharedPreferences.Editor preferences = getSharedPreferences(getString(R.string.sharedPreferenceName),Context.MODE_PRIVATE).edit();
        preferences.remove("token");
        preferences.apply();
        startActivity(new Intent(MainActivity.this,LoginActivity.class));
        finish();
    }

    private class OnClickUserPanel implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            disconnectUser();
        }
    }

    public class UserInfoRequestTask extends AsyncTask<Void,Void,JSONObject> {

        JSONObject result;
        Exception error;

        @Override
        protected JSONObject doInBackground(Void... voids) {

            String requestAction = getString(R.string.baseRequestUrl)+"getUserByName?username="+username+"&token="+token;

            try {
                this.result = ApiRequest.getJSONObjectFromURL(requestAction);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                this.error = e;
            }
            return this.result;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if(this.error != null){
                finish();
                return;
            }

            try {
                JSONObject user = jsonObject.getJSONObject("user");
                JSONObject location = user.getJSONObject("location");
                usernameTextView.setText(user.getString("name"));
                locationTextView.setText(location.getString("name"));
                userLocation = new Planet();
                userLocation.fromJson(location);
            } catch (JSONException e) {
                e.printStackTrace();
                disconnectUser();
                finish();
                return;
            }

            new PlanetsRequestTask().execute();
        }
    }

    public class PlanetsRequestTask extends AsyncTask<Void,Void,JSONObject> {

        JSONObject result;
        Exception error;

        @Override
        protected JSONObject doInBackground(Void... voids) {

            String requestAction = getString(R.string.baseRequestUrl)+"getConnectedPlanets?initalPlanet="+userLocation.getName()+"&token="+token;

            try {
                this.result = ApiRequest.getJSONObjectFromURL(requestAction);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                this.error = e;
            }
            return this.result;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if(this.error != null){
                disconnectUser();
                finish();
                return;
            }

            JSONArray planets = null;
            try {
                planets = jsonObject.getJSONArray("planets");
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            ArrayList<Planet> planetList = recyclerAdapter.getPlanetList();
            planetList.clear();
            for(int i = 0; i<planets.length(); i++){
                JSONObject jsplanet = null;
                try {
                    jsplanet = (JSONObject) planets.get(i);
                } catch (JSONException e) {e.printStackTrace();continue;}
                Planet planet = new Planet();
                planet.fromJson(jsplanet);
                planetList.add(planet);
            }
            recyclerAdapter.notifyDataSetChanged();
            loadingPanel.setVisibility(View.GONE);
        }
    }

}
