package fr.epickiwi.uberdrive;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Space;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import fr.epickiwi.uberdrive.View.SpaceshipAdapter;
import fr.epickiwi.uberdrive.api.ApiRequest;
import fr.epickiwi.uberdrive.model.Spaceship;

public class SpaceshipChoiceActivity extends AppCompatActivity {

    private String username;
    private String token;

    private SpaceshipAdapter recyclerAdapter;
    private RecyclerView spaceshipRecyclerView;
    private String planetName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spaceship_choice);

        this.planetName = getIntent().getExtras().getString("planetName");
        if(this.planetName == null){
            Log.e("Error","Need a planet name to start simulation");
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle(getString(R.string.spaceshipSelection));
        setSupportActionBar(toolbar);

        SharedPreferences preferences = getSharedPreferences(getString(R.string.sharedPreferenceName), Context.MODE_PRIVATE);
        this.username = preferences.getString("username",null);
        this.token = preferences.getString("token",null);

        this.spaceshipRecyclerView = findViewById(R.id.shipRecycler);
        this.spaceshipRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerAdapter = new SpaceshipAdapter(this.planetName);
        this.spaceshipRecyclerView.setAdapter(this.recyclerAdapter);

        new SpaceshipRequestTask().execute();
    }

    public class SpaceshipRequestTask extends AsyncTask<Void,Void,JSONObject> {

        JSONObject result;
        Exception error;

        @Override
        protected JSONObject doInBackground(Void... voids) {

            String requestAction = getString(R.string.baseRequestUrl)+"getAllStarships?token="+token;

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

            JSONArray spaceships = null;
            try {
                spaceships = jsonObject.getJSONArray("starships");
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

            ArrayList<Spaceship> spaceshipList = recyclerAdapter.getSpaceshipList();
            spaceshipList.clear();
            for(int i = 0; i<spaceships.length(); i++){
                JSONObject jsspaceship = null;
                Spaceship ship = new Spaceship();
                try {
                    jsspaceship = (JSONObject) spaceships.get(i);
                    ship.fromJson(jsspaceship);
                } catch (JSONException e) {e.printStackTrace();continue;}
                spaceshipList.add(ship);
            }
            recyclerAdapter.notifyDataSetChanged();
        }
    }
}
