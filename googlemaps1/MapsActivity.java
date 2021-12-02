package com.example.googlemaps1;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.googlemaps1.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    final private int REQUEST_COARSE_ACCESS = 123;
    boolean permissionGranted = false;
    LocationManager lm;
    LocationListener locationListener;



    @Override
    public String getSystemServiceName(Class<?> serviceClass) {
        return super.getSystemServiceName(serviceClass);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();

        if(ActivityCompat.checkSelfPermission(this,ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                ACCESS_FINE_LOCATION}, REQUEST_COARSE_ACCESS);
            return;
        }else {
            permissionGranted = true;
        }

        if(permissionGranted){
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
        }

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);



        LatLng point1 = new LatLng(55.006866, -7.327820);
        LatLng point2 = new LatLng(55.005986, -7.320363);

        mMap.addMarker(new MarkerOptions().position(point1).title("Tour Start"));
        mMap.addMarker(new MarkerOptions().position(point2).title("Timber Quay"));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point1, 14f));


        Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(point1, point2)
                .width(5)
                .color(Color.RED));



        

    }

    private class MyLocationListener implements LocationListener{

        Marker currentLocation = mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Current Location"));

        @Override
        public void onLocationChanged(@NonNull Location location) {

            if (location != null){

                //Toast.makeText(getBaseContext(),"Current Location : Lat: " + location.getLatitude() + " Lng: " + location.getLongitude(), Toast.LENGTH_LONG).show();
                LatLng p = new LatLng(location.getLatitude(), location.getLongitude());


                currentLocation.setPosition(p);

               // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(p, 12.0f));

                LatLng argyleBar = new LatLng(55.006866, -7.327820);



                Location currentLocation = new Location("this");
                currentLocation.setLongitude(p.longitude);
                currentLocation.setLatitude(p.latitude);

                Location argyleLocation = new Location("this");
                argyleLocation.setLongitude(argyleBar.longitude);
                argyleLocation.setLatitude(argyleBar.latitude);

                double lengthInMeters = currentLocation.distanceTo(argyleLocation);
                long lengthRounded = Math.round(lengthInMeters);

                Toast.makeText(getBaseContext(), "Distance to start point: " + lengthRounded  + "M", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {

        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_COARSE_ACCESS:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    permissionGranted = true;
                    if(ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED){
                        return;
                    }
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, locationListener);
                }else{
                    permissionGranted = false;
                }
                break;
            default: super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }else{
            permissionGranted = true;
        }
        if (permissionGranted){
            lm.removeUpdates(locationListener);
        }
    }

    public void distanceToStart(Location location, Location startPoint){

        double lengthInMeters = location.distanceTo(startPoint);
        long lengthRounded = Math.round(lengthInMeters);

        Toast.makeText(getBaseContext(), "Distance to the start is: " + lengthRounded  + "M", Toast.LENGTH_SHORT).show();
    }
}
