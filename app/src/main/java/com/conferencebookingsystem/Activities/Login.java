package com.conferencebookingsystem.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.conferencebookingsystem.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class Login extends AppCompatActivity {

    TextView txtCreateAccount;
    Button btnLogin;

    private EditText txtPassword;
    private EditText txtUsername;
    AsyncTask<String, Void, String> asyncLoginAPI;
    String token;
    private SharedPreferences.Editor editor;
    JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txtPassword = findViewById(R.id.txtPassword);
        txtUsername = findViewById(R.id.txtUserNamee);
        SharedPreferences userPassword = getSharedPreferences("customerPassword",MODE_PRIVATE);

        SharedPreferences receivedToken = getSharedPreferences("tokenAPI",MODE_PRIVATE);
        editor = receivedToken.edit();

        Button start = (Button)findViewById(R.id.btnLogin);

        start.setEnabled(false);

        final String password = userPassword.getString("password",null);

        // disablar password för användaren
        txtPassword.setEnabled(false);

        if(password != null){ txtPassword.setText(password); }

        startButtonLock();
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asyncLoginAPI = new LoginApi();
                asyncLoginAPI.execute("https://dev-be.timetomeet.se/service/rest/api-token-auth/", jsonUsernamePassword());
            }
        });

        //Christian har skrivit den här metoden
        txtCreateAccount = findViewById(R.id.txtCreateAccount);
        txtCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, CreateAccount.class));

            }
        });
    }

    // Nusret
    // Låser upp startknappen när username och password är ifyllt
    public void startButtonLock() {
        btnLogin = findViewById(R.id.btnLogin);

        if (!txtUsername.getText().equals("") && !txtPassword.getText().equals("")){

            btnLogin.setEnabled(true);
        }
        else {
            btnLogin.setEnabled(false);
        }
    }
    // Nusret
    // Trycker ner programmet istället för att gå till splashscreen
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        //normal back button: super.onBackPressed();
    }

    private String jsonUsernamePassword(){
        String zx = "z_";
        String jsonUsernamePassword ="{" +
                "    \"username\": " + "\"" + zx + txtUsername.getText() + "\"," +
                "    \"password\": " + "\"" + txtPassword.getText() + "\"" +
                "}";
        return jsonUsernamePassword;
    }

    private class LoginApi extends AsyncTask<String, Void, String> {
        private String responseContent;
        private String Error = null;

        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(Login.this,"Logging in..",Toast.LENGTH_SHORT).show();

        }

        protected String doInBackground(String... requestData) {
            BufferedReader reader=null;

            try {
                URL url = new URL(requestData[0]);  // https://dev-be.timetomeet.se/service/rest/api-token-auth/

                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept","application/json");
                connection.setDoOutput(true); // True för POST, PUT. False för GET

                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                wr.write(requestData[1]); // { "username": ....
                wr.flush();

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line ;
                while((line = reader.readLine()) != null) {
                    sb.append(line + "  \n");
                }

                responseContent = sb.toString();
                jsonObject = new JSONObject(responseContent);

            }
            catch(Exception ex) {
                Error = ex.getMessage();
            }
            finally {
                try {
                    reader.close();
                }
                catch(Exception ex) {

                }
            }

            return responseContent;
        }

        protected void onPostExecute(String result) {

            if(responseContent != null) {
                try {

                    token = jsonObject.getString("token");
                    editor.putString("token", token);
                    editor.commit();

                    Thread.sleep(2000);
                    //Toast.makeText(Login.this, "Please wait!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Login.this, Search.class));
                } catch (InterruptedException | JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(Login.this,"Wrong username.Please try again..",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
