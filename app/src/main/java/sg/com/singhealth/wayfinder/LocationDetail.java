package sg.com.singhealth.wayfinder;

/**
 * Created by L31106 on 9/18/2017.
 */

public class LocationDetail {
    String id, locationName;
    double latitude, longitude;


    public LocationDetail(String id, String locationName, double latitude, double longitude) {
        this.id = id;
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

}
