package com.example.p10ps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button btnCheck, btnStartDetect, btnStopDetect, btnMusic;

    String folderLocation;

    TextView tvLastKnownLocation, tvLongtitude, tvLatitude;

    private GoogleMap map;

    Double lastLat, lastLong;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnCheck = findViewById(R.id.btnCheck);
        btnStartDetect = findViewById(R.id.btnStartDetect);
        btnStopDetect = findViewById(R.id.btnStopDetect);
        btnMusic = findViewById(R.id.btnPlayMusic);
        tvLastKnownLocation = findViewById(R.id.textViewLast);
        tvLatitude = findViewById(R.id.textViewLatitude);
        tvLongtitude = findViewById(R.id.textViewLongtitude);

        //Check for permissions
        askPermission();
        checkPermission();


        //Folder Creation
        folderLocation = getFilesDir().getAbsolutePath() + "/MyFolder2";

        File folder = new File(folderLocation);
        if (folder.exists() == false) {
            boolean result = folder.mkdir();
            if (result = true){
                Log.d("File Read/Write", "Folder Created");
            }
        }


        FusedLocationProviderClient client;

        client = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    String msg = "Lat : " + location.getLatitude() + "Long : " + location.getLongitude();
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                    Double lastLat = location.getLatitude();
                    Double lastLong = location.getLongitude();
                    LatLng poi_lastLocation = new LatLng(lastLat, lastLong);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(poi_lastLocation, 15));
                }
                else {
                    Toast.makeText(MainActivity.this, "Error finding last location", Toast.LENGTH_LONG).show();
                }
            }
        });


        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment)fm.findFragmentById(R.id.map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Toast.makeText(getApplicationContext(), "onMapReady", Toast.LENGTH_SHORT).show();

                map = googleMap;


                UiSettings ui = map.getUiSettings();

                ui.setCompassEnabled(true);
                ui.setZoomControlsEnabled(true);
            }
        });
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location data = locationResult.getLastLocation();
                    double lat = data.getLatitude();
                    double lng = data.getLongitude();
                    lastLat = lat;
                    lastLong = lng;

                    LatLng poi_CurrentLocation = new LatLng(lastLat, lastLong);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(poi_CurrentLocation,15));
                    //map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(312,123),15));
                    Log.d("Testing123", String.valueOf(poi_CurrentLocation));
                    System.out.println(poi_CurrentLocation + "123");
                    tvLatitude.setText(String.valueOf(lastLat));
                    tvLongtitude.setText(String.valueOf(lastLong));
                    try {
                        String folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Folder";
                        File targetFile = new File(folderLocation, "location.txt");
                        FileWriter writer = new FileWriter(targetFile, true);
                        writer.write(String.valueOf(poi_CurrentLocation));
                        writer.flush();
                        writer.close();
                    } catch (IOException e) {
                        Toast.makeText(MainActivity.this, "Failed to write!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                }
            };
        };

        btnStartDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkPermission();
                //Declare and display map
                LocationRequest mLocationRequest = LocationRequest.create();
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                mLocationRequest.setInterval(10000);
                mLocationRequest.setFastestInterval(5000);
                mLocationRequest.setSmallestDisplacement(100);

                FusedLocationProviderClient client;

                client = LocationServices.getFusedLocationProviderClient(MainActivity.this);

                client.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
            }
        });
        btnStopDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
                client.removeLocationUpdates(mLocationCallback);
            }
        });
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent checkRecords = new Intent(MainActivity.this, RecordsActivity.class);

                startActivity(checkRecords);
            }
        });
        btnMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(MainActivity.this, MyService.class));
            }
        });

    }
    private void askPermission(){
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}, 102);
    }
    private boolean checkPermission(){
        int permissionCheck_read = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionCheck_write = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCheck_coarse = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_fine = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCheck_internet = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET);

        if (permissionCheck_read == PermissionChecker.PERMISSION_GRANTED || permissionCheck_write == PermissionChecker.PERMISSION_GRANTED
        || permissionCheck_coarse == PermissionChecker.PERMISSION_GRANTED || permissionCheck_fine == PermissionChecker.PERMISSION_GRANTED
        || permissionCheck_internet == PermissionChecker.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }
    public void onLocationChanged(Location location){
        tvLastKnownLocation = findViewById(R.id.textViewLast);
        tvLatitude = findViewById(R.id.textViewLatitude);
        tvLongtitude = findViewById(R.id.textViewLongtitude);

        tvLatitude.setText((int) location.getLatitude());
        tvLongtitude.setText((int) location.getLongitude());
    }
}