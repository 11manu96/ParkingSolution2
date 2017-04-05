package com.android.parking.Activity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.android.parking.Adapters.ParkingListCustomAdapter;
import com.android.parking.R;
import com.android.parking.Utils.GPSTracker;
import com.android.parking.Utils.PlaceJSONParser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by manumaheshwari on 7/18/16.
 */
public class ParkingListActivity extends AppCompatActivity {

    ListView parkingSpotList;
    ArrayList<String> parkingNameList;
    ArrayList<String> parkingImageList;
    ArrayList<String> parkingVicinityList;
    ArrayList<String> parkingDistanceList;

    GPSTracker tracker;
    double mLatitude;
    double mLongitude;
    Context context;
    List<HashMap<String,String>> ParkingList;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        setContentView(R.layout.parking_list_activity_layout);

        Bundle bundle = getIntent().getExtras();
        bundle.get("url");

        tracker = new GPSTracker(this);
        mLatitude = tracker.getLatitude();
        mLongitude = tracker.getLongitude();

        context = this;





        parkingSpotList = (ListView) findViewById(R.id.parking_list);



        parkingDistanceList = new ArrayList<String>();
        parkingImageList = new ArrayList<String>();
        parkingVicinityList = new ArrayList<String>();
        parkingNameList = new ArrayList<String>();



        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        sb.append("location="+mLatitude+","+mLongitude);
        //sb.append("location="+"28.586414"+","+"77.324220");

        sb.append("&radius=5000");
        sb.append("&types="+"parking");
        sb.append("&sensor=true");
        sb.append("&key=AIzaSyAGbIe0wZcReiOZFRRIXKs15OC6Z299oRk");


// Creating a new non-ui thread task to download Google place json data
        PlacesTask placesTask = new PlacesTask();

// Invokes the "doInBackground()" method of the class PlaceTask
        placesTask.execute(bundle.get("url").toString());


        /**call the map call back to know map is loaded or not*/




    }










    private class PlacesTask extends AsyncTask<String, Integer, String> {

        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try{
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result){
            ParserTask parserTask = new ParserTask();

// Start parsing the Google places in JSON format
// Invokes the "doInBackground()" method of the class ParseTask
            parserTask.execute(result);
        }



    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{

        JSONObject jObject;

        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String,String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;
            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try{
                jObject = new JSONObject(jsonData[0]);

                /** Getting the parsed data as a List construct */
                places = placeJsonParser.parse(jObject);

            }catch(Exception e){
                Log.d("Exception",e.toString());
            }
            return places;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(final List<HashMap<String,String>> list){

            ParkingList = list;
// Clears all the existing markers
            for(int i=0;i <list.size();i++){


                // Getting a place from the places list
                HashMap<String, String> hmPlace = list.get(i);

                // Getting latitude of the place
                double lat = Double.parseDouble(hmPlace.get("lat"));

                // Getting longitude of the place
                double lng = Double.parseDouble(hmPlace.get("lng"));

                String imageUrl = hmPlace.get("icon");
                // Getting name
                String name = hmPlace.get("place_name");

                // Getting vicinity
                String vicinity = hmPlace.get("vicinity");

                float[] distance = new float[1];

                Location.distanceBetween(mLatitude, mLongitude, lat, lng, distance);



                parkingNameList.add(name);
                parkingImageList.add(imageUrl);
                parkingVicinityList.add(vicinity);
                parkingDistanceList.add(String.valueOf(distance[0]/1000));

                Log.d("distance", String.valueOf(parkingDistanceList.get(i)));


                LatLng latLng = new LatLng(lat, lng);


            }

            ParkingListCustomAdapter adapter = new ParkingListCustomAdapter(context, parkingNameList.toArray(new String[parkingNameList.size()]), parkingImageList.toArray(new String[parkingImageList.size()]), parkingVicinityList.toArray(new String[parkingVicinityList.size()]), parkingDistanceList.toArray(new String[parkingDistanceList.size()]));
            parkingSpotList.setAdapter(adapter);
            parkingSpotList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> a, View v, int position,
                                        long id) {




                    Intent intent = new Intent(ParkingListActivity.this, ParkingDetailActivity.class);

                    intent.putExtra("name", ParkingList.get(position).get("place_name"));
                    intent.putExtra("icon", ParkingList.get(position).get("icon"));
                    intent.putExtra("lat", ParkingList.get(position).get("lat"));
                    intent.putExtra("lng", ParkingList.get(position).get("lng"));
                    intent.putExtra("vicinity", ParkingList.get(position).get("vicinity"));
                    intent.putExtra("startlat", mLatitude);
                    intent.putExtra("startlong", mLongitude);

                    startActivity(intent);
                }
            });


        }

    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

// Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

// Connecting to url
            urlConnection.connect();

// Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception download url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }

        return data;
    }





}
