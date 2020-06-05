package com.conferencebookingsystem.API;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class AddTechnology {
    AsyncTask<String, Void, String> asyncAddTechnology;
    String token, selectedTechnologyID;
    int conferenceRoomAvailabilityNumber;
    ArrayList<Integer> selectedTechnologyIDs;

    public AddTechnology(String token, int conferenceRoomAvailabilityNumber, ArrayList<Integer> selectedTechnologyIDs) {
        this.token = token;
        this.conferenceRoomAvailabilityNumber = conferenceRoomAvailabilityNumber;
        this.selectedTechnologyIDs = selectedTechnologyIDs;
        System.out.println("AddTechnology class: selected technology size - " + this.selectedTechnologyIDs.size());
        System.out.println("AddTechnology class: selected technology list - " + this.selectedTechnologyIDs);
    }

    public void startAddTechnology(){

        for(int a=0; a<selectedTechnologyIDs.size(); a++){
            selectedTechnologyID = selectedTechnologyIDs.get(a).toString();
            asyncAddTechnology = new RestConnectionAddTechnology();
            asyncAddTechnology.execute("https://dev-be.timetomeet.se/service/rest/bookingselectabletechnology/add/", jsonParam());
        }
    }

    private String jsonParam(){
        String jsonParam = "{" +
                "    \"conferenceRoomAvailability\": " + conferenceRoomAvailabilityNumber + "," +
                "    \"technology\": " + selectedTechnologyID +
                "}";
        System.out.println("The jsonparam: " + jsonParam);

        return jsonParam;
    }

    class RestConnectionAddTechnology extends AsyncTask<String, Void, String> {
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
                connection.setRequestMethod("POST");
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
            System.out.println("AddTechnology class - response content received from API " + responseContent);

        }
    }



}
