package com.conferencebookingsystem.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.conferencebookingsystem.API.CityList;
import com.conferencebookingsystem.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class Search extends AppCompatActivity {

    Button buttonSearch, buttonViewPlant;
    TableLayout tableLayout;
    ScrollView scrollView;
    TableRow tableRow;
    ImageView imageView;
    TextView textViewDescription, textViewPrice;
    EditText seats;
    LinearLayout linearLayoutH, linearLayoutV;
    Spinner spinner;
    String selectedPlantId = null;
    HashMap<Integer, String> listOfCities;
    private String selectedCityOnSpinner;
    private static final String tag = "Search";
    private TextView Date;
    private DatePickerDialog.OnDateSetListener DateListener;
    AsyncTask<String, Void, String> asyncSearchAPI;
    int ii, aa=0, clickedButtonId;
    private SharedPreferences numberOfPeople;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        numberOfPeople = getSharedPreferences("guests",MODE_PRIVATE);
        editor = numberOfPeople.edit();

        spinner = findViewById(R.id.CitySearch);
        tableLayout = findViewById(R.id.tableLayout);
        scrollView = findViewById(R.id.scrollView);
        buttonSearch = findViewById(R.id.SearchButton);
        Date = (TextView) findViewById(R.id.Date);
        seats = findViewById(R.id.People);

        // current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String currentDate = year + "-" + (month+1) + "-" + day;

        CityList city = new CityList("https://dev-be.timetomeet.se/service/rest/city/", getBaseContext());
        listOfCities = city.getCities();

        System.out.println("List of cities: " + listOfCities);
        // default search settings if nothing is picked
        DataHolder.setDate(currentDate);
        DataHolder.setCity("1");
        //DataHolder.setPeople("5");

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPlantId = null;
                DataHolder.setPeople(seats.getText().toString());
                asyncSearchAPI = new RestConnectionSearch();
                try {
                    asyncSearchAPI.execute("https://dev-be.timetomeet.se/service/rest/conferenceroomavailability/search/",jsonSearchParam()).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("The string value is: " + jsonSearchParam());
            }

        });


        // Nusret
        // https://www.youtube.com/watch?v=hwe1abDO2Ag
        Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog DatePicker = new DatePickerDialog(
                        Search.this,
                        android.R.style.Theme_Holo_Light_Dialog, DateListener, year, month, day);
                DatePicker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                DatePicker.show();

            }
        });
        DateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String selectedDate = year + "-" + month + "-" + dayOfMonth;
                Date.setText(selectedDate);
                DataHolder.setDate(selectedDate);
            }
        };

        // Nusret
        // https://android--code.blogspot.com/2015/08/android-spinner-hint.html


        String[] Cities = new String[]{
                "City",
                "Göteborg",
                "Stockholm",
                "Falkenberg",
                "Lidingö",
                "Malmö",
                "Marstrand",
                "Mölndal",
                "Nynäshamn",
                "Solna",
                "Sundbyberg",
                "Uppsala",
                "Vaxholm",
                "Västerås",
                "Växjö",
                "Ystad",
                "Hägersten",
                "Stockholm-Globen",
                "Johanneshov",
                "Bromma",
                "Kista",
                "Skärholmen",
                "Årsta",
                "Danderyd",
                "Hovås",
                "Hisinge Backa",
                "Styrsö",
                "Västra Frölunda",
                "Mölnlycke"
        };

        final List<String> cityList = new ArrayList<>(Arrays.asList(Cities));

        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String> (
                this,R.layout.support_simple_spinner_dropdown_item, cityList) {
        @Override
        public boolean isEnabled(int position){
            if(position == 0)
            {
                // Disable the first item from Spinner
                // First item will be use for hint
                return false;
            }
            else
            {
                return true;
            }
        }
        @Override
        public View getDropDownView(int position, View convertView,
                ViewGroup parent) {
            View view = super.getDropDownView(position, convertView, parent);
            TextView tv = (TextView) view;
            if(position == 0){
                // Set the hint text color gray
                tv.setTextColor(Color.GRAY);
            }
            else {
                tv.setTextColor(Color.BLACK);
            }
            return view;
        }
    };

        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            selectedCityOnSpinner = (String) parent.getItemAtPosition(position);

            // If user change the default selection
            // First item is disable and it is used for hint
            if(position > 0){
                // Notify the selected item text
                Toast.makeText
                        (getApplicationContext(), "Selected : " + selectedCityOnSpinner, Toast.LENGTH_SHORT)
                        .show();
                String currentCity = "" + position;
                System.out.println(currentCity);

                /*getting id of the selected city on the spinner*/
                System.out.println("City hashmap size: " + listOfCities.size());

                for(Map.Entry<Integer, String> entry: listOfCities.entrySet()){
                    if(entry.getValue().equals(selectedCityOnSpinner)){
                        DataHolder.setCity(String.valueOf(entry.getKey()));
                        System.out.println("The selected city has ID: " + entry.getKey());
                    }
                }

                // DataHolder.setCity(currentCity);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    });
    }

    private String jsonSearchParam(){
        //date 2020-05-28 dvs. yyyy-mm-dd

        String jsonSearchParam = "{" +
                "    \"cityId\": "+DataHolder.getCity()+"," +
                "    \"seats\": "+DataHolder.getPeople()+"," +
                "    \"plantId\": "+ selectedPlantId +"," +
                "    \"dateTimeFrom\": \""+DataHolder.getDate()+"T09:00:00+02:00\"," +
                "    \"dateTimeTo\": \""+DataHolder.getDate()+"T17:00:00+02:00\"," +
                "    \"page\": 1" +
                "}";
        return jsonSearchParam;
    }

        private class RestConnectionSearch extends AsyncTask<String, Void, String> {
        //private HttpURLConnection Client ;
        private String responseContent;
        private String Error = null;
        private URL url;
        int a1=0, a2=0, a3=0;
        String plantName, plantFacts;
        int priceFrom;

        private JSONObject visitingAddress;
        private JSONArray seats, plantImages, plantIDs, plantsOverview;
        private JSONObject jsonObject, plant;

        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(Search.this,"Searching for rooms..",Toast.LENGTH_SHORT).show();
            System.out.println(DataHolder.getCity());
            System.out.println(DataHolder.getPeople());
            System.out.println(DataHolder.getDate());
        }

        protected String doInBackground(String... requestData) {
            BufferedReader reader=null;

            try {
                url = new URL(requestData[0]);  //https://dev-be.timetomeet.se/service/rest/conferenceroomavailability/search/

                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept","application/json");
                //connection.setRequestProperty("Authorization","Token de15b469e3fc7f654700fdbb1bca7778eb113f5a");
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
                int code = connection.getResponseCode();

                responseContent = sb.toString();
                jsonObject = new JSONObject(responseContent);
                //System.out.println("Response content: " + responseContent + "\nResponse code: " + code);

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
                // Nusret och Kamil
        protected void onPostExecute(final String result) {

            tableLayout.removeAllViews();

            // adding custom font
            Typeface monterrat = ResourcesCompat.getFont(getApplicationContext(),R.font.montserrat);
            Typeface monterratBold = ResourcesCompat.getFont(getApplicationContext(),R.font.montserratbold);
                    //Typeface.createFromAsset(getAssets(), "font/montserrat.ttf");
                    //ResourcesCompat.getFont(this, R.font.montserrat);
            /*Adding dynamic view to the app*/

            ArrayList<JSONObject> listPlant= new ArrayList<>();
            ArrayList<JSONObject> addressPlant= new ArrayList<>();

            try {
                plantIDs = jsonObject.getJSONArray("plants");
                plantsOverview = jsonObject.getJSONArray("plantsOverview");

                for(int i=0; i<plantsOverview.length(); i++){
                    plant = plantsOverview.getJSONObject(i);
                    listPlant.add(plant);
                    visitingAddress = (JSONObject) plant.get("visitingAddress");
                    addressPlant.add(visitingAddress);
                    plantFacts = visitingAddress.getString("street");
            }

                for(int i = 0; i< plantsOverview.length(); i++){
                    ii = i;
                    aa++;
                    tableRow = new TableRow(getBaseContext());
                    tableRow.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, WRAP_CONTENT));
                    tableRow.setBackgroundResource(R.drawable.table_divider);


                    linearLayoutH = new LinearLayout(getBaseContext());
                    linearLayoutH.setOrientation(LinearLayout.HORIZONTAL);

                    linearLayoutV = new LinearLayout(getBaseContext());
                    linearLayoutV.setOrientation(LinearLayout.VERTICAL);


                    imageView = new ImageView(getBaseContext());
                    imageView.setMaxWidth(200);
                    imageView.setMaxHeight(200);

                    textViewPrice = new TextView(getBaseContext());
                    textViewPrice.setWidth(150);
                    textViewPrice.setHeight(180);
                    textViewPrice.setTextColor(Color.BLACK);
                    textViewPrice.setTypeface(monterrat);
                    LinearLayout.LayoutParams priceTxt = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
                    priceTxt.setMargins(20,10,10,10);
                    textViewPrice.setLayoutParams(priceTxt);

                    textViewDescription = new TextView(getBaseContext());
                    textViewDescription.setWidth(780);
                    textViewDescription.setTextColor(Color.BLACK);
                    textViewDescription.setTypeface(monterrat);
                    textViewDescription.setBackgroundResource(R.drawable.table_divider);
                    textViewDescription.setLayoutParams(new LinearLayout.LayoutParams(780, WRAP_CONTENT));

                    buttonViewPlant = new Button(getBaseContext());
                    buttonViewPlant.getBackground().setColorFilter(0xE65BD744, PorterDuff.Mode.MULTIPLY);
                    buttonViewPlant.setWidth(80);
                    buttonViewPlant.setHeight(60);
                    buttonViewPlant.setTextSize(10);
                    buttonViewPlant.setTextColor(Color.BLACK);
                    buttonViewPlant.setId(i);
                    buttonViewPlant.setTypeface(monterratBold);
                    LinearLayout.LayoutParams button = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
                    button.setMargins(20,10,10,10);
                    buttonViewPlant.setLayoutParams(priceTxt);

                    buttonViewPlant.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                selectedPlantId = plantIDs.get(v.getId()).toString();
                                System.out.println("The selected plant id: " + selectedPlantId);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            editor.putString("guestNumber",DataHolder.getPeople());
                            editor.commit();

                            startActivity(new Intent(Search.this, Booking.class)
                                    .putExtra("searchParam",jsonSearchParam())
                                    .putExtra("plantId", selectedPlantId)
                            );

                        }
                    });

                    scrollView = new ScrollView(getBaseContext());
                    scrollView.setLayoutParams(new ScrollView.LayoutParams(780, WRAP_CONTENT));


                    tableLayout.addView(tableRow);
                    TableLayout.LayoutParams tableRowParams=
                            new TableLayout.LayoutParams
                                    (TableLayout.LayoutParams.FILL_PARENT, WRAP_CONTENT);

                    int leftMargin=2;
                    int topMargin=10;
                    int rightMargin=2;
                    int bottomMargin=15;

                    tableRowParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);

                    tableRow.setLayoutParams(tableRowParams);

                    tableRow.addView(linearLayoutH);
                        linearLayoutH.addView(imageView);
                        a1++;
                        if(a1== 1){
                            linearLayoutH.removeView(scrollView);
                        }
                        linearLayoutH.addView(scrollView);
                            scrollView.addView(textViewDescription);
                            textViewDescription.setText(listPlant.get(i).getString("plantFacts")
                                            + "\n" + addressPlant.get(i).getString("street") +
                                    ", " + addressPlant.get(i).getString("zip") + " "
                                         +  addressPlant.get(i).getString("city")
                                    );
                        linearLayoutH.addView(linearLayoutV);
                            linearLayoutV.addView(textViewPrice);
                            textViewPrice.setText("Price from: \n" + listPlant.get(i).getString("priceFrom"));

                            a2++;
                            if(a2==1){
                                linearLayoutV.removeView(buttonViewPlant);
                            }
                            linearLayoutV.addView(buttonViewPlant);
                            buttonViewPlant.setText("View Rooms");
                    a1=0; a2=0;

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class DataHolder {
        private static String city;
        private static String date;
        private static String people;

        public static String getCity() {return city;}
        public static void setCity(String city) {DataHolder.city = city;}

        public static String getDate() {return date;}
        public static void setDate(String date) {DataHolder.date = date;}

        public static String getPeople() {return people;}
        public static void setPeople(String people) {DataHolder.people = people;}

    }


}
