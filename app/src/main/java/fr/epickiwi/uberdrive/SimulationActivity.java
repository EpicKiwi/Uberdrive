package fr.epickiwi.uberdrive;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import fr.epickiwi.uberdrive.api.ApiRequest;
import fr.epickiwi.uberdrive.model.Planet;
import fr.epickiwi.uberdrive.model.Spaceship;

public class SimulationActivity extends AppCompatActivity {

    private String username;
    private String token;
    private Planet location;
    private String destinationName, spaceshipName;
    private Planet destination;
    private Spaceship spaceship;
    private View planetStartInclude;
    private View planetDestinationInclude;
    private View spaceshipInclude;
    private TextView distanceText;
    private TextView durationText;
    private double duration;
    private double totalPrice;
    private TextView totalPriceText;
    private FrameLayout loadingPanel;
    private View completedPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        Bundle extras = getIntent().getExtras();
        this.destinationName = extras.getString("planetName");
        this.spaceshipName = extras.getString("spaceshipName");

        SharedPreferences preferences = getSharedPreferences(getString(R.string.sharedPreferenceName), Context.MODE_PRIVATE);
        this.username = preferences.getString("username",null);
        this.token = preferences.getString("token",null);

        this.planetStartInclude = findViewById(R.id.startPlanetInclude);
        this.planetDestinationInclude = findViewById(R.id.destinationPlanetInclude);
        this.spaceshipInclude = findViewById(R.id.spaceshipInclude);
        this.distanceText = findViewById(R.id.distanceText);
        this.durationText = findViewById(R.id.durationText);
        this.totalPriceText = findViewById(R.id.totalPriceText);
        this.loadingPanel = findViewById(R.id.loadingPanel);
        this.completedPanel = findViewById(R.id.completedPanel);
        final Button validateButton = findViewById(R.id.validateButton);
        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateButton.setEnabled(false);
                new CreateShipmentRequestTask().execute();
            }
        });

        Button backbutton = findViewById(R.id.backButton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        new UserInfoRequestTask().execute();
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
                JSONObject jslocation = user.getJSONObject("location");
                location = new Planet();
                location.fromJson(jslocation);
            } catch (JSONException e) {
                e.printStackTrace();
                finish();
                return;
            }

            TextView nametext = planetStartInclude.findViewById(R.id.planetNameText);
            TextView climateText = planetStartInclude.findViewById(R.id.planetClimate);
            TextView distanceText = planetStartInclude.findViewById(R.id.planetDistanceText);

            distanceText.setVisibility(View.GONE);
            nametext.setText(location.getName());
            climateText.setText(location.getClimate());

            new GetSimulationRequestTask().execute();
        }
    }

    public class GetSimulationRequestTask extends AsyncTask<Void,Void,JSONObject> {

        JSONObject result;
        Exception error;

        @Override
        protected JSONObject doInBackground(Void... voids) {

            String requestAction = getString(R.string.baseRequestUrl)+"simulateShipment?fromPlanet="+location.getName()
                    +"&toPlanet="+destinationName
                    +"&starship="+spaceshipName
                    +"&token="+token;

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
                JSONObject jsdestination = jsonObject.getJSONObject("to");
                destination = new Planet();
                destination.fromJson(jsdestination);
                JSONObject jsspaceship = jsonObject.getJSONObject("starship");
                spaceship = new Spaceship();
                spaceship.fromJson(jsspaceship);
                destination.setDistance(jsonObject.getInt("distance"));
            } catch (JSONException e) {
                e.printStackTrace();
                finish();
                return;
            }

            TextView nametext = planetDestinationInclude.findViewById(R.id.planetNameText);
            TextView climateText = planetDestinationInclude.findViewById(R.id.planetClimate);
            TextView planetDistanceText = planetDestinationInclude.findViewById(R.id.planetDistanceText);

            planetDistanceText.setVisibility(View.GONE);
            nametext.setText(destination.getName());
            climateText.setText(destination.getClimate());

            TextView spaceshipNametext = spaceshipInclude.findViewById(R.id.shipNameText);
            TextView spaceshipPricetext = spaceshipInclude.findViewById(R.id.shipPriceText);
            TextView spaceshipSpeedtext = spaceshipInclude.findViewById(R.id.shipSpeedText);

            spaceshipNametext.setText(spaceship.getName());
            spaceshipPricetext.setText(getString(R.string.shipPriceUnit,String.valueOf(spaceship.getPrice())));
            spaceshipSpeedtext.setText(getString(R.string.shipSpeedUnit,spaceship.getSpeed()));

            distanceText.setText(getString(R.string.distanceUnit,destination.getDistance()));
            duration = destination.getDistance()/((double)spaceship.getSpeed());
            durationText.setText(getString(R.string.durationUnit,String.valueOf(Math.round(duration*100.0)/100.0)));
            totalPrice = spaceship.getPrice()*destination.getDistance();
            totalPriceText.setText(getString(R.string.priceUnit,String.valueOf(Math.round(totalPrice*100.0)/100.0)));

            loadingPanel.setVisibility(View.GONE);
        }
    }

    public class CreateShipmentRequestTask extends AsyncTask<Void,Void,JSONObject> {

        JSONObject result;
        Exception error;

        @Override
        protected JSONObject doInBackground(Void... voids) {

            String requestAction = getString(R.string.baseRequestUrl)+"createShipment?username="+username
                    +"&fromPlanet="+location.getName()
                    +"&toPlanet="+destinationName
                    +"&starship="+spaceshipName
                    +"&token="+token;

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

            completedPanel.setVisibility(View.VISIBLE);
        }
    }
}
