package com.conferencebookingsystem.API;

import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

public class TechnologyList {

    HashMap<Integer,String> technologyList = new HashMap<>();
    AsyncTask<String, Void, String> asyncTechnologyAPI;
    JSONArray jsonArray;

    public TechnologyList() {
        asyncTechnologyAPI = new RestConnectionTechnologyAPI();
        asyncTechnologyAPI.execute("https://dev-be.timetomeet.se/service/rest/technology/");
    }

    private class RestConnectionTechnologyAPI extends AsyncTask<String, Void, String> {
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
                connection.setDoOutput(false); // True för POST, PUT. False för GET

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;

                while((line = reader.readLine()) != null) {
                    sb.append(line + "  \n");
                }

                responseContent = sb.toString();
                jsonArray = new JSONArray(responseContent);
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
                for(int i=0; i<jsonArray.length();i++){
                    JSONObject results = jsonArray.getJSONObject(i);
                    int id = results.getInt("id");
                    String name = results.getString("name");
                    technologyList.put(id,name);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public HashMap<Integer, String> getTechnologyList() {
        return technologyList;
    }
}
