package sg.com.singhealth.wayfinder;

/**
 * File Name: LocationDetail.java
 * Created By: AY17 P3 FYPJ NYP SIT
 * Description: -
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
