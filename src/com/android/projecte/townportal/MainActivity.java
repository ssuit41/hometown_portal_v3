/* MainActivity.java
 * Project E - Eric Daniels
 */

package com.android.projecte.townportal;

import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

/*
 * Main Activity
 * Description: Main area for user to select different activities in this
 *              application.
 */
public class MainActivity extends Activity {

    // Used for constructing types of places to views
    private Vector<PlaceType> vFood = new Vector<PlaceType>(),
                              vEnt = new Vector<PlaceType>(),
                              vShop = new Vector<PlaceType>(),
                              vSchool = new Vector<PlaceType>(),
    						  vHotel = new Vector<PlaceType>(),
    						  vHealth = new Vector<PlaceType>(),
    						  vGovernment = new Vector<PlaceType>(),
    						  vTransportation = new Vector<PlaceType>(),
    						  vReligion = new Vector<PlaceType>(),
    						  vEmergency = new Vector<PlaceType>();
    
    private String foodTitle, entertainmentTitle, shoppingTitle, schoolsTitle, hotelTitle, governmentTitle, 
    				healthTitle, transportationTitle, religionTitle, emergencyTitle;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );

        // Use custom title bar
        requestWindowFeature( Window.FEATURE_CUSTOM_TITLE );
        setContentView( R.layout.activity_main );
        getWindow().setFeatureInt( Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title );
        
        // Get titles
        this.foodTitle = getString( R.string.food_text );
        this.entertainmentTitle = getString( R.string.entertainment_text );
        this.shoppingTitle = getString( R.string.shopping_text );
        this.schoolsTitle = getString( R.string.schools_text );
        this.hotelTitle = getString( R.string.lodging_text);
        this.healthTitle = getString( R.string.health_text);
        this.transportationTitle = getString( R.string.transportation_text);
        this.religionTitle = getString( R.string.religion_text);
        this.emergencyTitle = getString(R.string.emergency_text);

        // Setup food
        this.vFood.add( new PlaceType( "cafe", "Cafes" ) );
        this.vFood.add( new PlaceType( "restaurant", "Restaurants" ) );
        this.vFood.add( new PlaceType( "grocery_or_supermarket", "Markets" ) );

        // Setup Entertainment
        this.vEnt.add( new PlaceType( "movie_theater", "Movies" ) );
        this.vEnt.add( new PlaceType( "night_club", "Night Clubs" ) );
        this.vEnt.add( new PlaceType( "museum", "Museums" ) );

        // Setup Shopping
        this.vShop.add( new PlaceType( "shopping_mall", "Malls" ) );
        this.vShop.add( new PlaceType( "book_store", "Books" ) );
        this.vShop.add( new PlaceType( "electronics_store", "Electronics" ) );

        // Setup Schools
        this.vSchool.add( new PlaceType( "school", "Schools" ) );
        this.vSchool.add( new PlaceType( "university", "Universities" ) );
        
        // Setup hotels
        this.vHotel.add(  new PlaceType( "lodging", "Hotels") );
        
        //Setup Health places
        this.vHealth.add( new PlaceType( "hospital", "Hospitals"));
        this.vHealth.add( new PlaceType( "doctor", "Doctors"));
        this.vHealth.add( new PlaceType( "pharmacy", "Pharmacies"));
        
        //Setup Government
        this.vGovernment.add( new PlaceType( "city_hall", "City Hall"));
        this.vGovernment.add( new PlaceType( "courthouse", "Court Houses"));
        
        //Setup Transportation
        this.vTransportation.add(new PlaceType( "airport", "Airports"));
        this.vTransportation.add(new PlaceType( "bus_station", "Bus Stations"));
        this.vTransportation.add(new PlaceType( "subway_station", "Subway Stations"));

        //Setup Religion 
        this.vReligion.add(new PlaceType( "church", "Churches"));
        this.vReligion.add(new PlaceType( "synagogue", "Synagogues"));
        this.vReligion.add(new PlaceType( "mosque", "Mosques"));
        
        //Setup Emergency 
        this.vEmergency.add(new PlaceType( "police", "Police Stations"));
        this.vEmergency.add(new PlaceType( "fire_station", "Fire Stations"));
    }

    /*
     * Button On Click Listener
     * Description: Listens for any of the buttons being clicked
     *              and launches their respective activity
     */
    public void onClick( View v ) {

        switch ( v.getId() ) {
        
        case R.id.btnFood: {
            
            openPlaceList( this.foodTitle, this.vFood );
            
            break;
        }
            
        case R.id.btnEntertainment: {
            
            openPlaceList( this.entertainmentTitle, this.vEnt );
            
            break;
        }
        
        case R.id.btnShopping: {
            
            openPlaceList( this.shoppingTitle, this.vShop );
            
            break; 
        }

        case R.id.btnSchools: {
            
            openPlaceList( this.schoolsTitle, this.vSchool );
            
            break; 
        }
        
        case R.id.btnHotels:{
	
        	openPlaceList( this.hotelTitle, this.vHotel);
        	
        	break;
        }
        
        case R.id.btnEmergency:{
        	
        	openPlaceList( this.emergencyTitle, this.vEmergency);
        	
        	break;
        }
        
        case R.id.btnGovernment:{
        	
        	openPlaceList( this.governmentTitle, this.vGovernment);
        	
        	break;
        	
        }
        
        case R.id.btnHealth:{
        	
        	openPlaceList( this.healthTitle, this.vHealth);
        	
        	break;
        }
        
        case R.id.btnReligion:{
        	
        	openPlaceList( this.religionTitle, this.vReligion);
        	
        	break;
        }
        
        case R.id.btnTransporation:{
        	
        	openPlaceList( this.transportationTitle, this.vTransportation);
        	
        	break;
        }
        case R.id.btnEmployment: {
            
            Intent employmentIntent = new Intent( this, EmploymentActivity.class );
            startActivity( employmentIntent );
            
            break; 
        }
        
        case R.id.btnNews:{
            
            Intent newsIntent = new Intent( this, NewsActivity.class );
            startActivity( newsIntent );
            
            break;
        }
     
        default:
            break;
        }
    }

    /*
     * Open Place List
     * Description: Start a MapActivity based off certain place info.
     */
    private void openPlaceList( String title, Vector<PlaceType> places ) {

        Intent intent = new Intent( this, MapActivity.class );
        intent.putExtra( "title", title );
        
        for ( int i = 0; i < places.size(); i++ )
            intent.putExtra( "PlaceType" + Integer.toString( i + 1 ), places.get( i ) );
        
        startActivity( intent );
    }
}
