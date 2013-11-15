/* GooglePlacesMap.java
 * Project E - Eric Daniels
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
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

@SuppressLint ( "SetJavaScriptEnabled")
/*
 * Google Places Map Activity
 * Description: Used with Map Activity to display map of a user 
 * 				selected category and a ListView of relative places.
 */
public class GooglePlacesMap extends Activity implements AdapterView.OnItemSelectedListener {

	// Panama City Beach Coordinates
	final private double panamaLat = 30.205971, panamaLong = -85.858862;
	
    private double latitude, longitude;
    private String type, bestProvider;
    private int milesAway = 10;
    private GooglePlacesSearch gpSearch = null;
    private ArrayList<Place> places = null;
    private ListView placesList = null;
    private ArrayAdapter<Place> adapter = null;
    private LocationManager locationManager;
    private Spinner spinner;
    private Location locationDetails;
    private WebView mapView;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_map );
        
        Bundle extras = getIntent().getExtras();
        this.type = extras.getString( "type" );
        
        // Get views
        this.spinner = (Spinner) findViewById( R.id.spinner1 );
        this.mapView = (WebView) findViewById( R.id.mapview );
        this.placesList = (ListView) findViewById( R.id.list );
        
        this.spinner.setOnItemSelectedListener( this );

        // Acquire a reference to the system Location Manager
        this.locationManager = (LocationManager) this.getSystemService( Context.LOCATION_SERVICE );
        
        if ( this.locationManager == null ) {
        	
        	Toast toast = Toast.makeText( this, "error: Failed to use the Location Service.", Toast.LENGTH_SHORT );
            toast.setGravity( Gravity.CENTER_HORIZONTAL, 0, 0 );
            toast.show();
            this.spinner.setSelection( 1 );
            
        } else {
        	
        	// Find best provider for searching locations
        	this.bestProvider = this.locationManager.getBestProvider( new Criteria(), true );
            
            if ( this.bestProvider == null ) {

                Toast toast = Toast.makeText( this, "error: Please enable Location Services.", Toast.LENGTH_SHORT );
                toast.setGravity( Gravity.CENTER_HORIZONTAL, 0, 0 );
                toast.show();
                this.spinner.setSelection( 1 );
                
            } else {
            	
            	// Ask for updates every once in a while but we don't actually care when we get them
                this.locationManager.requestLocationUpdates( this.bestProvider, 6000, 20,  new LocationListener() {

    				@Override
    				public void onLocationChanged(Location location) {}

    				@Override
    				public void onProviderDisabled(String provider) {}

    				@Override
    				public void onProviderEnabled(String provider) {}

    				@Override
    				public void onStatusChanged(String provider, int status, Bundle extras) {}
                	
                });
            }
        }

        this.gpSearch = new GooglePlacesSearch( type, getGoogleCoordinates() );
        
        placesList.setOnItemClickListener( new OnItemClickListener() {
        	
        	@Override
            public void onItemClick( AdapterView<?> parent, View view, int position, long id ) { 

                new DetailTask( places.get( (int) id ) ).execute();
            }
        });

        
        this.mapView.getSettings().setJavaScriptEnabled( true );
    }
    
    /*
     * Get Google Coordinates
     * Description: Generates Google Places API coordinates
     */
    private String getGoogleCoordinates() {
    	
    	return Double.toString( this.latitude ) + "," + Double.toString( this.longitude );
    }

    /*
     * Get Map HTML
     * Description: Gets map HTML for location selected
     */
    private String getMapHTML( double latitude, double longitude, String type, int milesAway ) {

        String radius;
        final int meters = 1609;
        radius = Integer.toString( meters * milesAway );

        // HTML and JavaScript source code retrieved from
        // https://developers.google.com/maps/documentation/javascript/examples/place-search

        String HTMLdata = "<html>  <head>    <title>Place searches</title>    <meta name=\"viewport\" content=\"initial-scale=1.0, user-scalable=no\">    <meta charset=\"utf-8\">    <link href=\"https://developers.google.com/maps/documentation/javascript/examples/default.css\" rel=\"stylesheet\">    <script src=\"https://maps.googleapis.com/maps/api/js?v=3.exp&sensor=true&libraries=places\"></script>    <script>var map;var infowindow;function initialize() {  var pyrmont = new google.maps.LatLng("
                + getGoogleCoordinates()
                + ");  map = new google.maps.Map(document.getElementById('map-canvas'), {    mapTypeId: google.maps.MapTypeId.ROADMAP,    center: pyrmont,    zoom: 13  });  var request = {    location: pyrmont,    radius: "
                + radius
                + ",    types: ['" + type + "']  };  infowindow = new google.maps.InfoWindow();  var service = new google.maps.places.PlacesService(map);  service.nearbySearch(request, callback);}function callback(results, status) {  if (status == google.maps.places.PlacesServiceStatus.OK) {    for (var i = 0; i < results.length; i++) {      createMarker(results[i]);    }  }}function createMarker(place) {  var placeLoc = place.geometry.location;  var marker = new google.maps.Marker({    map: map,    position: place.geometry.location  });  google.maps.event.addListener(marker, 'click', function() {    infowindow.setContent(place.name);    infowindow.open(map, this);  });}google.maps.event.addDomListener(window, 'load', initialize);    </script>  </head>  <body>    <div id=\"map-canvas\" style=\"width: 100%;height: 100%; float:center\"></div>  </body></html>";
        
        return HTMLdata;
    }

    /*
     * ListView Task
     * Description: AsyncTask to get Places.
     */
    class ListViewTask extends AsyncTask<Void, Void, ArrayList<Place>> {

        @Override
        protected ArrayList<Place> doInBackground( Void... unused ) {

            places = gpSearch.findPlaces();
            return places;
        }

        @Override
        protected void onPostExecute( ArrayList<Place> places ) {

        	adapter = new ArrayAdapter<Place>( GooglePlacesMap.this, android.R.layout.simple_list_item_1, places );
            placesList.setAdapter( adapter );

        }
    }

    /* 
     * Detail Task
     * Description: AsyncTask to get Google Places Detail.
     */
    class DetailTask extends AsyncTask<Void, Void, PlaceDetail> {

        private Place place;

        public DetailTask( Place place ) {

            this.place = place;
        }

        @Override
        protected PlaceDetail doInBackground( Void... unused ) {

            return gpSearch.findPlaceDetail( this.place.placeReference );
        }

        @Override
        protected void onPostExecute( PlaceDetail placeDetail ) {

        	// Load placeDetail into its activity
            Intent placeDetailIntent = new Intent( GooglePlacesMap.this, PlaceDetailActivity.class );
            placeDetailIntent.putExtra( "name", placeDetail.siteName );
            placeDetailIntent.putExtra( "rating", place.rating );
            placeDetailIntent.putExtra( "price", place.price );
            placeDetailIntent.putExtra( "address", placeDetail.address );
            placeDetailIntent.putExtra( "phonenumber", placeDetail.phoneNumber );
            placeDetailIntent.putExtra( "website", placeDetail.website );
            placeDetailIntent.putExtra( "photoRef", placeDetail.photoRef );
            placeDetailIntent.putExtra( "gpSearchType", type );
            placeDetailIntent.putExtra( "gpSearchGeoLocation", getGoogleCoordinates() );
                
            startActivity( placeDetailIntent );
        }
    }

    @Override
    /*
     * Spinner - On Item Selected
     * Description: Called when the location to be used in searching has
     * 				been set.
     */
    public void onItemSelected( AdapterView<?> arg0, View arg1, int arg2, long arg3 ) {
    	
        // "My Location" is one of the string items of the drop-down selector
        if ( this.spinner.getSelectedItem().toString().equals( "My Location" ) ) {

            // update latitude and longitude coordinates for each
            if ( this.bestProvider != null ) {

                this.locationDetails = locationManager.getLastKnownLocation( bestProvider );

                if ( this.locationDetails != null ) {

                    this.latitude = this.locationDetails.getLatitude();
                    this.longitude = this.locationDetails.getLongitude();

                    // Update GP Search Parameters
                    this.gpSearch = new GooglePlacesSearch( this.type, getGoogleCoordinates() );
                    new ListViewTask().execute();
                    	
                	this.mapView.stopLoading();
                	this.mapView.loadData( getMapHTML( this.latitude, this.longitude, this.type, this.milesAway ), "text/html", "UTF-8" );

                } else {

                    // Default to Panama City
                	this.spinner.setSelection( 1 );
                    Toast toast = Toast.makeText( this, "Failed to get current location. Defaulting to Panama City. Try again soon.",
                                    Toast.LENGTH_SHORT );
                    
                    toast.setGravity( Gravity.CENTER_HORIZONTAL, 0, 0 );
                    toast.show();
                }
            }
        }

        if ( this.spinner.getSelectedItem().toString().equals( "Panama City" ) ) {
            
        	// update latitude and longitude coordinates for each
        	this.latitude = this.panamaLat;
        	this.longitude = this.panamaLong;

        	// Update GP Search Parameters
        	this.gpSearch = new GooglePlacesSearch( type, getGoogleCoordinates() );
            new ListViewTask().execute();

            this.mapView.stopLoading();
            this.mapView.loadData( getMapHTML( this.latitude, this.longitude, this.type, this.milesAway ), "text/html", "UTF-8" );
        }
    }

    @Override
	public void onNothingSelected(AdapterView<?> arg0) {}
}
