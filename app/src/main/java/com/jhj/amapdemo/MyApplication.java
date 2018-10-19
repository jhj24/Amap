package com.jhj.amapdemo;

import android.app.Application;

import com.jhj.location.Location;

/**
 * Created by jhj on 18-8-22.
 */

public class MyApplication extends Application {

    public Location location;

    @Override
    public void onCreate() {
        super.onCreate();
        location = new Location(this);

    }
}
