/* PlaceType.java
 * Electric Sheep - K.Hall, C.Munoz, A.Reaves
 * Class used to hold GooglePlace type data 
 */

package com.android.projecte.townportal;

import java.io.Serializable;

public class PlaceType implements Serializable {

    private static final long serialVersionUID = 1L;
    private String googleName;
    private String displayName;

    public PlaceType() {

    }

    public PlaceType(String _googleName, String _displayName) {

        googleName = _googleName;
        displayName = _displayName;
    }

    public String getGoogleName() {

        return googleName;
    }

    public String getDisplayName() {

        return displayName;
    }
}
