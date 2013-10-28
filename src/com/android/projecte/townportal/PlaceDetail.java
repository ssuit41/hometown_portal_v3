/* PlaceDetail.java
 * Electric Sheep - K.Hall, C.Munoz, A.Reaves
 * Class used to hold GooglePlace Detail data 
 */

package com.android.projecte.townportal;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;

public class PlaceDetail {

    private String phoneNumber = null;
    private String address = null;
    private String website = null;
    private String photoRef = null;
    private String siteName = null;
    private Bitmap sitePhoto = null;

    public String getPhoneNumber() {

        return phoneNumber;
    }

    public void setPhoneNumber( String phoneNumber ) {

        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {

        return address;
    }

    public void setAddress( String address ) {

        this.address = address;
    }

    public String getWebsite() {

        return website;
    }

    public void setWebsite( String website ) {

        this.website = website;
    }

    public String getPhotoRef() {

        return photoRef;
    }

    public void setPhotoRef( String photoRef ) {

        this.photoRef = photoRef;
    }

    public String getSiteName() {

        return siteName;
    }

    public void setSiteName( String siteName ) {

        this.siteName = siteName;
    }

    static PlaceDetail jsonToPlaceDetail( JSONObject result ) {

        PlaceDetail placeDetail = null;

        try {
            placeDetail = new PlaceDetail();

            if ( !( result.isNull( "photos" ) ) ) {
                JSONArray photos = result.getJSONArray( "photos" );
                JSONObject photo = photos.getJSONObject( 0 );
                placeDetail.setPhotoRef( photo.getString( "photo_reference" ) );
            }

            if ( !( result.isNull( "formatted_phone_number" ) ) ) {
                String phoneNumber = result
                        .getString( "formatted_phone_number" );
                placeDetail.setPhoneNumber( phoneNumber );
            }

            if ( !( result.isNull( "formatted_address" ) ) ) {
                String address = result.getString( "formatted_address" );
                placeDetail.setAddress( address );
            }

            if ( !( result.isNull( "website" ) ) ) {
                String website = result.getString( "website" );
                placeDetail.setWebsite( website );
            }

            if ( !( result.isNull( "name" ) ) ) {
                String siteName = result.getString( "name" );
                placeDetail.setSiteName( siteName );
            }

        } catch ( JSONException ex ) {
            Logger.getLogger( Place.class.getName() ).log( Level.SEVERE, null,
                    ex );
        }
        return placeDetail;
    }

    public Bitmap getSitePhoto() {

        return sitePhoto;
    }

    public void setSitePhoto( Bitmap sitePhoto ) {

        this.sitePhoto = sitePhoto;
    }

}
