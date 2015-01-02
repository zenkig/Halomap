package com.example.zenkig.halomap;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import java.util.ArrayList;
import java.util.List;

public class HaloMapsActivity extends Activity implements
        GoogleMap.OnMarkerDragListener, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMyLocationChangeListener, GoogleMap.OnInfoWindowClickListener{
//        GooglePlayServicesClient.ConnectionCallbacks,
//        GooglePlayServicesClient.OnConnectionFailedListener{

    final int RQS_GooglePlayServices = 1;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    boolean markerClicked; // default status
    TextView tvLocInfo;    // Text on the icon marker

    Circle myCircle;  // Circle for drawing on MAP
    Circle myCircleCenter; // Circle for user location
    private FragmentManager supportFragmentManager;


    // Global constants
    /* Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;


    // Info Window Class
    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;

        MyInfoWindowAdapter(){
            myContentsView = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoContents(Marker marker) {
            TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.title));
            tvTitle.setText(marker.getTitle());
            TextView tvSnippet = ((TextView)myContentsView.findViewById(R.id.snippet));
            tvSnippet.setText(marker.getSnippet());

            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            return null;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halo_maps);
        setUpMapIfNeeded();

        tvLocInfo = (TextView)findViewById(R.id.locinfo);

       // mLocationClient = new LocationClient(this, this, this); // create location client

        mMap.setMyLocationEnabled(true); // location layer does not provide data
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        mMap.setOnMapClickListener(this);

        // before loop:
        List<Marker> markers = new ArrayList<Marker>();
        mMap.setOnMapLongClickListener(this);
        // after loop
        markers.size(); // marker numbers size get


        mMap.setOnMarkerDragListener(this);
        mMap.setOnMyLocationChangeListener(this);  // listener for location change added
        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter()); // Info window listener adaptor added
        mMap.setOnInfoWindowClickListener(this);


        markerClicked = false;

        // Getting Google Play status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        // Showing status
        if(status!=ConnectionResult.SUCCESS){ // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        } else { // Google Play Services are available

            // Getting LocationManager object from System Service LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);

            // Getting Current Location
            Location location = locationManager.getLastKnownLocation(provider);
        }

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
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
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

            FragmentManager myFragmentManager = getFragmentManager();
            MapFragment myMapFragment
                    = (MapFragment)myFragmentManager.findFragmentById(R.id.map);
            mMap = myMapFragment.getMap();

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

        Marker paris = mMap.addMarker(new MarkerOptions().position(new LatLng(48.7, 2.338)).title("Paris Area"));

        mMap.moveCamera(CameraUpdateFactory.zoomTo(14f)); // default map zoom level

//        // test first default marker and show the position
//        Marker marker001 = mMap.addMarker(new MarkerOptions()
//                .position(new LatLng(10, 10))
//                .title("Hello world")
//                .draggable(true)
//                .alpha(0.8f)
//                //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.c1))
//                .title("Melbourne")
//                .snippet("Population: 4,137,400"));
//        marker001.showInfoWindow();

    }


    @Override
    public void onMapClick(LatLng point) {

        tvLocInfo.setText(point.toString()); // Clicked Location
        mMap.animateCamera(CameraUpdateFactory.newLatLng(point));

        // add Marker upon Circle center
        mMap.addMarker(new MarkerOptions()
                .position(point)
                .title(point.toString()));

        markerClicked = false;

    }

    @Override
    public void onMapLongClick(LatLng point) {

        int disRadius = 200;  // default circle size

//        ArrayList<Markers> markers = new ArrayList<Markers>();
        tvLocInfo.setText("New marker with Circle added@" + point.toString());

        // Draw circle on map
        CircleOptions circleOptions = new CircleOptions()
               .center(point)   //set center
               .radius(disRadius)   //set radius in meters
               .fillColor(0x40ff0000)  //semi-transparent
               .strokeColor(Color.RED)
               .strokeWidth(5);

        // Get back the mutable Circle
        //mMap.addCircle(circleOptions);
        Circle circle = mMap.addCircle(circleOptions);
        //To alter the shape of the circle after it has been added,
        //can call Circle.setRadius() or Circle.setCenter() and provide new values.

        // add Marker upon circle center and show the position
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(point)
                .title(point.toString()));
        markers.add(marker);

        markerClicked = false;

    }

    @Override
    public void onMarkerDrag(Marker marker) {
       tvLocInfo.setText("Marker " + marker.getId() + " Drag@" + marker.getPosition());
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
       tvLocInfo.setText("Marker " + marker.getId() + " DragEnd");
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
       tvLocInfo.setText("Marker " + marker.getId() + " DragStart");

    }


    @Override
    public void onMyLocationChange(Location location) {

        int zoomFactor = 20;

        tvLocInfo.setText("New circle added@" + location.toString());  // text info on current location

        LatLng locLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        double accuracy = location.getAccuracy();
        accuracy = accuracy * zoomFactor;

        if(myCircleCenter == null){
            CircleOptions circleOptions = new CircleOptions()
                    .center(locLatLng)   //set center
                    .radius(accuracy)   //set radius in meters
                    .fillColor(0x400000ff) // my location circle color
                    .strokeColor(Color.BLACK)
                    .strokeWidth(5);

            myCircleCenter = mMap.addCircle(circleOptions);
        }else{
            myCircleCenter.setCenter(locLatLng);
            myCircleCenter.setRadius(accuracy);
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLng(locLatLng));

        /*   // location info shown on map in realtime
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        tvLocInfo.setText(
                "lat: " + lat + "\n" +
                        "lon: " + lon);
        */
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        Toast.makeText(getBaseContext(),
                "See Info Window @" + marker.getId(),
                Toast.LENGTH_SHORT).show();

    }

}


//// For adding makers info
//// before loop:
//List<Marker> markers = new ArrayList<Marker>();
//
//// inside your loop:
//Marker marker = myMap.addMarker(new MarkerOptions().position(new LatLng(geo1Dub,geo2Dub)));
//markers.add(marker);
//
//// after loop:
//markers.size(); // default





