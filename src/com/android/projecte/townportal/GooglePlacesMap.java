/* GooglePlacesMap.java
 * Project E - Eric Daniels
 * Used with Google Maps activity page to display map of user selected category
 *   and ListView of places
 */

package com.android.projecte.townportal;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

@SuppressLint ( "SetJavaScriptEnabled")
public class GooglePlacesMap extends Activity implements
        AdapterView.OnItemSelectedListener, ListView.OnItemClickListener,
        View.OnClickListener, LocationListener {

    public double latitude = 30.205971;
    public double longitude = -85.858862;
    public String type = "restaurant";
    public int milesAway = 10;
    private boolean firstTime = true;

    GooglePlacesSearch gpSearch = null;
    ArrayList<Place> arrayList = null;
    PlaceDetail placeDetail = null;
    PlacePhoto placePhoto = null;

    ListView lv = null;
    ArrayAdapter<Place> arrayAdapter = null;

    private LocationManager locationManager;
    private Spinner spinner;
    private Location locationDetails;
    private String geoLocation;
    private String bestProvider;
    private WebView mapView;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        Bundle b = getIntent().getExtras();
        type = b.getString( "type" );
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_map );

        // Acquire a reference to the system Location Manager
        try {
            locationManager = (LocationManager) this
                    .getSystemService( Context.LOCATION_SERVICE );
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        spinner = (Spinner) findViewById( R.id.spinner1 );
        spinner.setOnItemSelectedListener( this );
        
        bestProvider = locationManager.getBestProvider( new Criteria(), true );
        
        if ( bestProvider == null ) {

            Toast toast = Toast.makeText( this, "error: Please enable location services.", Toast.LENGTH_SHORT );
            toast.setGravity( Gravity.CENTER_HORIZONTAL, 0, 0 );
            toast.show();
            spinner.setSelection( 1 );
            
        } else
            locationManager.requestLocationUpdates( bestProvider, 6000, 20,  this );

        String geoLocation = Double.toString( latitude ) + "," + Double.toString( longitude );

        gpSearch = new GooglePlacesSearch( type, geoLocation );
        lv = (ListView) findViewById( R.id.list );
        lv.setOnItemClickListener( this );

        mapView = (WebView) findViewById( R.id.mapview );
        mapView.getSettings().setJavaScriptEnabled( true );

        try {
            
            mapView.loadData( getMapHTML( latitude, longitude, type, milesAway ), "text/html", null );

            // starting the AsynTask ListViewTask
            new ListViewTask().execute();
            
        } catch ( Exception e ) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.map, menu );
        return true;

    }

    private String getMapHTML( double latitude, double longitude, String type,
            int milesAway ) {

        String radius;
        final int meters = 1609;
        radius = Integer.toString( meters * milesAway );
        String googleCoordinates = Double.toString( latitude ) + ","
                + Double.toString( longitude );

        // HTML and javascript source code sourced from
        // https://developers.google.com/maps/documentation/javascript/examples/place-search

        String HTMLdata = "<html>  <head>    <title>Place searches</title>    <meta name=\"viewport\" content=\"initial-scale=1.0, user-scalable=no\">    <meta charset=\"utf-8\">    <link href=\"https://developers.google.com/maps/documentation/javascript/examples/default.css\" rel=\"stylesheet\">    <script src=\"https://maps.googleapis.com/maps/api/js?v=3.exp&sensor=true&libraries=places\"></script>    <script>var map;var infowindow;function initialize() {  var pyrmont = new google.maps.LatLng("
                + googleCoordinates
                + ");  map = new google.maps.Map(document.getElementById('map-canvas'), {    mapTypeId: google.maps.MapTypeId.ROADMAP,    center: pyrmont,    zoom: 13  });  var request = {    location: pyrmont,    radius: "
                + radius
                + ",    types: ['"
                + type
                + "']  };  infowindow = new google.maps.InfoWindow();  var service = new google.maps.places.PlacesService(map);  service.nearbySearch(request, callback);}function callback(results, status) {  if (status == google.maps.places.PlacesServiceStatus.OK) {    for (var i = 0; i < results.length; i++) {      createMarker(results[i]);    }  }}function createMarker(place) {  var placeLoc = place.geometry.location;  var marker = new google.maps.Marker({    map: map,    position: place.geometry.location  });  google.maps.event.addListener(marker, 'click', function() {    infowindow.setContent(place.name);    infowindow.open(map, this);  });}google.maps.event.addDomListener(window, 'load', initialize);    </script>  </head>  <body>    <div id=\"map-canvas\" style=\"width: 100%;height: 100%; float:center\"></div>  </body></html>";
        return HTMLdata;
    }

    class ListViewTask extends AsyncTask<Void, Void, ArrayList<Place>> {

        @Override
        protected ArrayList<Place> doInBackground( Void... unused ) {

            arrayList = gpSearch.findPlaces();
            return arrayList;
        }

        @Override
        protected void onPostExecute( ArrayList<Place> _placesList ) {

            try {
                arrayAdapter = new ArrayAdapter<Place>( GooglePlacesMap.this,
                        android.R.layout.simple_list_item_1, _placesList );
                lv.setAdapter( arrayAdapter );
            } catch ( Exception e ) {
                e.printStackTrace();
            }

        }
    }

    // AsyncTask to get Google Places Detail
    class DetailTask extends AsyncTask<Void, Void, PlaceDetail> {

        private Place place;

        public DetailTask( Place place ) {

            this.place = place;
        }

        @Override
        protected PlaceDetail doInBackground( Void... unused ) {

            placeDetail = gpSearch.findPlaceDetail( this.place.getPlaceReference() );
            return placeDetail;
        }

        @Override
        protected void onPostExecute( PlaceDetail theDetail ) {

            placeDetail = theDetail;

            Intent placeDetailIntent = new Intent( GooglePlacesMap.this, PlaceDetailActivity.class );
            placeDetailIntent.putExtra( "name", placeDetail.getSiteName() );
            placeDetailIntent.putExtra( "rating", place.getRating() );
            placeDetailIntent.putExtra( "price", place.getPrice() );
            placeDetailIntent.putExtra( "address", placeDetail.getAddress() );
            placeDetailIntent.putExtra( "phonenumber", placeDetail.getPhoneNumber() );
            placeDetailIntent.putExtra( "website", placeDetail.getWebsite() );
            placeDetailIntent.putExtra( "photoRef", theDetail.getPhotoRef() );
            placeDetailIntent.putExtra( "gpSearchType", type );
            placeDetailIntent.putExtra( "gpSearchGeoLocation", Double.toString( latitude ) + "," + Double.toString( longitude ) );
                
            startActivity( placeDetailIntent );
        }
    }

    @Override
    public void onItemClick( AdapterView<?> parent, View view, int position,
            long id ) {

        Place place = arrayList.get( (int) id );

        // starting the AsyncTask DetailTask
        new DetailTask( place ).execute();

    }

    public void onItemSelected( AdapterView<?> arg0, View arg1, int arg2,
            long arg3 ) {

        // "My Location" is one of the string items of the drop-down selector
        if ( spinner.getSelectedItem().toString().equals( "My Location" ) ) {

            // update latitude and longitude coordinates for each
            if ( bestProvider != null ) {

                locationDetails = locationManager.getLastKnownLocation( bestProvider );

                if ( locationDetails != null ) {

                    latitude = locationDetails.getLatitude();
                    longitude = locationDetails.getLongitude();
                    geoLocation = Double.toString( latitude ) + "," + Double.toString( longitude );

                    gpSearch = new GooglePlacesSearch( type, geoLocation );
                    // starting the AsynTask ListViewTask
                    new ListViewTask().execute();

                    try {
                        mapView.stopLoading();
                        mapView.loadData( getMapHTML( latitude, longitude, type, milesAway ), "text/html", "UTF-8" );

                    } catch ( NullPointerException e ) {
                        e.printStackTrace();
                    }

                } else {

                    // Default to Panama City
                    spinner.setSelection( 1 );
                    Toast toast = Toast
                            .makeText( this, "Failed to get current location. Defaulting to Panama City. Try again soon.",
                                    Toast.LENGTH_SHORT );
                    toast.setGravity( Gravity.CENTER_HORIZONTAL, 0, 0 );
                    toast.show();
                }

            }

        }

        if ( spinner.getSelectedItem().toString().equals( "Panama City" ) ) {
            // update latitude and longitude coordinates for each
            latitude = 30.205971;
            longitude = -85.858862;

            if ( !firstTime ) {
                // only load again if this isn't first load that occurs in
                // onCreate
                String geoLocation = Double.toString( latitude ) + "," + Double.toString( longitude );
                gpSearch = new GooglePlacesSearch( type, geoLocation );

                // starting the AsynTask ListViewTask
                new ListViewTask().execute();
            }

            try {
                mapView.stopLoading();
                mapView.loadData( getMapHTML( latitude, longitude, type, milesAway ), "text/html", null );

            } catch ( NullPointerException e ) {
                e.printStackTrace();

            }

        }
        firstTime = false;
    }

    public void onNothingSelected( AdapterView<?> arg0 ) {

        return;

    }

    public void onClick( View view ) {

        return;

    }

    @Override
    public void onLocationChanged( Location arg0 ) {

        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderDisabled( String arg0 ) {

        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled( String arg0 ) {

        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged( String arg0, int arg1, Bundle arg2 ) {

        // TODO Auto-generated method stub

    }

}
