package com.conferencebookingsystem.API;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.json.JSONObject;
import org.w3c.dom.ls.LSOutput;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ConferenceRoomBook {

    AsyncTask<String, Void, String> asyncConferenceRoomBooking;
    String token;
    int conferenceRoomAvailabilityNumber, chosenSeatingNumber;

    public ConferenceRoomBook(String token, int conferenceRoomAvailabilityNumber, int chosenSeatingNumber) {
        this.token = token;
        this.conferenceRoomAvailabilityNumber = conferenceRoomAvailabilityNumber;
        this.chosenSeatingNumber = chosenSeatingNumber;
    }

    public void startConferenceRoomBook(){
        asyncConferenceRoomBooking = new RestConnectionConferenceRoomBooking();
        asyncConferenceRoomBooking.execute("https://dev-be.timetomeet.se/service/rest/conferenceroomavailability/book/"+conferenceRoomAvailabilityNumber+"/",
                jsonParam());
    }

    private String jsonParam(){
        String jsonParam = "{" +
                "    \"chosenSeating\": " + chosenSeatingNumber + "," +
                "    \"reservation_guid\": \"test\"" +
                "}";
        System.out.println("The jsonparam: " + jsonParam);
        return jsonParam;
    }

    class RestConnectionConferenceRoomBooking extends AsyncTask<String, Void, String> {
        private String responseContent;
        private String Error = null;
        private URL url;

        private JSONObject jsonObject;

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... requestData) {
            BufferedReader reader=null;

            try {
                url = new URL(requestData[0]);
                System.out.println("The URL: " + url);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept","application/json");
                connection.setRequestProperty("Authorization","Token " + token);
                connection.setDoOutput(true); // True för POST, PUT. False för GET
                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                wr.write(requestData[1]); // { "username": ....
                wr.flush();

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;

                while((line = reader.readLine()) != null) {
                    sb.append(line + "  \n");
                }
                responseContent = sb.toString();
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
            System.out.println("ConferenceRoomBook - The response content received from API " + responseContent);

        }
    }
}
