/* MapActivity.java
 * Project E - Eric Daniels
 */

package com.android.projecte.townportal;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TextView;

@SuppressWarnings ( "deprecation")
/*
 * Map Activity
 * Description: Used with Google Maps activity page to display tabs which are
 *   			sub-categories of a user selected category.
 */
public class MapActivity extends TabActivity {

    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );
        
        // http://stackoverflow.com/questions/2736389/how-to-pass-object-from-one-activity-to-another-in-android
        Intent intent = getIntent();
        String title = ( String ) intent.getSerializableExtra( "title" );
        PlaceType pt1 = (PlaceType) intent.getSerializableExtra( "PlaceType1" );
        PlaceType pt2 = (PlaceType) intent.getSerializableExtra( "PlaceType2" );
        PlaceType pt3 = (PlaceType) intent.getSerializableExtra( "PlaceType3" );

        // Use custom title bar
        requestWindowFeature( Window.FEATURE_CUSTOM_TITLE );
        setContentView( R.layout.activity_map_tabs );
        getWindow().setFeatureInt( Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title );
        ((TextView) findViewById( R.id.title ) ).setText( title );

        // Set up TabHost
        TabHost tabHost = (TabHost) findViewById( android.R.id.tabhost );
        tabHost.setup( this.getLocalActivityManager() );

        TabHost.TabSpec spec; // Reusable TabSpec for each tab
        Intent tabIntent; // Reusable Intent for each tab

        // Tab 1 - first tab is required, others may be null
        tabIntent = new Intent().setClass( this, GooglePlacesMap.class ).putExtra( "type", pt1.googleName );
        spec = tabHost.newTabSpec( pt1.displayName ).setIndicator( pt1.displayName ).setContent( tabIntent );
        tabHost.addTab( spec );

        // Tab 2
        if ( pt2 != null ) {
        	
            tabIntent = new Intent().setClass( this, GooglePlacesMap.class ).putExtra( "type", pt2.googleName );
            spec = tabHost.newTabSpec( pt2.displayName ).setIndicator( pt2.displayName ).setContent( tabIntent );
            tabHost.addTab( spec );
        }

        // Tab 3
        if ( pt3 != null ) {
        	
            tabIntent = new Intent().setClass( this, GooglePlacesMap.class ).putExtra( "type", pt3.googleName );
            spec = tabHost.newTabSpec( pt3.displayName ).setIndicator( pt3.displayName ).setContent( tabIntent );
            tabHost.addTab( spec );
        }

        tabHost.setCurrentTab( 0 );

        // loop through all tab views and set height value
        // http://www.speakingcode.com/2011/10/17/adjust-height-of-android-tabwidget/
        int heightValue = 30;
        for ( int i = 0; i < tabHost.getTabWidget().getTabCount(); i++ )
            tabHost.getTabWidget().getChildAt( i ).getLayoutParams().height = 
            	(int) ( heightValue * this.getResources().getDisplayMetrics().density );

    }
}
