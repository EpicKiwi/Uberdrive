package fr.epickiwi.uberdrive;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import fr.epickiwi.uberdrive.api.ApiRequest;
import fr.epickiwi.uberdrive.model.Planet;

public class PlanetActivity extends AppCompatActivity {

    private String planetName;
    private String token;
    private Planet planet;
    private TextView climatField;
    private TextView terrainField;
    private TextView populationField;
    private TextView diameterField;
    private TextView gravityField;
    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planet);

        this.planetName = getIntent().getExtras().getString("name");
        if(this.planetName == null){
            Log.e("Error","No planet name provided");
            finish();
            return;
        }

        SharedPreferences preferences = getSharedPreferences(getString(R.string.sharedPreferenceName), Context.MODE_PRIVATE);
        this.token = preferences.getString("token",null);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle(this.planetName);
        setSupportActionBar(toolbar);

        this.climatField = findViewById(R.id.climatPlanetField);
        this.terrainField = findViewById(R.id.terrainPlanetField);
        this.populationField = findViewById(R.id.populationPlanetField);
        this.diameterField = findViewById(R.id.diameterPlanetField);
        this.gravityField = findViewById(R.id.gravityPlanetField);
        this.startButton = findViewById(R.id.startButton);
        this.startButton.setOnClickListener(new OnStartButtonClick());

        new PlanetInfoRequestTask().execute();
    }

    private class OnStartButtonClick implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(PlanetActivity.this,SpaceshipChoiceActivity.class);
            intent.putExtra("planetName",planet.getName());
            startActivity(intent);
            finish();
        }
    }

    public class PlanetInfoRequestTask extends AsyncTask<Void,Void,JSONObject> {

        JSONObject result;
        Exception error;

        @Override
        protected JSONObject doInBackground(Void... voids) {

            String requestAction = getString(R.string.baseRequestUrl)+"getOnePlanet?name="+planetName+"&token="+token;

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
                JSONObject jsplanet = jsonObject.getJSONObject("planet");
                planet = new Planet();
                planet.fromJson(jsplanet);
            } catch (JSONException e) {
                e.printStackTrace();
                finish();
                return;
            }

            climatField.setText(planet.getClimate());
            terrainField.setText(planet.getTerrain());
            populationField.setText(getString(R.string.populationUnit,planet.getPopulation()));
            diameterField.setText(getString(R.string.diameterUnit,planet.getDiameter()));
            gravityField.setText(getString(R.string.gravityUnit,String.valueOf(planet.getGravity())));
        }
    }

}
