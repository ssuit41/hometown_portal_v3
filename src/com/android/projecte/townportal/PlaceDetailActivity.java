/* PlaceDetailActivity.java
 * Project E - Eric Daniels
 * Used with Place Detail activity page to display detailed information
 *    when user clicks on Place from MapActivity page
 */

package com.android.projecte.townportal;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class PlaceDetailActivity extends Activity {

    TextView nameTextView, addressTextView, phoneNumberTextView, websiteTextView;
    ImageView photoImageView;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );

        // Use custom title bar
        requestWindowFeature( Window.FEATURE_CUSTOM_TITLE );
        setContentView( R.layout.activity_main );
        getWindow().setFeatureInt( Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title );
        setContentView( R.layout.activity_place_detail );

        // Set Place detail TextViews
        nameTextView = (TextView) findViewById( R.id.nameText );
        addressTextView = (TextView) findViewById( R.id.addressText );
        phoneNumberTextView = (TextView) findViewById( R.id.phoneNumberText );
        websiteTextView = (TextView) findViewById( R.id.websiteText );
        photoImageView = (ImageView) findViewById( R.id.photoImage );

        // Set Place detail variables
        String mName = getIntent().getExtras().getString( "name" );
        String mAddress = getIntent().getExtras().getString( "address" );
        String mPhoneNumber = getIntent().getExtras().getString( "phonenumber" );
        String mWebsite = getIntent().getExtras().getString( "website" );
        Bitmap mPhoto = getIntent().getParcelableExtra( "photo" );

        // Set TextViews
        nameTextView.setText( mName );
        addressTextView.setText( mAddress );
        phoneNumberTextView.setText( mPhoneNumber );
        websiteTextView.setClickable( true );
        websiteTextView.setMovementMethod( LinkMovementMethod.getInstance() );

        if ( mWebsite != null ) {
            websiteTextView.setText( Html.fromHtml( "<a href=" + mWebsite + ">" + mWebsite ) );
        }

        // Set Photo ImageView
        if ( null != mPhoto ) {
            photoImageView.setImageBitmap( mPhoto );
        }

    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.place_detail, menu );
        return true;
    }

}
