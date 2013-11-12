/* Place.java
 * Project E - Eric Daniels
 * Class used to hold GooglePlace data 
 */

package com.android.projecte.townportal;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

public class Place {

    private String id;
    private String icon;
    private String name;
    private String vicinity;
    private Double latitude;
    private Double longitude;
    private String placeReference;
    private Double rating;
    private int price;

    public String getId() {

        return id;
    }

    public void setId( String id ) {

        this.id = id;
    }

    public String getIcon() {

        return ( icon );
    }

    public void setIcon( String icon ) {

        this.icon = icon;
    }

    public Double getLatitude() {

        return latitude;
    }

    public void setLatitude( Double latitude ) {

        this.latitude = latitude;
    }

    public Double getLongitude() {

        return longitude;
    }

    public void setLongitude( Double longitude ) {

        this.longitude = longitude;
    }

    public String getName() {

        return name;
    }

    public void setName( String name ) {

        this.name = name;
    }

    public String getVicinity() {

        return vicinity;
    }

    public void setVicinity( String vicinity ) {

        this.vicinity = vicinity;
    }
    
    public double getRating() {
        
        return this.rating;
    }
    
    public void setRating( double rating ) {

        this.rating = rating;
    }
    
    public void setPrice( int price ) {

        this.price = price;
    }

    public int getPrice() {

        return this.price;
    }
    
    public String getPlaceReference() {

        return placeReference;
    }

    public void setPlaceReference( String placeReference ) {

        this.placeReference = placeReference;
    }
    
    static Place jsonToPlace( JSONObject toPlace ) {

        try {

            Place result = new Place();
            JSONObject geometry = (JSONObject) toPlace.get( "geometry" );
            JSONObject location = (JSONObject) geometry.get( "location" );
            result.setLatitude( (Double) location.get( "lat" ) );
            result.setLongitude( (Double) location.get( "lng" ) );
            result.setIcon( toPlace.getString( "icon" ) );
            result.setName( toPlace.getString( "name" ) );
            result.setVicinity( toPlace.getString( "vicinity" ) );
            result.setId( toPlace.getString( "id" ) );
            result.setPlaceReference( toPlace.getString( "reference" ) );
            
            try {
                
                result.setRating( toPlace.getDouble( "rating" ) );
            
            } catch ( JSONException ex ) {
                
                result.setRating( 0 );
            }
            
            try {
                
                result.setPrice( toPlace.getInt( "price_level" ) );
            
            } catch ( JSONException ex ) {
                
                result.setPrice( 0 );
            }

            
            return result;

        } catch ( JSONException ex ) {
            
            Logger.getLogger( Place.class.getName() ).log( Level.SEVERE, null, ex );
        }
        return null;
    }

    @Override
    public String toString() {
        
        return this.name;
    }
}