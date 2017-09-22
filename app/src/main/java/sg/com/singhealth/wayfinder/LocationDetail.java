package sg.com.singhealth.wayfinder;

/**
 * File Name: LocationDetail.java
 * Created By: AY17 P3 FYPJ NYP SIT
 * Description: -
 */

public class LocationDetail {
    String id;
    double latitude, longitude;


    public LocationDetail(String id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
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
