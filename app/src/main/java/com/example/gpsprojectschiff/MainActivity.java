package com.example.gpsprojectschiff;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Calendar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {
    TextView input;
    TextView address;
    TextView totalDistanceText;
    TextView time;
    Button reset;
    LocationManager locationManager;
    double currLat;
    double currLon;
    float totalDistance = 0f;
    double previousLat = 0.0;
    double previousLon = 0;
    long startTime = 0;
    long elapsedTime = 0;
    List<Address> addressList;
//ask him to re explain step 7 specifically
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        reset = findViewById(R.id.resetTime);
        input = findViewById(R.id.text);
        address = findViewById(R.id.address);
        time = findViewById(R.id.elapsedTime);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        totalDistanceText = findViewById(R.id.totDistance);
        addressList = new ArrayList<>();
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        /*distanceBetween(40.37482073242896, -74.565922421396, 40.38248328476193, -74.57584118345588);
        Log.d("Test", "\"Distance from SBHS to Target: "+distanceBetween(40.37482073242896, -74.565922421396, 40.38248328476193, -74.57584118345588)+" meters");

        distanceBetween(40.38248328476193, -74.57584118345588, 40.29230523512657, -74.68127815553257);
        Log.d("Test", "Distance: "+distanceBetween(40.38248328476193, -74.57584118345588, 40.29230523512657, -74.68127815553257)+" meters");

        distanceBetween(40.29230523512657, -74.68127815553257, 40.37482073242896, -74.565922421396);

        Log.d("Test", "Distance from SBHS to Target: 1197.2665 meters");*/



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overridingo
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
             Log.d("Test", "Failed Permissions");
            return;

        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        Calendar c = Calendar.getInstance();
        c = Calendar.getInstance();
        startTime = c.getTimeInMillis();
        Log.d("Time", startTime+" is the Start Time");

        //Current time in milliseconds



        Log.d("Test", "geocoder");


    }
    public float distanceBetween(double lat1, double lon1, double lat2, double lon2)
    {
        Location loc1 = new Location(""); loc1.setLatitude(lat1); loc1.setLongitude(lon1);
        Location loc2 = new Location("");
        loc2.setLatitude(lat2); loc2.setLongitude(lon2); float distanceInMeters = loc1.distanceTo(loc2);
        float distanceInMiles = (float) (distanceInMeters/1609.344);
        return distanceInMiles;

    }

    public void getEstTime(double lat1, double lon1, double lat2, double lon2)
    {
        Location loc1 = new Location(""); loc1.setLatitude(lat1); loc1.setLongitude(lon1);
        Location loc2 = new Location("");
        loc2.setLatitude(lat2); loc2.setLongitude(lon2);

    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        time.setText((SystemClock.elapsedRealtime())/1000+" seconds");
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time.setText((0+" seconds"));
            }
        });
        currLat = location.getLatitude();
        currLon = location.getLongitude();
        Calendar c = Calendar.getInstance();
        long newTime = c.getTimeInMillis();
        elapsedTime = (newTime-startTime)/1000;
        time.setText("Time to destionation: "+elapsedTime);
        Log.d("Time", "Elapsed time: "+elapsedTime);
        Log.d("Time", " New Time: "+newTime);
        Log.d("Test", "Curr Lat: "+currLat+", Curr Lon: "+currLon);
        input.setText("Curr Lat: "+currLat+", Curr Lon: "+currLon);

        Geocoder g = new Geocoder(this, new Locale("US"));
        try {
            addressList = g.getFromLocation(currLat, currLon, 2);
            //Log.d("Test", "Address: "+addressList.get);
            address.setText("Address: "+addressList.get(0).getAddressLine(0)+"");

        } catch (IOException e) {
            e.printStackTrace();

        }
        if(previousLat==0 && previousLon==0)
        {
            previousLat = currLat;
            previousLon = currLon;


        }
        else
        {
            totalDistance += distanceBetween(previousLat, previousLon, currLat, currLon);
            //int totalRoundedDistance = Math.round(totalDistance);
            /*int index = (totalDistance+"").indexOf(".");
            String roundedDistance = (totalDistance+"").substring(0, index+4);*/
            totalDistanceText.setText("Total Distance: "+totalDistance+" miles");
            previousLat = currLat;
            previousLon = currLon;

        }
        //Get current time in milliseconds
        //calculate elapsed time

    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1:
                //Code to update textview on first run after initially accepting permissions
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
                }
                else
                {


                }
        }
        return;
    }

}