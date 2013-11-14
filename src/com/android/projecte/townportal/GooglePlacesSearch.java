/* GooglePlacesSearch.java
 * Project E - Eric Daniels
 */

package com.android.projecte.townportal;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/*
 * Google Places Search
 * Description: Uses JSON query to retrieve Google Places information 
 *    			about places with a type selected by user.
 */
public class GooglePlacesSearch {

    public String location = "30.205971,-85.858862",
    		      radius = "16100", // in meters - about 10 miles
                  types,
                  sensor = "false",
    		      APIKey = "AIzaSyBz7p2E8oDDBYJYvL3RM3cFjHCJDkpuqwU",
    			  reference = null;
    
    BitmapFactory.Options bmOptions;

    public GooglePlacesSearch(String placeType, String geoLocation) {

    	this.types = placeType;
    	this.location = geoLocation;

    }

    public String FormGoogleSearchURL() {

        String returnVal = new String();

        returnVal = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
        returnVal += this.location + "&radius=" + this.radius + "&types=" + this.types
                    + "&sensor=" + this.sensor + "&key=" + this.APIKey;

        return ( returnVal );
    }

    public ArrayList<Place> findPlaces() {

        String urlString = FormGoogleSearchURL();

        try {
            String json = getJSON( urlString );
            JSONObject object = new JSONObject( json );
            JSONArray array = object.getJSONArray( "results" );

            ArrayList<Place> arrayList = new ArrayList<Place>();
            
            for ( int i = 0; i < array.length(); i++ ) {
                
                try {
                    
                    Place place = Place.jsonToPlace( (JSONObject) array.get( i ) );
                    arrayList.add( place );
                    
                } catch ( Exception e ) {
                    
                    e.printStackTrace();
                }
            }
            return arrayList;
            
        } catch ( JSONException ex ) {
            
            ex.printStackTrace();
        }
        return null;
    }

    protected String getJSON( String _URL ) {

        return getURLContent( _URL );
    }

    private String getURLContent( String _URL ) {

        StringBuilder content = new StringBuilder();
        try {
            URL url = new URL( _URL );
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( urlConnection.getInputStream() ), 8 );
            String line;
            
            while ( ( line = bufferedReader.readLine() ) != null )
                content.append( line + "\n" );
            
            bufferedReader.close();
            
        } catch ( Exception e ) {
            
            e.printStackTrace();
        }
        return content.toString();
    }

    public String GetPlaceDetailUrl( String placeRef ) {

        String returnVal = new String();

        returnVal = "https://maps.googleapis.com/maps/api/place/details/json?reference=";
        returnVal += placeRef + "&sensor=" + this.sensor + "&key=" + this.APIKey;

        return ( returnVal );
    }

    public String GetPlaceDetailPhotoUrl( String photoRef ) {

        String returnVal = new String();
        returnVal = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400";
        returnVal += "&photoreference=" + photoRef + "&sensor=" + this.sensor + "&key=" + this.APIKey;

        return ( returnVal );
    }

    // Gets PlaceDetails from Google Places passing Places reference
    public PlaceDetail findPlaceDetail( String placeRef ) {

        PlaceDetail placeDetail = null;
        String urlString = GetPlaceDetailUrl( placeRef );

        try {
            
            String json = getJSON( urlString );
            JSONObject object = new JSONObject( json );
            JSONObject result = object.getJSONObject( "result" );

            placeDetail = PlaceDetail.jsonToPlaceDetail( result );

        } catch ( JSONException ex ) {
            ex.printStackTrace();
        }
        return placeDetail;
    }

    // Gets Places Photo from Google Photos passing photo reference
    public PlacePhoto findPlacePhoto( String photoReference ) {

        PlacePhoto placePhoto = new PlacePhoto();
        String urlString = GetPlaceDetailPhotoUrl( photoReference );

        try {

            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI( new URI( urlString ) );
            
            HttpResponse response = client.execute( request );
            Bitmap photo = BitmapFactory.decodeStream( response.getEntity().getContent() );

            placePhoto.photo = photo;

        } catch ( Exception ex ) {
        	
            ex.printStackTrace();
        }
        
        return placePhoto;
    }

}
