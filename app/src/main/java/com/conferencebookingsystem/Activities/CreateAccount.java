package com.conferencebookingsystem.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.conferencebookingsystem.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.URL;
import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;

public class CreateAccount extends AppCompatActivity {

    private Button buttonCreate;
    private RequestQueue mQueue;
    AsyncTask<String, Void, String> asyncCreateUserAPI;
    private TextView firstName, lastName, userName, password, email, phone, company, orgNo, street, zipCode, city;
    private SharedPreferences userPassword;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createaccount);

        buttonCreate = findViewById(R.id.btnCreate);
        buttonCreate.setEnabled(false);

        final EditText passwordField = findViewById(R.id.txtPassword);
        passwordField.setText(generateToken(8));
        userPassword = getSharedPreferences("customerPassword",MODE_PRIVATE);
        editor = userPassword.edit();

        mQueue = Volley.newRequestQueue(this);

        firstName = findViewById(R.id.txtFirstName);
        lastName = findViewById(R.id.txtLastName);
        userName = findViewById(R.id.txtUserName);
        password = findViewById(R.id.txtPassword);
        email = findViewById(R.id.txtEmail);
        phone = findViewById(R.id.txtPhone);
        company = findViewById(R.id.txtCompany);
        orgNo = findViewById(R.id.txtOrgno);
        street = findViewById(R.id.txtStreet);
        zipCode = findViewById(R.id.txtZipcode);
        city = findViewById(R.id.txtCity);
        buttonCreate = findViewById(R.id.btnCreate);

        asyncCreateUserAPI = new CreateAccountApi();
        createButtonLock();

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("password",password.getText().toString());
                editor.commit();
                asyncCreateUserAPI.execute("https://dev-be.timetomeet.se/service/rest/user/add/", jsonCreateUser());
            }
        });
    }

    // Nusret
    // Låser create knappen om alla fält inte är ifyllda
    public void createButtonLock() {
        if (!firstName.getText().equals("") && !lastName.getText().equals("") &&
                !userName.getText().equals("") && !password.getText().equals("") &&
                !email.getText().equals("") && !phone.getText().equals("") &&
                !company.getText().equals("") && !orgNo.getText().equals("") &&
                !street.getText().equals("") && !zipCode.getText().equals("") &&
                !city.getText().equals("")){

            buttonCreate.setEnabled(true);
        }
        else {
            buttonCreate.setEnabled(false);
        }
    }

    private String jsonCreateUser(){

        String jsonCreateUser ="{" +
            "    \"first_name\": " + "\"" + firstName.getText() + "\"," +
            "    \"last_name\": " + "\"" + lastName.getText() + "\"," +
            "    \"username\": " + "\"" + "z_".concat(userName.getText().toString()) + "\"," +
            "    \"password\": " + "\"" + password.getText() + "\"," +
            "    \"email\": " + "\"" + email.getText() + "\"," +
            "    \"phone_number\": " + "\"" + phone.getText() + "\"," +
            "    \"organization_name\": " + "\"" + company.getText() + "\"," +
            "    \"organization_nr\": " + "\"" + orgNo.getText() + "\"," +
            "    \"street\": " + "\"" + street.getText() + "\"," +
            "    \"city_name\": " + "\"" + city.getText() + "\"," +
            "    \"zipCode\": " + "\"" + zipCode.getText() + "\"" +
            "}";
        System.out.println("The whole json string is: " + jsonCreateUser);

        return jsonCreateUser;
    }

    public String generateToken(int byteLength){

        SecureRandom secureRandom = new SecureRandom();
        byte[] token = new byte[byteLength];
        secureRandom.nextBytes(token);

        return new BigInteger(1,token).toString(16);
    }

    private class CreateAccountApi extends AsyncTask<String, Void, String> {
        private String responseContent;
        private String Error = null;
        private JSONObject jsonObject;

        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(CreateAccount.this,"Creating account..",Toast.LENGTH_SHORT).show();
        }

        protected String doInBackground(String... requestData) {
            BufferedReader reader=null;

            try {
                URL url = new URL(requestData[0]);  // url

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
            try {
                Thread.sleep(2000);
                Toast.makeText(CreateAccount.this,"Account is created!",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CreateAccount.this,Login.class));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
