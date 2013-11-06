/* PlacePhoto.java
 * Project E - Eric Daniels
 * Class used to hold GooglePlace Photo data 
 */

package com.android.projecte.townportal;

import android.graphics.Bitmap;

public class PlacePhoto {

    private String photoReference;
    private Bitmap photo;

    public String getPhotoReference() {

        return photoReference;
    }

    public void setPhotoReference( String photoReference ) {

        this.photoReference = photoReference;
    }

    public Bitmap getPhoto() {

        return photo;
    }

    public void setPhoto( Bitmap photo ) {

        this.photo = photo;
    }

}
