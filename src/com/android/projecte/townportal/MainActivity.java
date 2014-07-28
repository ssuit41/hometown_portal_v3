/* MainActivity.java
 * Project E - Eric Daniels
 */

package com.android.projecte.townportal;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Vector;
import com.android.projecte.townportal.rss.FeedListActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
//Facebook implementation code from developers.Facebook.com following official 
//Facebook developer instructions

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
                              //added from Nigel
                              		vHotel = new Vector<PlaceType>(),
                              		vHealth = new Vector<PlaceType>(),
                              		vGovernment = new Vector<PlaceType>(),
                              		vTransportation = new Vector<PlaceType>(),
                              		vReligion = new Vector<PlaceType>(),
                              		vEmergency = new Vector<PlaceType>();
    
    private String foodTitle, entertainmentTitle, shoppingTitle, schoolsTitle, 
    hotelTitle, governmentTitle, healthTitle, transportationTitle, religionTitle, emergencyTitle;  // added from Nigel
    private String city, state;
    
    private UiLifecycleHelper uiHelper;  //facebook implement


    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );
      //Custom Title Bar
        requestWindowFeature( Window.FEATURE_CUSTOM_TITLE );
        setContentView( R.layout.activity_main );
        getWindow().setFeatureInt( Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title );
        
        
        //code for generating a key hash in logcat window, tried to use this in fixing facebook issue
        /*try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.android.projecte.townportal", 
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                }
        } catch (NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }*/
        
        
        
        // Get titles
        this.foodTitle = getString( R.string.food_text );
        this.entertainmentTitle = getString( R.string.entertainment_text );
        this.shoppingTitle = getString( R.string.shopping_text );
        this.schoolsTitle = getString( R.string.schools_text );
        
        this.hotelTitle = getString( R.string.lodging_text);   //added from Nigel
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
        
        //Setup facebook
        uiHelper = new UiLifecycleHelper(this, null);  //facebook
        uiHelper.onCreate(savedInstanceState);         //facebook

        
        popLocation();
    }
    
    //facebook start
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
            @Override
            public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
                Log.e("Activity", String.format("Error: %s", error.toString()));
            }

            @Override
            public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
                Log.i("Activity", "Success!");
            }
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }//facebook end */ 
    
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
            openRSS("News");
            break;
        }
        
        case R.id.btnHotels:{   //added from Nigel
        	openPlaceList( this.hotelTitle, this.vHotel);
        	break;
        }
        
        case R.id.btnEmergency:{   //added from Nigel
        	openPlaceList( this.emergencyTitle, this.vEmergency);
        	break;
        }
        
        case R.id.btnGovernment:{   //added from Nigel
        	openPlaceList( this.governmentTitle, this.vGovernment);
        	break;
        }
        
        case R.id.btnHealth:{   //added from Nigel
        	openPlaceList( this.healthTitle, this.vHealth);
        	break;
        }
        
        case R.id.btnReligion:{   //added from Nigel
        	openPlaceList( this.religionTitle, this.vReligion);
        	break;
        }
        
        case R.id.btnTransporation:{   //added from Nigel
        	openPlaceList( this.transportationTitle, this.vTransportation);
        	break;
        }
        
        case R.id.btnFacebook: {   //implementing facebook action
            
        	 FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)  
             .setLink("https://play.google.com/store/apps/details?id=com.app_cocoabeach.layout&hl=en")
             .build();  //facebook
             uiHelper.trackPendingDialogCall(shareDialog.present());//facebook
             
             
            
            break; 
        }
        
        case R.id.btnSports:{   //re-added from Shawn
        	openRSS("Sports");
        	break;
        }
        
        
        case R.id.btnWeather:{   //re-added from Shawn
        	openRSS("Weather");
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
        intent.putExtra("city", city);
    	intent.putExtra("state", state);
        
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
    
    /*
     * Starts the FeedListActivity by passing in the correct search term to generate the feed
     */
    
    private void openRSS (String feedType)
    {
    	Intent intent = new Intent(this, FeedListActivity.class);
    	intent.putExtra("feedType", feedType);
    	intent.putExtra("city", city);
    	intent.putExtra("state", state);
    	startActivity(intent);
    }
}
