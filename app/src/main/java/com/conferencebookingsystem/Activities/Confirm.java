package com.conferencebookingsystem.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

import com.conferencebookingsystem.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class Confirm extends AppCompatActivity {

    Button changeActivityToSearch;
    TextView textViewDateAndTime, textViewBookingInformation, textViewTPrice, textViewConfirmationStatus;
    AsyncTask<String, Void, String> asyncResult;
    String token, bookingNumber, emailStateText, arrivalDate, arrivalTime, departTime, blockDescription, numberOfParticipants;
    String name, lastname, organisation, address, roomName, seating, totalPrice, status;
    JSONObject jsonObject;
    JSONArray jsonArray;
    List<String> foodBeverage = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        textViewDateAndTime = findViewById(R.id.textViewDateAndTime);
        textViewBookingInformation = findViewById(R.id.textViewBookingInformation1);
        textViewTPrice = findViewById(R.id.textViewTPrice);
        textViewConfirmationStatus = findViewById(R.id.textViewConfirmationStatus);

        SharedPreferences rToken = getSharedPreferences("tokenAPI", MODE_PRIVATE);
        token = rToken.getString("token", null);

        asyncResult = new RestConnectionResult();
        asyncResult.execute("https://dev-be.timetomeet.se/service/rest/booking/completed/");

        //Christian har skrivit den här metoden
        changeActivityToSearch = findViewById(R.id.btnSearch);
        changeActivityToSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Confirm.this, Search.class));

            }
        });

    }

    private class RestConnectionResult extends AsyncTask<String, Void, String> {
        private String responseContent;
        private String Error = null;
        private URL url;

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... requestData) {
            BufferedReader reader=null;

            try {
                url = new URL(requestData[0]);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept","application/json");
                connection.setRequestProperty("Authorization","Token " + token);
                connection.setDoOutput(false); // True för POST, PUT. False för GET

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;

                while((line = reader.readLine()) != null) {
                    sb.append(line + "  \n");
                }
                responseContent = sb.toString();
                jsonObject = new JSONObject(responseContent);

                System.out.println("Confirm class: " + jsonObject);
            }
            catch(Exception ex) {
                Error = ex.getMessage();
                System.out.println("The error message is: " + Error);
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

        protected void onPostExecute(final String result) {

            try {
                JSONObject booking_details = jsonObject.getJSONObject("booking_details");
                    bookingNumber = booking_details.getString("bookingNumber");
                    emailStateText = booking_details.getString("emailStateText");
                    arrivalDate = booking_details.getString("arrivalDate");
                    arrivalTime = booking_details.getString("arrivalTime");
                    departTime = booking_details.getString("departTime");
                    blockDescription = booking_details.getString("blockDescription");
                numberOfParticipants = jsonObject.getString("number_of_participants");


                textViewDateAndTime.setText(arrivalDate + "\n" + blockDescription + ": " + arrivalTime + "-" + departTime
                        + "\n" + "People: " + numberOfParticipants);

                name = jsonObject.getString("personFirstName");
                lastname = jsonObject.getString("personLastName");
                totalPrice = jsonObject.getString("sum_total_excl_vat");

                JSONObject bookedByPerson = jsonObject.getJSONObject("booked_by_person");
                organisation = bookedByPerson.getString("books_for_organization");

                textViewBookingInformation.setText("Bookingnumber: " + bookingNumber +"\n" + "Name: " + name + "\n"
                        + "Lastname: " + lastname + "\n" + "Organisation: " + organisation + "\n" +"Price: " + totalPrice);


                JSONObject bookingPlant = jsonObject.getJSONObject("booking_plant");
                address = bookingPlant.getString("visiting_address");

                JSONArray bookedConferenceRoom = jsonObject.getJSONArray("booked_conference_rooms_with_price");
                for(int i=0; i<bookedConferenceRoom.length(); i++){
                    JSONObject j1 = (JSONObject) bookedConferenceRoom.get(i);
                    roomName = j1.getString("conference_room_title");
                    seating = j1.getString("chosen_seating");
                }

                // Nusret
                JSONArray bookedFoodBeverege = jsonObject.getJSONArray("bookingFoodBeverage");
                for(int i=0; i<bookedFoodBeverege.length(); i++){
                    JSONObject jfood = (JSONObject) bookedFoodBeverege.get(i);
                    foodBeverage.add(jfood.getString("foodBeverageName"));
                }

                textViewTPrice.setText("Address: " + address + "\n" + "Room name: " + roomName + "\n" + "Seating: " + seating
                + "\n" + "Food and Beverage: " + foodBeverage.toString());
                textViewConfirmationStatus.setText(emailStateText);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            for(int i=0; i<jsonObject.length(); i++){

            }


        }
    }


}