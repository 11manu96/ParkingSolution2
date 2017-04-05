package com.android.parking.Utils;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;


/**
 * Created by manumaheshwari on 7/14/16.
 */
public class BaseActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {


/*
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        *//**
         * This is going to be our actual root layout.
         *//*
        *//*fullLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        *//**//**
         * {@link FrameLayout} to inflate the child's view. We could also use a {@link android.view.ViewStub}
         *//**//*
        FrameLayout activityContainer = (FrameLayout) fullLayout.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);

        *//**//**
         * Note that we don't pass the child's layoutId to the parent,
         * instead we pass it our inflated layout.*//*

        super.setContentView(fullLayout);





    }*/

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

    /**
     * Helper method that can be used by child classes to
     * specify that they don't want a {@link Toolbar}
     * @return true
     */


    /**
     * Helper method to allow child classes to opt-out of having the
     * hamburger menu.
     * @return
     */




}