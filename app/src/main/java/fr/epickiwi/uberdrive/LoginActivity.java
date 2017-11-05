package fr.epickiwi.uberdrive;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;

import fr.epickiwi.uberdrive.api.ApiRequest;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private Button registerButton;

    private EditText usernameText;
    private EditText passwordText;

    private TextView errorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences preferences = getSharedPreferences(getString(R.string.sharedPreferenceName),Context.MODE_PRIVATE);
        String registeredUsername = preferences.getString("username",null);
        if( registeredUsername != null &&
           preferences.getString("token",null) != null){
            openApp();
        }

        this.loginButton = findViewById(R.id.LoginButton);
        this.registerButton = findViewById(R.id.registerButton);
        this.usernameText = findViewById(R.id.usernameText);
        this.passwordText = findViewById(R.id.passwordText);
        this.errorTextView = findViewById(R.id.errorText);

        if(registeredUsername != null){
            this.usernameText.setText(registeredUsername);
        }

        this.loginButton.setOnClickListener(new OnLoginClick());
        this.registerButton.setOnClickListener(new OnRegisterClick());
    }

    private void openApp(){
        startActivity(new Intent(LoginActivity.this,MainActivity.class));
        finish();
    }

    private class OnLoginClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            loginButton.setEnabled(false);
            registerButton.setEnabled(false);
            errorTextView.setVisibility(View.INVISIBLE);
            new LoginRequestTask().execute();

        }
    }

    private class OnRegisterClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
            registerIntent.putExtra("password",passwordText.getText().toString());
            registerIntent.putExtra("username",usernameText.getText().toString());
            startActivity(registerIntent);

        }
    }

    public class LoginRequestTask extends AsyncTask<Void,Void,JSONObject> {

        JSONObject result;
        Exception error;

        @Override
        protected JSONObject doInBackground(Void... voids) {

            String username = usernameText.getText().toString();
            String password = passwordText.getText().toString();

            String requestAction = getString(R.string.baseRequestUrl)+"getToken?username="+username+"&password="+password;

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
                loginButton.setEnabled(true);
                registerButton.setEnabled(true);
                if(this.error instanceof FileNotFoundException){
                    errorTextView.setText(getString(R.string.badLoginError));
                } else {
                    errorTextView.setText(getString(R.string.genericError));
                }
                errorTextView.setVisibility(View.VISIBLE);
                return;
            }

            try {
                String username = jsonObject.getString("username");
                String token = jsonObject.getString("token");
                SharedPreferences.Editor preferences = getSharedPreferences(getString(R.string.sharedPreferenceName),Context.MODE_PRIVATE).edit();
                preferences.putString("username",username);
                preferences.putString("token",token);
                preferences.apply();
                openApp();
            } catch (JSONException e) {
                e.printStackTrace();
                errorTextView.setText(getString(R.string.genericError));
                errorTextView.setVisibility(View.VISIBLE);
            }
        }
    }

}
