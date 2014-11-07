package com.example.zenkig.halomap;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import android.location.Location;
import android.widget.Toast;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class HaloMapsActivity extends FragmentActivity implements
        GoogleMap.OnMarkerDragListener, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMyLocationChangeListener {

    final int RQS_GooglePlayServices = 1;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    boolean markerClicked; // default status
    TextView tvLocInfo;    // Text on the icon marker

    Circle myCircle;  // Circle for drawing on MAP


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halo_maps);
        setUpMapIfNeeded();

        //tvLocInfo = (TextView)findViewById(R.id.locinfo);


        mMap.setMyLocationEnabled(true);

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        //mMap.setOnMarkerClickListener(this);
        mMap.setOnMarkerDragListener(this);

        mMap.setOnMyLocationChangeListener(this);  // listener for location change added


        markerClicked = false;

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if (resultCode == ConnectionResult.SUCCESS){
            Toast.makeText(getApplicationContext(),
                    "isGooglePlayServicesAvailable SUCCESS",
                    Toast.LENGTH_LONG).show();
        }else{
            GooglePlayServicesUtil.getErrorDialog(resultCode, this, RQS_GooglePlayServices);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_legalnotices:
                String LicenseInfo = GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(
                        getApplicationContext());
                AlertDialog.Builder LicenseDialog = new AlertDialog.Builder(HaloMapsActivity.this);
                LicenseDialog.setTitle("Legal Notices");
                LicenseDialog.setMessage(LicenseInfo);
                LicenseDialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
        private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Africa Origin"));

        // add marker and show the position
        Marker marker001 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(10, 10))
                .title("Hello world")
                .draggable(true)
                .alpha(0.8f)
                //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.c1))
                .title("Melbourne")
                .snippet("Population: 4,137,400"));
        marker001.showInfoWindow();

    }


    @Override
    public void onMapClick(LatLng point) {

        int distance = 50000;

        //  tvLocInfo.setText(point.toString());

        mMap.animateCamera(CameraUpdateFactory.newLatLng(point));


        // Draw circle
        CircleOptions circleOptions = new CircleOptions()
                .center(point)   //set center
                .radius(distance)   //set radius in meters
                .fillColor(Color.TRANSPARENT)  //default
                .strokeColor(Color.BLUE)
                .strokeWidth(5);

        mMap.addCircle(circleOptions);
        //myCircle =


        // add Marker upon Circle center
        mMap.addMarker(new MarkerOptions()
                .position(point)
                .title(point.toString()));

        markerClicked = false;

    }

    @Override
    public void onMapLongClick(LatLng point) {
        int distance = 50000;

//        tvLocInfo.setText("New marker added@" + point.toString());
//        mMap.addMarker(new MarkerOptions()
//                .position(point)
//                .draggable(true));


        // Draw circle on map
        CircleOptions circleOptions = new CircleOptions()
               .center(point)   //set center
               .radius(distance)   //set radius in meters
               .fillColor(0x40ff0000)  //semi-transparent
               .strokeColor(Color.RED)
               .strokeWidth(5);

       mMap.addCircle(circleOptions);
        //myCircle =

        // add Marker upon circle center and show the position
        mMap.addMarker(new MarkerOptions()
                .position(point)
                .title(point.toString()));

        markerClicked = false;

    }

    @Override
    public void onMarkerDrag(Marker marker) {
//       tvLocInfo.setText("Marker " + marker.getId() + " Drag@" + marker.getPosition());
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
 //      tvLocInfo.setText("Marker " + marker.getId() + " DragEnd");
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
//       tvLocInfo.setText("Marker " + marker.getId() + " DragStart");

    }


    @Override
    public void onMyLocationChange(Location location) {
        // tvLocInfo.setText("New circle added@" + location.toString());  // text info on current location

        LatLng locLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        double accuracy = location.getAccuracy();

        if(myCircle == null){
            CircleOptions circleOptions = new CircleOptions()
                    .center(locLatLng)   //set center
                    .radius(accuracy)   //set radius in meters
                    .fillColor(Color.RED)
                    .strokeColor(Color.BLACK)
                    .strokeWidth(5);

            myCircle = mMap.addCircle(circleOptions);
        }else{
            myCircle.setCenter(locLatLng);
            myCircle.setRadius(accuracy);
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLng(locLatLng));
    }


}





