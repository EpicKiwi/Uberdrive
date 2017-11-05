package fr.epickiwi.uberdrive;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import fr.epickiwi.uberdrive.api.ApiRequest;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameText;
    private EditText passwordText;
    private EditText passwordCheckText;

    private Button registerButton;

    private TextView errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.usernameText = findViewById(R.id.usernameText);
        this.passwordText = findViewById(R.id.passwordText);
        this.passwordCheckText = findViewById(R.id.passwordCheckText);
        this.registerButton = findViewById(R.id.registerButton);
        this.errorText = findViewById(R.id.errorText);

        this.registerButton.setOnClickListener(new OnRegisterClick());

        String previousUsername = getIntent().getExtras().getString("username");
        String previousPassword = getIntent().getExtras().getString("password");

        if(previousUsername != null)
            this.usernameText.setText(previousUsername);

        if(previousPassword != null)
            this.passwordText.setText(previousPassword);

    }

    private class OnRegisterClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            if(Objects.equals(usernameText.getText().toString(), "")) {
                usernameText.setError(getString(R.string.emptyField));
                return;
            }

            if(Objects.equals(passwordText.getText().toString(), "")) {
                passwordText.setError(getString(R.string.emptyField));
                return;
            }

            if(!Objects.equals(passwordText.getText().toString(), passwordCheckText.getText().toString())) {
                passwordCheckText.setError(getString(R.string.passwordMatchError));
                return;
            }

            registerButton.setEnabled(false);
            new RegisterRequestTask().execute();

        }
    }

    public class RegisterRequestTask extends AsyncTask<Void,Void,JSONObject> {

        JSONObject result;
        Exception error;

        @Override
        protected JSONObject doInBackground(Void... voids) {

            String username = usernameText.getText().toString();
            String password = passwordText.getText().toString();

            String requestAction = getString(R.string.baseRequestUrl)+"registerUser?username="+username+"&password="+password;

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
                registerButton.setEnabled(true);
                errorText.setText(getString(R.string.genericError));
                errorText.setVisibility(View.VISIBLE);
                return;
            }

            finish();
        }
    }

}
