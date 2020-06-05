package com.conferencebookingsystem.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.conferencebookingsystem.API.AddBooking;
import com.conferencebookingsystem.API.AddFoodbeverage;
import com.conferencebookingsystem.API.AddTechnology;
import com.conferencebookingsystem.API.ConferenceRoomBook;
import com.conferencebookingsystem.API.FoodbeverageList;
import com.conferencebookingsystem.API.TechnologyList;
import com.conferencebookingsystem.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class Choice extends AppCompatActivity {

    RadioButton[] radioButtons;
    CheckBox[] checkBoxFoodbeverage = null, checkBoxTechnology = null;

    ScrollView scrollViewChoice;
    TextView textView1, textView2, textView3;
    Button confirm;
    LinearLayout linearLayoutV1, linearLayoutV2, linearLayoutV3, linearLayoutH1;
    LinearLayout linearLayoutV4, linearLayoutV5, linearLayoutV6, linearLayoutH2;
    LinearLayout linearLayoutV7, linearLayoutV8, linearLayoutV9, linearLayoutH3;
    RadioGroup radioGroup;
    RadioButton radioButton;
    TableLayout tableLayout;
    TableRow tableRow1, tableRow2, tableRow3;
    ArrayList<String> seatList = new ArrayList<>();
    HashMap<Integer, String> listOfFoodBeverageAll = new HashMap<>();
    HashMap<Integer, String> listOfTechnologyAll = new HashMap<>();
    HashMap<Integer, String > listOfFoodBeveragePlant, listOfTechnologyRoom;
    HashMap<Integer, String> seatingListHashmap;

    int conferenceRoomNumber, foodbeverageNumber, clickedCheckBoxFoodbeverageIndex, clickedCheckBoxTechnologyIndex,
            clickedRadioButtonIndex, chosenSeatId, conferenceRoomAvailabilityId, standardSeating;
    String chosenPlantId, urlFoodBeverageListPlant, urlTechnology, token, timeToServe;
    JSONArray jsonArray;
    AsyncTask<String, Void, String> asyncFoodbeverage;
    AsyncTask<String, Void, String> asyncTechnology;
    AsyncTask<String, Void, String> asyncStandartSeating;
    ArrayList<Integer> chosenTechnologyByUser = new ArrayList<>();
    ArrayList<Integer> chosenFoodBeverageByUser = new ArrayList<>();
    ArrayList<Integer> clickedRadionButtonIndexArray = new ArrayList<>();

    private View.OnClickListener radioButtonListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {

            asyncStandartSeating = new RestConnectionStandartSeating();
            asyncStandartSeating.execute("https://dev-be.timetomeet.se/service/rest/conferenceroomstandardseat/conferenceroom/"+conferenceRoomNumber);

            for(int i=0; i<radioButtons.length;i++){
                if(radioButtons[i].getId() == view.getId()){
                    clickedRadioButtonIndex = i;
                    //System.out.println("clickedRadionButtonIndexArray Length: " + clickedRadionButtonIndexArray.length);

                    if(!clickedRadionButtonIndexArray.isEmpty()){
                        radioButtons[clickedRadionButtonIndexArray.get(0)].setChecked(false);
                        clickedRadionButtonIndexArray.clear();
                    }
                    clickedRadionButtonIndexArray.add(clickedRadioButtonIndex);


                    System.out.println("Hashmap: " + seatingListHashmap);

                    for(Map.Entry<Integer, String> entry : seatingListHashmap.entrySet()) {
                        if (entry.getValue().equals(seatList.get(clickedRadioButtonIndex))) {
                            System.out.println("The selected radiobutton has an ID: " + entry.getKey());
                            chosenSeatId = entry.getKey();
                        }
                    }

                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
        scrollViewChoice = findViewById(R.id.scrollViewChoice);
        tableLayout = findViewById(R.id.tableLayoutChoice);
        listOfFoodBeveragePlant = new HashMap<>();
        listOfTechnologyRoom = new HashMap<>();

        SharedPreferences rToken = getSharedPreferences("tokenAPI", MODE_PRIVATE);
        token = rToken.getString("token", null);

        Intent in = getIntent();
        seatList.clear();
        seatList = (ArrayList<String>) in.getSerializableExtra("seatList");
        seatingListHashmap = (HashMap<Integer, String>) in.getSerializableExtra("seatingHashmap");
        conferenceRoomAvailabilityId = in.getIntExtra("conferenceRoomAvailabilityId", 0);
        System.out.println("The sent over hashmap" + seatingListHashmap);


        conferenceRoomNumber = in.getIntExtra("roomNumber",0);
        chosenPlantId = in.getStringExtra("plantId");
        urlFoodBeverageListPlant = "https://dev-be.timetomeet.se/service/rest/plantfoodbeverage/venue/" + chosenPlantId;
        urlTechnology = "https://dev-be.timetomeet.se/service/rest/conferenceroomtechnology/conferenceroom/" + conferenceRoomNumber;
        timeToServe = in.getStringExtra("timeToServe");
        System.out.println("Hello from Choice class: timeToServe " + timeToServe);

        System.out.println(urlFoodBeverageListPlant);
        System.out.println("Hello from Choice class - conf room number: " + conferenceRoomNumber);
        System.out.println("Hello from Choise class: " + seatList.size() + " " + seatList);
        System.out.println("Hello from Choice class: plant id - " + chosenPlantId);
        radioButtons = new RadioButton[seatList.size()];

        FoodbeverageList foodbeverageList = new FoodbeverageList();
        listOfFoodBeverageAll = foodbeverageList.getFoodbeverageList();

        TechnologyList technologyList = new TechnologyList();
        listOfTechnologyAll = technologyList.getTechnologyList();

        asyncFoodbeverage = new RestConnection();
        asyncFoodbeverage.execute(urlFoodBeverageListPlant, "foodbeverage");

        asyncTechnology = new RestConnection();
        asyncTechnology.execute(urlTechnology, "technology");

        System.out.println("conferenceRoomNumber: " + conferenceRoomNumber);
    }

    private class RestConnection extends AsyncTask<String, Void, String> {
        private String responseContent, choice;
        private String Error = null;
        private URL url;

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... requestData) {
            BufferedReader reader=null;

            try {
                url = new URL(requestData[0]);
                choice = requestData[1];

                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept","application/json");
                connection.setDoOutput(true); // True för POST, PUT. False för GET

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

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
            switch (choice) {
                case "foodbeverage":
                    try {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject results = jsonArray.getJSONObject(i);
                            int foodBeverage = results.getInt("foodBeverage");
                            System.out.println("FoodbeverageID: " + foodBeverage);

                            for(Map.Entry<Integer, String> entry : listOfFoodBeverageAll.entrySet()) {
                                if (entry.getKey().equals(foodBeverage)) {
                                    listOfFoodBeveragePlant.put(entry.getKey(), entry.getValue());
                                    System.out.println("Food list: " + entry.getKey() + "-" + entry.getValue());
                                }
                            }

                        }
                        checkBoxFoodbeverage = new CheckBox[listOfFoodBeveragePlant.size()];
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                case "technology":
                    try {
                        System.out.println("executing technology----------------");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject results = jsonArray.getJSONObject(i);
                            int id = results.getInt("id");
                            int technology = results.getInt("technology");
                            System.out.println("The size of technologyAll " + listOfTechnologyAll.size());

                            for(Map.Entry<Integer, String> entry : listOfTechnologyAll.entrySet()) {
                                if (entry.getKey().equals(technology)) {
                                    listOfTechnologyRoom.put(entry.getKey(), entry.getValue());
                                    System.out.println("Technology list: " + entry.getKey() + "-" + entry.getValue());
                                }
                            }
                        }
                        checkBoxTechnology = new CheckBox[listOfTechnologyRoom.size()];


                    }catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
            }

            if(seatList.size()>0 && listOfFoodBeveragePlant.size()>0 && listOfTechnologyRoom.size()>0){
                addChoices(seatList.size(), listOfFoodBeveragePlant.size(), listOfTechnologyRoom.size());
            }

        }

    }
        // Nusret och Kamil
    public void addChoices(int furnitureNumber,int foodbeverageNumber, int technologyNumber) {
        tableLayout.removeAllViews();

        Typeface monterrat = ResourcesCompat.getFont(getApplicationContext(),R.font.montserrat);
        Typeface monterratBold = ResourcesCompat.getFont(getApplicationContext(),R.font.montserratbold);

        tableRow1 = new TableRow(getBaseContext());
        tableRow1.removeAllViews();
        tableRow1.setLayoutParams(new ScrollView.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        tableRow1.setBackgroundResource(R.drawable.table_divider);

        tableRow2 = new TableRow(getBaseContext());
        tableRow2.removeAllViews();
        tableRow2.setLayoutParams(new ScrollView.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        tableRow2.setBackgroundResource(R.drawable.table_divider);

        tableRow3 = new TableRow(getBaseContext());
        tableRow3.removeAllViews();
        tableRow3.setLayoutParams(new ScrollView.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        tableRow3.setBackgroundResource(R.drawable.table_divider);

        linearLayoutH1 = new LinearLayout(getBaseContext());
        linearLayoutH1.setOrientation(LinearLayout.HORIZONTAL);

        linearLayoutH2 = new LinearLayout(getBaseContext());
        linearLayoutH2.setOrientation(LinearLayout.HORIZONTAL);

        linearLayoutH3 = new LinearLayout(getBaseContext());
        linearLayoutH3.setOrientation(LinearLayout.HORIZONTAL);

        linearLayoutV1 = new LinearLayout(getBaseContext());
        linearLayoutV1.setOrientation(LinearLayout.VERTICAL);

        linearLayoutV2 = new LinearLayout(getBaseContext());
        linearLayoutV2.setOrientation(LinearLayout.VERTICAL);

        linearLayoutV3 = new LinearLayout(getBaseContext());
        linearLayoutV3.setOrientation(LinearLayout.VERTICAL);

        linearLayoutV4 = new LinearLayout(getBaseContext());
        linearLayoutV4.setOrientation(LinearLayout.VERTICAL);

        linearLayoutV5 = new LinearLayout(getBaseContext());
        linearLayoutV5.setOrientation(LinearLayout.VERTICAL);

        linearLayoutV6 = new LinearLayout(getBaseContext());
        linearLayoutV6.setOrientation(LinearLayout.VERTICAL);

        linearLayoutV7 = new LinearLayout(getBaseContext());
        linearLayoutV7.setOrientation(LinearLayout.VERTICAL);

        linearLayoutV8 = new LinearLayout(getBaseContext());
        linearLayoutV8.setOrientation(LinearLayout.VERTICAL);

        linearLayoutV9 = new LinearLayout(getBaseContext());
        linearLayoutV9.setOrientation(LinearLayout.VERTICAL);

        textView1 = new TextView(getBaseContext());
        textView1.setText("Seating");
        textView1.setTypeface(monterrat);
        textView1.setTextColor(Color.BLACK);

        textView2 = new TextView(getBaseContext());
        textView2.setText("Food & Drinks");
        textView2.setTypeface(monterrat);
        textView2.setTextColor(Color.BLACK);

        textView3 = new TextView(getBaseContext());
        textView3.setText("Technology");
        textView3.setTypeface(monterrat);
        textView3.setTextColor(Color.BLACK);

        confirm = new Button(getBaseContext());
        confirm.setWidth(60);
        confirm.setHeight(40);
        confirm.setTextSize(10);
        confirm.setText("Confirm");
        confirm.getBackground().setColorFilter(0xE65BD744, PorterDuff.Mode.MULTIPLY);
        confirm.setTextColor(Color.BLACK);
        confirm.setTypeface(monterratBold);
        confirm.setText("Confirm");

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddBooking addBooking = new AddBooking(getBaseContext(), token);
                addBooking.startAddBooking();

                ConferenceRoomBook conferenceRoomBook = new ConferenceRoomBook(token, conferenceRoomAvailabilityId, standardSeating);
                conferenceRoomBook.startConferenceRoomBook();

                chosenFoodBeverageByUser.clear();
                chosenTechnologyByUser.clear();

                for(int t=0;t<checkBoxFoodbeverage.length;t++) {
                    if (checkBoxFoodbeverage[t].isChecked() == true) {
                        chosenFoodBeverageByUser.add(checkBoxFoodbeverage[t].getId());
                    }
                }

                for(int t1=0;t1<checkBoxTechnology.length;t1++) {
                    if (checkBoxTechnology[t1].isChecked() == true) {
                        chosenTechnologyByUser.add(checkBoxTechnology[t1].getId());
                    }
                }

                AddFoodbeverage addFoodbeverage = new AddFoodbeverage(getBaseContext() ,token, conferenceRoomAvailabilityId, chosenFoodBeverageByUser, timeToServe);
                addFoodbeverage.startAddFoodbeverage();

                AddTechnology addTechnology = new AddTechnology(token, conferenceRoomAvailabilityId, chosenTechnologyByUser);
                addTechnology.startAddTechnology();

                startActivity(new Intent(Choice.this, Confirm.class));

            }
        });


        tableLayout.addView(textView1);
        tableLayout.addView(tableRow1);

        tableLayout.addView(textView2);
        tableLayout.addView(tableRow2);

        tableLayout.addView(textView3);
        tableLayout.addView(tableRow3);

        tableLayout.addView(confirm);

            tableRow1.addView(linearLayoutH1);
                linearLayoutH1.addView(linearLayoutV1);
                linearLayoutH1.addView(linearLayoutV2);
                linearLayoutH1.addView(linearLayoutV3);

                //adding room furniture
                for(int i=0; i<furnitureNumber; i++){
                    radioButtons[i] = new RadioButton(getBaseContext());
                    radioButtons[i].setId(i);
                    radioButtons[i].setText(seatList.get(i));
                    radioButtons[i].setTypeface(monterrat);
                    radioButtons[i].setTextColor(Color.BLACK);
                    if(i<3){
                        linearLayoutV1.addView(radioButtons[i]);
                    } else if(i>=3 && i<6){
                        linearLayoutV2.addView(radioButtons[i]);
                    }else {
                        linearLayoutV3.addView(radioButtons[i]);
                    }
                }

                for(RadioButton r:radioButtons){
                    r.setOnClickListener(radioButtonListener);
                }

            tableRow2.addView(linearLayoutH2);
                //linearLayoutH2.addView(linearLayoutV4);
                //linearLayoutH2.addView(linearLayoutV5);
                linearLayoutH2.addView(linearLayoutV6);

                //adding foodbeverage
                for(int i1=0; i1<foodbeverageNumber; i1++){
                    checkBoxFoodbeverage[i1] = new CheckBox(getBaseContext());
                    checkBoxFoodbeverage[i1].setId((int) listOfFoodBeveragePlant.keySet().toArray()[i1]);
                    checkBoxFoodbeverage[i1].setText((String) listOfFoodBeveragePlant.values().toArray()[i1]);
                    checkBoxFoodbeverage[i1].setTypeface(monterrat);
                    checkBoxFoodbeverage[i1].setTextColor(Color.BLACK);

                    if(i1<3){
                        linearLayoutV6.addView(checkBoxFoodbeverage[i1]);
                    } else if(i1>=3 && i1<6){
                        linearLayoutV6.addView(checkBoxFoodbeverage[i1]);
                    }else{
                        linearLayoutV6.addView(checkBoxFoodbeverage[i1]);
                    }
                }

//            for(CheckBox checkBox:checkBoxFoodbeverage){
//                checkBox.setOnClickListener(checkBoxFoodbeverageListener);
//            }

            //adding technology
            tableRow3.addView(linearLayoutH3);
            //linearLayoutH3.addView(linearLayoutV7);
            //linearLayoutH3.addView(linearLayoutV8);
            linearLayoutH3.addView(linearLayoutV9);

            for(int i1=0; i1<technologyNumber; i1++){
                checkBoxTechnology[i1] = new CheckBox(getBaseContext());
                checkBoxTechnology[i1].setId((int) listOfTechnologyRoom.keySet().toArray()[i1]);
                checkBoxTechnology[i1].setText((String) listOfTechnologyRoom.values().toArray()[i1]);
                checkBoxTechnology[i1].setTypeface(monterrat);
                checkBoxTechnology[i1].setTextColor(Color.BLACK);

                if(i1<3){
                    linearLayoutV9.addView(checkBoxTechnology[i1]);
                } else if(i1>=3 && i1<6){
                    linearLayoutV9.addView(checkBoxTechnology[i1]);
                }else{
                    linearLayoutV9.addView(checkBoxTechnology[i1]);
                }
            }

//            for(CheckBox checkBoxT:checkBoxTechnology){
//                checkBoxT.setOnClickListener(checkBoxTechnologyListener);
//            }
    }

    public void setStandardSeating(int standardSeating) {
        this.standardSeating = standardSeating;
    }

    private class RestConnectionStandartSeating extends AsyncTask<String, Void, String> {
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
                System.out.println(url);
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
            for(int i=0; i<jsonArray.length(); i++){
                try {
                    jsonObject = jsonArray.getJSONObject(i);
                    if((jsonObject.getInt("id")==chosenSeatId) && (jsonObject.getInt("conferenceRoom")==conferenceRoomNumber)) {
                        standardSeating = jsonObject.getInt("standardSeating");
                        setStandardSeating(standardSeating);
                        System.out.println("From inner class: " + standardSeating);
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
