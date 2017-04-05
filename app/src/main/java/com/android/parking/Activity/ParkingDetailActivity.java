package com.android.parking.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.parking.R;
import com.android.parking.Utils.GPSTracker;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.URL;
import java.util.Locale;

/**
 * Created by manumaheshwari on 7/19/16.
 */
public class ParkingDetailActivity extends AppCompatActivity {

    TextView nameTextview;
    TextView vicinityTextview;
    ImageView parkinglotIcon;
    Context context;

    String startLat;
    String startLong;
    String lat;
    String longi;


    private DrawerLayout drawerLayout;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parking_lot_detail_layout);

        context = this;
        Bundle bundle = getIntent().getExtras();

        startLat = bundle.get("startlat").toString();
        startLong = bundle.get("startlong").toString();
        lat = bundle.getString("lat");
        longi = bundle.getString("lng");

        nameTextview = (TextView) findViewById(R.id.textview_name);
        vicinityTextview = (TextView) findViewById(R.id.textview_vicinity);
        parkinglotIcon = (ImageView) findViewById(R.id.parking_imageview);

        new LoadImage(parkinglotIcon).execute(bundle.get("icon").toString());

        nameTextview.setText(bundle.get("name").toString());
        vicinityTextview.setText(bundle.get("vicinity").toString());



        LinearLayout callLayout = (LinearLayout) findViewById(R.id.call_layout);
        callLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        LinearLayout navigationLayout = (LinearLayout) findViewById(R.id.direction_layout);
        navigationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr="+ startLat +"," + startLong + "&daddr=" + lat  + "," + longi
                        ));
                startActivity(intent);
            }
        });



        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initNavigationDrawer();


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

    public void initNavigationDrawer() {

        NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();

                switch (id){


                }
                return true;
            }
        });

        /*View header = navigationView.getHeaderView(0);
        TextView tv_email = (TextView)header.findViewById(R.id.tv_email);
        tv_email.setText("raj.amalw@learn2crack.com");
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);*/

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close){

            @Override
            public void onDrawerClosed(View v){
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

}
