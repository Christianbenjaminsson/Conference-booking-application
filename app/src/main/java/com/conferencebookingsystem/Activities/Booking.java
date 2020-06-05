package com.conferencebookingsystem.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.conferencebookingsystem.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class Booking extends AppCompatActivity{

    Button buttons[] = null;
    RadioButton radioButton[] = null;
    ScrollView scrollView, scrollView1;
    TableLayout tableLayout;
    TableRow tableRow, tableRow1;
    LinearLayout linearLayoutH, linearLayoutH1, linearLayoutV, linearLayoutV1, linearLayoutV2;
    ImageView imageView;
    TextView textViewTime1, textViewTime2, textViewTime3, textViewPrice1, textViewPrice2, textViewPrice3, textViewDescription;
    Button buttonBook;
    RadioGroup radioGroup;
    RadioButton radioButton1, radioButton2, radioButton3;

    String jsonSearchParam, timeToServe;
    AsyncTask<String, Void, String> asyncSearchAPI;
    int priceAM, pricePM, conferenceRoomId, conferenceRoomAvailabilityId, priceFull, count=0, buttonIndex,
        clickedRadiobuttonId, countR = 0, countRA = 0, quotient, remainder;
    String conferenceRoomName, conferenceRoomDescription, seat_name, hoursAvailableFrom, hoursAvailableTo, chosenPlantId,
            preNoonAvailabilityHourStart, preNoonAvailabilityHourEnd, afterNoonAvailabilityHourStart, afterNoonAvailabilityHourEnd;

    private JSONArray seats, images, roomIDs, rooms, conferenceRoomAvailability;
    private JSONObject jsonObject, room, image, seat, conferenceRoomAvailability_room;

    int aaa=0;
    int[] blockSelected, conferenceRoomIds;

    ArrayList<JSONArray> seatListArray = new ArrayList<>();
    ArrayList<JSONObject> seatList = new ArrayList<>();
    ArrayList<JSONObject> imageList = new ArrayList<>();
    ArrayList<JSONObject> conferenceRoomAvailabilityList = new ArrayList<>();
    ArrayList<String> seatListString = new ArrayList<>();
    HashMap<Integer, String> seatingListHashmap = new HashMap<>();

    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            for(int i=0; i<buttons.length;i++){
                if(buttons[i].getId() == view.getId()){
                    buttonIndex = i;
                    System.out.println("The clicked button has an ID: "+ buttonIndex);
                    System.out.println("The blockselected has an ID: "+ blockSelected[buttonIndex]);
                }
            }
            try {
                seatListString.clear();
                for(int a=0; a<conferenceRoomAvailabilityList.size(); a++) {
                    if ((conferenceRoomAvailabilityList.get(a).getInt("block") == blockSelected[buttonIndex]) &&
                        (conferenceRoomAvailabilityList.get(a).getInt("conferenceRoom") == conferenceRoomIds[buttonIndex])) {
                        System.out.println("The room number is: " + conferenceRoomIds[buttonIndex]);

                        for(int a2=0; a2<seatListArray.get(buttonIndex).length();a2++){
                            seatListString.add(seatListArray.get(buttonIndex).getJSONObject(a2).getString("seat_name"));
                            //System.out.println("The seats are " + seatListArray.get(buttonIndex).getJSONObject(a2).getString("seat_name"));
                        }

                            conferenceRoomAvailabilityId = conferenceRoomAvailabilityList.get(a).getInt("id");
                            timeToServe = conferenceRoomAvailabilityList.get(a).getString("start") + "T" + conferenceRoomAvailabilityList.get(a).getString("hoursAvailableFrom");
                        System.out.println("TimetoServe is equal: " + timeToServe);
                            System.out.println("The conference room availability number: " + conferenceRoomAvailabilityId);
                            startActivity(new Intent(Booking.this, Choice.class)
                                    .putExtra("seatingHashmap", seatingListHashmap)
                                    .putExtra("seatList", seatListString)
                                    .putExtra("plantId", chosenPlantId)
                                    .putExtra("conferenceRoomAvailabilityId", conferenceRoomAvailabilityId)
                                    .putExtra("roomNumber",conferenceRoomAvailabilityList.get(a).getInt("conferenceRoom"))
                                    .putExtra("timeToServe",timeToServe)

                            );
                    }else if(blockSelected[buttonIndex] == 33){
                        if((conferenceRoomAvailabilityList.get(a).getInt("conferenceRoom") == conferenceRoomIds[buttonIndex])){
                            conferenceRoomAvailabilityId = conferenceRoomAvailabilityList.get(a).getInt("cra_id_for_full_day");
                            System.out.println("The conference room availability number: " + conferenceRoomAvailabilityId);

                            timeToServe = conferenceRoomAvailabilityList.get(a).getString("start") + "T" + conferenceRoomAvailabilityList.get(a).getString("hoursAvailableFrom");
                            for(int a2=0; a2<seatListArray.get(buttonIndex).length();a2++){
                                seatListString.add(seatListArray.get(buttonIndex).getJSONObject(a2).getString("seat_name"));
                                //System.out.println("The seats are " + seatListArray.get(buttonIndex).getJSONObject(a2).getString("seat_name"));
                            }

                            startActivity(new Intent(Booking.this, Choice.class)
                                    .putExtra("seatingHashmap", seatingListHashmap)
                                    .putExtra("seatList", seatListString)
                                    .putExtra("plantId", chosenPlantId)
                                    .putExtra("conferenceRoomAvailabilityId", conferenceRoomAvailabilityId)
                                    .putExtra("roomNumber",conferenceRoomAvailabilityList.get(a).getInt("conferenceRoom"))
                                    .putExtra("timeToServe",timeToServe)
                            );

                            break;
                        }
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        scrollView = findViewById(R.id.scrollviewBooking);
        tableLayout = findViewById(R.id.tableLayoutBooking);

        Intent in = getIntent();
        jsonSearchParam = in.getStringExtra("searchParam");
        chosenPlantId = in.getStringExtra("plantId");
        count = 0;
        asyncSearchAPI = new RestConnectionSearch();
        try {
            asyncSearchAPI.execute("https://dev-be.timetomeet.se/service/rest/conferenceroomavailability/search/",jsonSearchParam).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private class RestConnectionSearch extends AsyncTask<String, Void, String> {
        private String responseContent;
        private String Error = null;
        private URL url;

        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(Booking.this,"List of available rooms..",Toast.LENGTH_SHORT).show();

        }

        protected String doInBackground(String... requestData) {
            BufferedReader reader=null;

            try {
                url = new URL(requestData[0]);  //https://dev-be.timetomeet.se/service/rest/conferenceroomavailability/search/

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
                String line = null;

                while((line = reader.readLine()) != null) {
                    sb.append(line + "  \n");
                }

                responseContent = sb.toString();
                jsonObject = new JSONObject(responseContent);

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
                rooms = jsonObject.getJSONArray("rooms");
                conferenceRoomAvailability = jsonObject.getJSONArray("conferenceRoomAvailability");
                System.out.println("conferenceRoomAvailability length is: " + conferenceRoomAvailability.length());
                buttons = new Button[rooms.length()];
                blockSelected = new int[rooms.length()];
                conferenceRoomIds = new int[rooms.length()];
                radioButton = new RadioButton[rooms.length() * 3];

                for(int i2=0; i2<conferenceRoomAvailability.length(); i2++){
                    conferenceRoomAvailability_room = conferenceRoomAvailability.getJSONObject(i2);
                    conferenceRoomAvailabilityList.add(conferenceRoomAvailability_room);
                }

                for(int i = 0; i< rooms.length(); i++) {
                    count = i;
                    room = rooms.getJSONObject(i);
                    System.out.println("The number of rooms are: " + rooms.length());

                    conferenceRoomId = room.getInt("conferenceRoomId");
                    conferenceRoomIds[i] = conferenceRoomId;
                    conferenceRoomName = room.getString("conferenceRoomName");
                    conferenceRoomDescription = room.getString("description");
                    
                    priceAM = room.getInt("priceAM");
                    pricePM = room.getInt("pricePM");
                    priceFull = room.getInt("priceFull");

                    preNoonAvailabilityHourStart = room.getString("preNoonAvailabilityHourStart");
                    preNoonAvailabilityHourEnd = room.getString("preNoonAvailabilityHourEnd");
                    afterNoonAvailabilityHourStart = room.getString("afterNoonAvailabilityHourStart");
                    afterNoonAvailabilityHourEnd = room.getString("afterNoonAvailabilityHourEnd");

                    images = room.getJSONArray("images");
                    seats = room.getJSONArray("seats");

                    seatListArray.add(seats);

                    for(int i1=0;i1<images.length();i1++) {
                        imageList.add(images.getJSONObject(i1));
                    }

                    for(int i2=0; i2<seats.length(); i2++){
                        JSONObject s = seats.getJSONObject(i2);
                        seatingListHashmap.put(s.getInt("id"), s.getString("seat_name"));
                        seatList.add(seats.getJSONObject(i2));
                    }
                    addElements();
                }

                for(Button btn:buttons){
                    btn.setOnClickListener(btnListener);
                }
            } catch (JSONException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
            // Nusret och Kamil
    public void addElements() throws JSONException, InterruptedException {
        //tableLayout.removeAllViews();
        aaa++;

        tableRow = new TableRow(getBaseContext());
        tableRow.setBackgroundResource(R.drawable.table_divider);

        tableRow1 = new TableRow(getBaseContext());
        tableRow1.setLayoutParams(new ScrollView.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        tableRow1.setBackgroundResource(R.drawable.table_divider);

        imageView = new ImageView(getBaseContext());

        // adding custom font
        Typeface monterrat = ResourcesCompat.getFont(getApplicationContext(),R.font.montserrat);
        Typeface monterratBold = ResourcesCompat.getFont(getApplicationContext(),R.font.montserratbold);

        linearLayoutH = new LinearLayout(getBaseContext());
        linearLayoutH.setOrientation(LinearLayout.HORIZONTAL);

        linearLayoutH1 = new LinearLayout(getBaseContext());
        linearLayoutH1.setOrientation(LinearLayout.HORIZONTAL);

        linearLayoutV = new LinearLayout(getBaseContext());
        linearLayoutV.setOrientation(LinearLayout.VERTICAL);
        linearLayoutV.setLayoutParams(new ScrollView.LayoutParams(550, TableLayout.LayoutParams.WRAP_CONTENT));

        linearLayoutV1 = new LinearLayout(getBaseContext());
        linearLayoutV1.setOrientation(LinearLayout.VERTICAL);

        linearLayoutV2 = new LinearLayout(getBaseContext());
        linearLayoutV2.setOrientation(LinearLayout.VERTICAL);
        linearLayoutV2.setLayoutParams(new ScrollView.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        buttons[count] = new Button(getBaseContext());
        buttons[count].setId(count);
        buttons[count].setWidth(60);
        buttons[count].setHeight(40);
        buttons[count].setTextSize(10);
        buttons[count].getBackground().setColorFilter(0xE65BD744, PorterDuff.Mode.MULTIPLY);
        buttons[count].setTextColor(Color.BLACK);
        buttons[count].setTypeface(monterratBold);
        buttons[count].setText("Book");

        scrollView1 = new ScrollView(getBaseContext());
        textViewDescription = new TextView(getBaseContext());

        textViewTime1 = new TextView(getBaseContext());
        textViewTime1.setTypeface(monterrat);
        textViewTime1.setTextColor(Color.BLACK);
        textViewTime2 = new TextView(getBaseContext());
        textViewTime2.setTypeface(monterrat);
        textViewTime2.setTextColor(Color.BLACK);
        textViewTime3 = new TextView(getBaseContext());
        textViewTime3.setTypeface(monterrat);
        textViewTime3.setText("HELDAG");
        textViewTime3.setTextColor(Color.BLACK);

        textViewDescription.setText(conferenceRoomDescription);
        textViewDescription.setTypeface(monterrat);
        textViewDescription.setTextColor(Color.BLACK);

        radioGroup = new RadioGroup(getBaseContext());
        radioGroup.setLayoutParams(new ScrollView.LayoutParams(250, TableLayout.LayoutParams.WRAP_CONTENT));

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                clickedRadiobuttonId = checkedId;
                quotient = checkedId/3;
                remainder = checkedId%3;
                switch (remainder){
                    case 0:
                        blockSelected[quotient] = 31;
                        break;
                    case 1:
                        blockSelected[quotient] = 32;
                        break;
                    case 2:
                        blockSelected[quotient] = 33;
                        break;
                }
            }
        });

        radioButton1 = new RadioButton(getBaseContext());
        radioButton1.setId(countR++);
        radioButton1.setText(priceAM + " kr");
        radioButton1.setTypeface(monterrat);
        radioButton1.setTextColor(Color.BLACK);
        radioButton1.setPadding(0,0,0,38);

        radioButton2 = new RadioButton(getBaseContext());
        radioButton2.setId(countR++);
        radioButton2.setText(pricePM + " kr");
        radioButton2.setTypeface(monterrat);
        radioButton2.setTextColor(Color.BLACK);
        radioButton2.setPadding(0,0,0,38);

        radioButton3 = new RadioButton(getBaseContext());
        radioButton3.setId(countR++);
        radioButton3.setText(priceFull + " kr");
        radioButton3.setTypeface(monterrat);
        radioButton3.setTextColor(Color.BLACK);


        textViewPrice1 = new TextView(getBaseContext());
        textViewPrice1.setTypeface(monterrat);
        textViewPrice1.setTextColor(Color.BLACK);
        textViewPrice2 = new TextView(getBaseContext());
        textViewPrice2.setTypeface(monterrat);
        textViewPrice2.setTextColor(Color.BLACK);
        textViewPrice3 = new TextView(getBaseContext());
        textViewPrice3.setTypeface(monterrat);
        textViewPrice3.setTextColor(Color.BLACK);

        tableLayout.addView(linearLayoutV2);
        linearLayoutV2.addView(tableRow1);
        TableLayout.LayoutParams tableRowParams=
            new TableLayout.LayoutParams
                    (TableLayout.LayoutParams.FILL_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);

        int leftMargin=2;
        int topMargin=30;
        int rightMargin=2;
        int bottomMargin=2;

        tableRowParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);

        tableRow1.setLayoutParams(tableRowParams);
            tableRow1.addView(scrollView1);
            scrollView1.addView(textViewDescription);

            tableLayout.addView(tableRow);
                tableRow.addView(linearLayoutH);
                    linearLayoutH.addView(imageView);

                    linearLayoutH.addView(linearLayoutH1);
                        linearLayoutH1.addView(linearLayoutV);
                            linearLayoutV.addView(textViewTime1);

                                linearLayoutV.removeView(textViewTime2);
                            linearLayoutV.addView(textViewTime2);

                                linearLayoutV.removeView(textViewTime3);
                            linearLayoutV.addView(textViewTime3);

                        linearLayoutH1.addView(linearLayoutV1);
                            linearLayoutV1.addView(radioGroup);

                        linearLayoutH.removeView(buttons[count]);
                    linearLayoutH.addView(buttons[count]);


        textViewTime1.setText("FÖRMIDDAG \n" + preNoonAvailabilityHourStart +" - " + preNoonAvailabilityHourEnd);
        textViewTime2.setText("EFTERMIDDAG \n" + afterNoonAvailabilityHourStart +" - " + afterNoonAvailabilityHourEnd);

        radioGroup.addView(radioButton1);
        radioGroup.addView(radioButton2);
        radioGroup.addView(radioButton3);
    }

}

