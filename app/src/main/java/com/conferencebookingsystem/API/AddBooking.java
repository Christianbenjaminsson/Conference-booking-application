package com.conferencebookingsystem.API;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class AddBooking {
    AsyncTask<String, Void, String> asyncAddBooking;
    String token, guestAmount;
    SharedPreferences guests;

    public AddBooking(Context context, String token) {

//        SharedPreferences rToken = context.getSharedPreferences("tokenAPI", context.MODE_PRIVATE);
//        this.token = rToken.getString("token", null);
        guests = context.getSharedPreferences("guests",Context.MODE_PRIVATE);
        guestAmount = guests.getString("guestNumber", null);

        System.out.println("AddBooking class: guestAmount " + guestAmount);
        this.token = token;
        System.out.println("AddBooking - The received token: " + token);
    }

    public void startAddBooking(){
        asyncAddBooking = new RestConnectionAddBooking();
        asyncAddBooking.execute("https://dev-be.timetomeet.se/service/rest/booking/add/", jsonSearchParam());
    }

    private String jsonSearchParam(){
        String jsonSearchParam = "{" +
                "    \"paymentAlternative\": 2," +
                "    \"wantHotelRoomInfo\": true," +
                "    \"wantActivityInfo\": true," +
                "    \"specialRequest\": \"\"," +
                "    \"numberOfParticipants\":" + guestAmount + "," +
                "    \"bookingSourceSystem\": 1," +
                "    \"agreementNumber\": \"\"" +
                "}";
        return jsonSearchParam;
    }

    class RestConnectionAddBooking extends AsyncTask<String, Void, String> {

    //private HttpURLConnection Client ;
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
        System.out.println("AddBooking - The response content received from API " + responseContent);

    }
}
}

