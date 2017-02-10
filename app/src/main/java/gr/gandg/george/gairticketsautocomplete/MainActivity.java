package gr.gandg.george.gairticketsautocomplete;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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

    public static final int ACTIVITY_SELECT_AIRPORT=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void selectAirport(View v) {
        Intent selectDateIntent = new Intent();
        selectDateIntent.setClass(this, SelectAirportActivity.class);
        selectDateIntent.putExtra("airportTextViewName",v.getId());
        startActivityForResult(selectDateIntent,ACTIVITY_SELECT_AIRPORT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTIVITY_SELECT_AIRPORT) {
            if (resultCode == Activity.RESULT_OK) {
                String theAirport = data.getStringExtra("selected_airport");
                int theID = data.getIntExtra("airportTextViewName",0);

                ((TextView)findViewById(theID)).setText(theAirport);
            }
        }
    }

}

