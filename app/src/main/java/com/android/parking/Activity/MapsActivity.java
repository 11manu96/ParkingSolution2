package com.android.parking.Activity;


import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.parking.R;
import com.android.parking.Utils.GPSTracker;
import com.android.parking.Utils.PlaceJSONParser;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.android.parking.R.string.app_name;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {



    private GoogleMap mMap;
    double mLatitude;
    double mLongitude;
    GPSTracker tracker;
    LatLngBounds.Builder builder;
    CameraUpdate cu;
    StringBuilder sb;
    PlacesTask placesTask;
    Marker currPosMarker;
    Context context;

    //marker for finding car
    Marker marker;


    //UI Elements
    private NavigationView navigationView;


    String currPosMrkerid;
    NestedScrollView bottomParkingView;
    private int selectedNavItemId;
    DrawerLayout drawer;




    HashMap<String, HashMap> extraMarkerInfo = new HashMap<String, HashMap>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        setContentView(R.layout.main_activity_layout);


        setToolBar();
        setNavigationDrawer();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        tracker = new GPSTracker(this);
        mLatitude = tracker.getLatitude();
        mLongitude = tracker.getLongitude();
        builder = new LatLngBounds.Builder();




        navigationView = (NavigationView) findViewById(R.id.navigationView);


        context = this;

        bottomParkingView = (NestedScrollView) findViewById(R.id.bottom_sheet);

        sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        if(mLatitude != 0.0 && mLongitude != 0.0) {
            sb.append("location=" + mLatitude + "," + mLongitude);
        }
        else {
            sb.append("location=" + "128.586414" + "," + "77.324220");
        }

        sb.append("&radius=5000");
        sb.append("&types="+"parking");
        sb.append("&sensor=true");
        sb.append("&key=YOUR_API_KEY");


// Creating a new non-ui thread task to download Google place json data
        placesTask = new PlacesTask();

// Invokes the "doInBackground()" method of the class PlaceTask
        placesTask.execute(sb.toString());
        Log.d("the url ",sb.toString());

        //setUpNavView();


        handleIntent(getIntent());
        /**call the map call back to know map is loaded or not*/

    }

    @Override
    protected void onNewIntent(Intent intent) {

        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;





        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                final HashMap<String, String> data = extraMarkerInfo.get(marker.getId());

                if(!marker.getId().equals(currPosMrkerid)) {

                    View bottomSheet = (View)findViewById(R.id.bottom_sheet);

                    LinearLayout bottomLayout = (LinearLayout) findViewById(R.id.bottomsheet);
                    bottomLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(MapsActivity.this, ParkingDetailActivity.class);

                            intent.putExtra("name", data.get("place_name"));
                            intent.putExtra("icon", data.get("icon"));
                            intent.putExtra("lat", data.get("lat"));
                            intent.putExtra("lng", data.get("lng"));
                            intent.putExtra("vicinity", data.get("vicinity"));
                            intent.putExtra("startlat", mLatitude);
                            intent.putExtra("startlong", mLongitude);

                            startActivity(intent);
                            Toast.makeText(MapsActivity.this, "hello", Toast.LENGTH_SHORT).show();
                        }
                    });

                    ImageView img = (ImageView) findViewById(R.id.place_picture);
                    new LoadImage(img).execute(data.get("icon"));

                    TextView nametv = (TextView) findViewById(R.id.place_name_textview);
                    nametv.setText(data.get("place_name"));

                    TextView vicinityTextView = (TextView) findViewById(R.id.vicinity);
                    vicinityTextView.setText(data.get("vicinity"));

                    //Log.d("vicinity", data.get(TA));
                    TextView distanceTextView = (TextView) findViewById(R.id.distance_textview);
                    distanceTextView.setText(data.get("distance"));

                    BottomSheetBehavior behavior = (BottomSheetBehavior)BottomSheetBehavior.from(bottomSheet);

                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                }
                return true;

            }
        });

        setToolBar();
        setNavigationDrawer();

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
        protected void onPostExecute(List<HashMap<String,String>> list) {

// Clears all the existing markers

            if (list != null) {



                builder = new LatLngBounds.Builder();

                final LatLng currentPos = new LatLng(mLatitude, mLongitude);

                currPosMarker = mMap.addMarker(new MarkerOptions()
                        .position(currentPos)
                        .title("Current Location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                currPosMrkerid = currPosMarker.getId();
                builder.include(currentPos);


                for (int i = 0; i < list.size(); i++) {

                    // Creating a marker

                    // Getting a place from the places list
                    HashMap<String, String> hmPlace = list.get(i);

                    // Getting latitude of the place
                    double lat = Double.parseDouble(hmPlace.get("lat"));

                    // Getting longitude of the place
                    double lng = Double.parseDouble(hmPlace.get("lng"));

                    // Getting name
                    String name = hmPlace.get("place_name");

                    // Getting vicinity
                    String vicinity = hmPlace.get("vicinity");
                    String imageUrl = hmPlace.get("icon");

                    float[] distance = new float[1];

                    Location.distanceBetween(mLatitude, mLongitude, lat, lng, distance);
                    hmPlace.put("distance", String.valueOf(distance[0] / 1000));

                    Log.d("vicinity", vicinity);

                    LatLng latLng = new LatLng(lat, lng);
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(name + " : " + vicinity));
                    extraMarkerInfo.put(marker.getId(), hmPlace);
                    builder.include(latLng);

                }

                LatLngBounds bounds = builder.build();

                if (bounds != null) {
                    /**create the camera with bounds and padding to set into map*/
                    int padding = 200;
                    cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                        @Override
                        public void onMapLoaded() {
                            //**set animated zoom camera into map*//*
                            mMap.animateCamera(cu);

                        }
                    });

                }

            }

            else{
                Toast.makeText(context, "Please check your interent connection", Toast.LENGTH_SHORT).show();
            }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);


        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem mSearchMenuItem = menu.findItem(R.id.search);
        SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchMenuItem);


       /* SearchView searchView =
                  (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        */



        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(
                new ComponentName(getApplicationContext(), MapsActivity.class)));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case R.id.list_view:

                Intent intent = new Intent(this, ParkingListActivity.class);
                intent.putExtra("url", sb.toString());
                startActivity(intent);
                Toast.makeText(MapsActivity.this, this.getClass().getSimpleName(), Toast.LENGTH_SHORT).show();
                break;

            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }


    private class LoadImage extends AsyncTask<String, String, Bitmap> {

        Bitmap bitmap;
        ImageView img;

        public LoadImage(ImageView imageView){
            this.img = imageView;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {

            if(image != null){
                img.setImageBitmap(image);

            }else{

                Toast.makeText(context, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();

            }
        }
    }


    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {


        private View view;

        public CustomInfoWindowAdapter() {
            view = getLayoutInflater().inflate(R.layout.markmycar_custom_info_window,
                    null);
        }

        @Override
        public View getInfoWindow(Marker marker) {

            return view;
        }

        @Override
        public View getInfoContents(Marker marker) {

            if (MapsActivity.this.marker != null
                    && MapsActivity.this.marker.isInfoWindowShown()) {
                MapsActivity.this.marker.hideInfoWindow();
                MapsActivity.this.marker.showInfoWindow();
            }
            return null;
        }
    }

    public void showConfirmationDialog(final LatLng latlng){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to add the lot at the current location ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String url = "https://maps.googleapis.com/maps/api/staticmap?center="+ latlng.latitude +"," + latlng.longitude + "&zoom=14&size=640x400&path=weight:3%7Ccolor:blue%7Cenc:{coaHnetiVjM??_SkM??~R&markers=color:red%7Clabel:C%7C" + latlng.latitude+","+ latlng.longitude +"&key=AIzaSyCIyhzHOcA3CMjuInT1vKKEJs9qhw9l49Y";

                        Intent intent = new Intent(context, AddParkingLotActivity.class);
                        intent.putExtra("mapScreenshot", url);
                        startActivity(intent);



                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void setToolBar() {

        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.hamburger);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("hello");

    }


    private void setNavigationDrawer() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navView = (NavigationView) findViewById(R.id.navigationView);

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                selectedNavItemId = menuItem.getItemId();

                //Toast.makeText(MapsActivity.this, "hello", Toast.LENGTH_SHORT).show();


                switch (selectedNavItemId){

                    case R.id.add_paring_lot:
                /*Intent intent = new Intent(this, );
                startActivity(intent);*/
                        drawer.closeDrawer(GravityCompat.START);

                        Toast.makeText(MapsActivity.this, "Long press where you want to add the parking Lot", Toast.LENGTH_SHORT).show();
                        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                            @Override
                            public void onMapLongClick(LatLng latLng) {


                                mMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .title("You are here")
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

                                showConfirmationDialog(latLng);


                            }
                        });


                        break;

                    case R.id.mark_my_car:
                        GPSTracker tracker = new GPSTracker(context);
                        if(menuItem.getTitle().equals("Find My Car")) {
                            menuItem.setTitle("Mark My Car");
                            SharedPreferences prefs = context.getSharedPreferences(
                                    "com.android.parking", Context.MODE_PRIVATE);
                            mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
                            marker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(prefs.getString("MyCarLatitiude", null)),Double.parseDouble(prefs.getString("MyCarLongitude", null))))
                                    .title("Here's your car"));
                            MapsActivity.this.marker.showInfoWindow();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(prefs.getString("MyCarLatitiude", null)),Double.parseDouble(prefs.getString("MyCarLongitude", null))), 15));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                        }
                        else {
                            menuItem.setTitle("Find My Car");
                            SharedPreferences prefs = context.getSharedPreferences(
                                    "com.android.parking", Context.MODE_PRIVATE);
                            prefs.edit().putString("MyCarLatitiude", String.valueOf(tracker.latitude)).apply();
                            prefs.edit().putString("MyCarLongitude", String.valueOf(tracker.longitude)).apply();
                            prefs.edit().putString("car_location_saved", "true").apply();
                            mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
                            marker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(prefs.getString("MyCarLatitiude", null)),Double.parseDouble(prefs.getString("MyCarLongitude", null))))
                                    .title("Here's your car"));
                            MapsActivity.this.marker.showInfoWindow();
                            Toast.makeText(MapsActivity.this, "Current Location Saved", Toast.LENGTH_SHORT).show();
                        }
                        break;


                }


                return onOptionsItemSelected(menuItem);
            }


        });
    }


}
