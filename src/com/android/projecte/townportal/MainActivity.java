/* MainActivity.java
 * Project E - Eric Daniels
 * Main navigation page of HomeTown Portal application which displays 
 *   categories for user to select
 */

package com.android.projecte.townportal;

import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

public class MainActivity extends Activity implements View.OnClickListener {

    ImageButton btnFood, btnEnt, btnShop, btnEmploy, btnSchool, btnNews;

    Vector<PlaceType> vFood = new Vector<PlaceType>();
    Vector<PlaceType> vEnt = new Vector<PlaceType>();
    Vector<PlaceType> vShop = new Vector<PlaceType>();
    Vector<PlaceType> vSchool = new Vector<PlaceType>();

    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );

        // Use custom title bar
        requestWindowFeature( Window.FEATURE_CUSTOM_TITLE );
        setContentView( R.layout.activity_main );
        getWindow().setFeatureInt( Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title );

        // Set up image buttons for each category with click listener
        // and add sub-categories to main category arrays if applicable
        // FOOD //
        btnFood = (ImageButton) findViewById( R.id.btnFood );
        btnFood.setOnClickListener( this );
        vFood.add( new PlaceType( "cafe", "Cafes" ) );
        vFood.add( new PlaceType( "restaurant", "Restaurants" ) );
        vFood.add( new PlaceType( "grocery_or_supermarket", "Markets" ) );

        // ENTERTAINMENT //
        btnEnt = (ImageButton) findViewById( R.id.btnEntertainment );
        btnEnt.setOnClickListener( this );
        vEnt.add( new PlaceType( "movie_theater", "Movies" ) );
        vEnt.add( new PlaceType( "night_club", "Night Clubs" ) );
        vEnt.add( new PlaceType( "museum", "Museums" ) );

        // SHOPPING //
        btnShop = (ImageButton) findViewById( R.id.btnShopping );
        btnShop.setOnClickListener( this );
        vShop.add( new PlaceType( "shopping_mall", "Malls" ) );
        vShop.add( new PlaceType( "book_store", "Books" ) );
        vShop.add( new PlaceType( "electronics_store", "Electronics" ) );

        // SCHOOLS //
        btnSchool = (ImageButton) findViewById( R.id.btnSchools );
        btnSchool.setOnClickListener( this );
        vSchool.add( new PlaceType( "school", "Schools" ) );
        vSchool.add( new PlaceType( "university", "Universities" ) );

        // EMPLOYMENT //
        btnEmploy = (ImageButton) findViewById( R.id.btnEmployment );
        btnEmploy.setOnClickListener( this );

        // NEWS //
        btnNews = (ImageButton) findViewById( R.id.btnNews );
        btnNews.setOnClickListener( this );
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.main, menu );
        return true;
    }

    @Override
    public void onClick( View v ) {

        switch ( v.getId() ) {
        
        case R.id.btnFood: {
            
            openPlaceList( getString(R.string.food_text), 
                    vFood.get( 0 ), vFood.get( 1 ), vFood.get( 2 ) );
            break;
        }
            
        case R.id.btnEntertainment: {
            
            openPlaceList( getString(R.string.entertainment_text),
                    vEnt.get( 0 ), vEnt.get( 1 ), vEnt.get( 2 ) );
            break;
        }
        
        case R.id.btnShopping: {
            
            openPlaceList( getString(R.string.shopping_text),
                    vShop.get( 0 ), vShop.get( 1 ), vShop.get( 2 ) );
            break; 
        }

        case R.id.btnSchools: {
            
            openPlaceList( getString(R.string.schools_text),
                    vSchool.get( 0 ), vSchool.get( 1 ), null );
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

    private void openPlaceList( String title, PlaceType type1, PlaceType type2,
            PlaceType type3 ) {

        Intent intent = new Intent( this, MapActivity.class );
        intent.putExtra( "title", title );
        intent.putExtra( "PlaceType1", type1 );
        intent.putExtra( "PlaceType2", type2 );
        intent.putExtra( "PlaceType3", type3 );
        startActivity( intent );
    }
}
