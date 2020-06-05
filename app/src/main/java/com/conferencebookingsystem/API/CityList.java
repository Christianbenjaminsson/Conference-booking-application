package com.conferencebookingsystem.API;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class CityList {

    String url;
    HashMap<Integer,String> cities = new HashMap<>();

    private static CityList mInstance;
    private RequestQueue requestQueue;
    private static Context context;
    String nextPage;
    public CityList(String url, Context context) {
        this.url = url;
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
        getCityAPI();
    }

    public void getCityAPI() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONArray jsonArray = response.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject results = jsonArray.getJSONObject(i);

                        int id = results.getInt("id");
                        String name = results.getString("name");
                        cities.put(id,name);
                    }
                    nextPage = response.getString("next");
                    if(!nextPage.equals("null")){
                        url = response.getString("next");
                        getCityAPI();
                    }else{
                        System.out.println("The number of cities on API are: " + cities.size());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(request);
    }

    public HashMap<Integer, String> getCities() {
        return cities;
    }
}
