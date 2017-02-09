package gr.gandg.george.gairticketsautocomplete;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


// #######################
// With AutoComplete Text View
//##################################

public class MainActivity extends AppCompatActivity {

    String amadeusKey;
    EditText searchEditText;
    ListView searchResultsListView;
    ArrayAdapter<String> mAirportAdapter;
    List<String> airports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        airports = new ArrayList<String>();

        amadeusKey = BuildConfig.AMADEUS_API_KEY;
        searchEditText = (EditText)findViewById(R.id.textView_search);
        searchResultsListView = (ListView)findViewById(R.id.listView_searchResults);

        mAirportAdapter  = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, airports);
        mAirportAdapter.setNotifyOnChange(true);
        searchResultsListView.setAdapter(mAirportAdapter);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchString = searchEditText.getText().toString();
                if (searchString != null)
                    (new AirportParser()).execute(searchString);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    private class AirportParser extends AsyncTask<String, Void, ArrayList<String>> {

        private static final String LOG_TAG = "AirportParser";
        private  String airportSearchString = "thess";
        private String API_KEY = null;



        protected ArrayList<String> doInBackground(String... params) {
            if (params[0] == null) {
                return null;
            }

            airportSearchString = params[0];

            API_KEY = amadeusKey;

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String airportJsonStr = null;

            String API_BASE_URL = "https://api.sandbox.amadeus.com/v1.2/airports/autocomplete";

            try {

                String theURL = API_BASE_URL + "?apikey=" + API_KEY +
                        "&term=" + URLEncoder.encode(airportSearchString, "UTF-8");
                Log.i(LOG_TAG, theURL);
                URL url = new URL(theURL);


                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                airportJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error " + e);
            } catch (Exception  e) {
                Log.e(LOG_TAG, e.getMessage() + e);
                e.printStackTrace();

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream" + e);
                    }
                }
            }

            return parseJson(airportJsonStr);
        }

        private ArrayList<String> parseJson(String json) {
            ArrayList<String>results = new ArrayList<String>();
            try {
                JSONArray airportArray = new JSONArray(json);
                for (int i=0; i< airportArray.length();i++) {
                    JSONObject aiportObject = airportArray.getJSONObject(i);
                    String theAirport = aiportObject.getString("label");
                    results.add(theAirport);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


            return  results;
        }


        @Override
        protected void onPostExecute(ArrayList<String> result) {
//            TextView mtv = (TextView)findViewById(R.id.main_textview);
//            mtv.setText(s);
            //mAirportAdapter.clear();
            airports.clear();
            airports = result;
            mAirportAdapter  = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_dropdown_item_1line, airports);
            mAirportAdapter.setNotifyOnChange(true);
            searchResultsListView.setAdapter(mAirportAdapter);

        }
    }


}

