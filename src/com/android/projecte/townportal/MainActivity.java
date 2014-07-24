/* MainActivity.java
 * Project E - Eric Daniels
 */

package com.android.projecte.townportal;

import java.util.Vector;
import com.android.projecte.townportal.rss.FeedListActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
                              vSchool = new Vector<PlaceType>();
    
    private String foodTitle, entertainmentTitle, shoppingTitle, schoolsTitle;
    private String city, state;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );
      //Custom Title Bar
        requestWindowFeature( Window.FEATURE_CUSTOM_TITLE );
        setContentView( R.layout.activity_main );
        getWindow().setFeatureInt( Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title );
        
        
        
        // Get titles
        this.foodTitle = getString( R.string.food_text );
        this.entertainmentTitle = getString( R.string.entertainment_text );
        this.shoppingTitle = getString( R.string.shopping_text );
        this.schoolsTitle = getString( R.string.schools_text );

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
        
        popLocation();
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainscreen, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		 switch (item.getItemId()) {
	        case R.id.location:
	        	popLocation();	        	
	        default:
	            return super.onOptionsItemSelected(item);
	    }
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
        
        case R.id.btnEmployment: {
            
            Intent employmentIntent = new Intent( this, FeedListActivity.class );
            employmentIntent.putExtra("feedType", "employment");
            employmentIntent.putExtra("simplyHiredURL", "http://www.simplyhired.com/a/job-feed/rss/q-" + city + "," + state);
            employmentIntent.putExtra("indeedURL", "http://rss.indeed.com/rss?q=" + city + "," + state);
            startActivity( employmentIntent );
            
            break; 
        }
        
        case R.id.btnNews:{
            
            Intent newsIntent = new Intent( this, FeedListActivity.class );
            newsIntent.putExtra("feedType", "News");
            newsIntent.putExtra("city", city);
            newsIntent.putExtra("state", state);
            startActivity( newsIntent );
            
            break;
        }
        /*
        case R.id.btnSports:{
        	Intent sportsIntent = new Intent(this, FeedListActivity.class);
        	sportsIntent.putExtra("feedType", "Sports");
        	sportsIntent.putExtra("city", city);
        	sportsIntent.putExtra("state", state);
        	startActivity( sportsIntent);
        }
        */
        /*
        case R.id.btnWeather:{
        	Intent weatherIntent = new Intent(this, FeedListActivity.class);
        	weatherIntent.putExtra("feedType", "%20Weather");
        	weatherIntent.putExtra("city", city + ",");
        	weatherIntent.putExtra("state", "%20" + state);
        	startActivity( weatherIntent);
        }
        */
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
    
    private void popLocation ()
    {
    	LayoutInflater inflater = getLayoutInflater();
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	alert.setTitle("Please insert City and State");
    	View view = inflater.inflate(R.layout.locationprompt, null);
    	alert.setView(view);
    	final EditText cityPrompt = (EditText) view.findViewById(R.id.city);
    	final EditText statePrompt = (EditText) view.findViewById(R.id.state);
    	
    	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				if(cityPrompt.getText().toString() == null || statePrompt.getText().toString() == null)
				{
					Toast.makeText(getApplicationContext(), "Invalid City,  State", Toast.LENGTH_SHORT).show();
					popLocation();
					
				}else
				{
					city = cityPrompt.getText().toString();
					state = statePrompt.getText().toString();
					((TextView) findViewById( R.id.title ) ).setText( "Welcome to "+ city + "," + state );
				
				}
				
			}
    	});
    	
    	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    		@Override
			public void onClick(DialogInterface dialog, int which) {}
		});
    	alert.show();
    	
    }
}
