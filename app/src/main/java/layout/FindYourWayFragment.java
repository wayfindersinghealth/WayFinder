package layout;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonParser;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerView;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import sg.com.singhealth.wayfinder.LocTracker;
import sg.com.singhealth.wayfinder.LocationDetail;
import sg.com.singhealth.wayfinder.MainActivity;
import sg.com.singhealth.wayfinder.R;

/**
 * File Name: FindYourWayFragment.java
 * Created By: AY17 P3 FYPJ NYP SIT
 * Description: Before Starting Application
 *              Change at Line 449, 450
 *              Delete Firebase Data.
 */

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FindYourWayFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FindYourWayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FindYourWayFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    LatLng currentLocation = null;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    MapView mapView;
    Timer t = null;

    private static MarkerView markerView = null;
    MarkerView locMarker = null;
    WifiManager wmgr;
    AutoCompleteTextView autoCompleteTextViewTo;
    ArrayList<String> aList = new ArrayList<String>();



    DatabaseReference databaseLocation;

    private OnFragmentInteractionListener mListener;

    public FindYourWayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FindYourWayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FindYourWayFragment newInstance(String param1, String param2) {
        FindYourWayFragment fragment = new FindYourWayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //-- MapBox Access Token --
        Mapbox.getInstance(getActivity(), getString(R.string.access_token));

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //-- Change Action Bar Title --
        ((MainActivity) getActivity()).setActionBarTitle("Find Your Way");

        //-- View --
        final View rootView = inflater.inflate(R.layout.fragment_find_your_way, container, false);

        //-- Connecting to DB --
        databaseLocation = FirebaseDatabase.getInstance().getReference("locations");

        //-- Location AutoCompleteTextView --
        //https://gist.github.com/ruuhkis/d942330d97163d868ee7
        autoCompleteTextViewTo = (AutoCompleteTextView) rootView.findViewById(R.id.autoCompleteTextViewTo);

        //-- Get From DB --
        databaseLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {
                    String locName = locationSnapshot.child("id").getValue().toString();
                    Log.d("locName", locName);
                    aList.add(locName);
                    Log.d("Alist", aList + " ");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //-- AutoCompleteTextView --
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                aList);

        autoCompleteTextViewTo.setAdapter(arrayAdapter);
        autoCompleteTextViewTo.setThreshold(0);

        //-- WifiManager --
        wmgr = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        //-- MapBox MapView --
        mapView = (MapView) rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {

                //-- Customize map with markers, polylines, etc. --
                //-- MapBox URL --
                mapboxMap.setStyleUrl(getString((R.string.mapbox_url)));

                //-- MapBox Zoom On Location --
                final LatLng zoomLocation = new LatLng(1.3792949602146791, 103.84983998176449);
                CameraPosition position = new CameraPosition.Builder()
                        .target(zoomLocation)
                        .zoom(19.8) // Sets the zoom
                        .build(); // Creates a CameraPosition from the builder
                mapboxMap.setCameraPosition(position);

                //-- Timer to Loops Marker Change ---
                t = new Timer();
                t.scheduleAtFixedRate(new TimerTask() {
                    int times = 9;
                    boolean truth;

                    @Override
                    public void run() {
                        ArrayList<LocTracker> locationArray = new ArrayList<>();

                        try {
                            for (int t = 0; t <times; t++) {
                                String locations = new PostTrackAPI().execute("https://ml.internalpositioning.com/track").get();
                                Log.d("Loc Name", locations);
                                if(locationArray.size() == 0 ){
                                    LocTracker thisLoc = new LocTracker();
                                    thisLoc.setCounter(1);
                                    thisLoc.setLocationName(locations);
                                    locationArray.add(thisLoc);

                                }else{
                                    for(int i=0; i<locationArray.size(); i++){
                                        if(locationArray.get(i).getLocationName().equalsIgnoreCase(locations)){
                                            locationArray.get(i).setCounter(locationArray.get(i).getCounter() + 1);
                                            truth = true;
                                            break;
                                        }else{
                                            truth = false;
                                        }
                                    }

                                if(!truth){
                                    LocTracker location = new LocTracker();
                                    location.setLocationName(locations);
                                    location.setCounter(1);
                                    locationArray.add(location);
                                }
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                    //-- Compare to Database --

                    for(int i=0; i<locationArray.size(); i++){
                        Log.d("Location Detail", locationArray.get(i).getLocationName() + "," + locationArray.get(i).getCounter());
                    }




                            /*String finalLocation = thisLoc.toString().toUpperCase();
                            Query locationQuery = databaseLocation.orderByChild("id").equalTo(finalLocation);
                            locationQuery.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {
                                        //-- Get Longitude and Latitude --
                                        double locLatitude = (double) locationSnapshot.child("latitude").getValue();
                                        double locLongitude = (double) locationSnapshot.child("longitude").getValue();

                                        currentLocation = new LatLng(locLatitude, locLongitude);

                                        Log.d("LatLng", locLatitude + ", " + locLongitude);

                                        //-- Marker Icon --
                                        IconFactory iconFactory = IconFactory.getInstance(getActivity());
                                        Icon icon = iconFactory.fromResource(R.drawable.ic_curr_location);

                                        //-- Set Marker on Map --
                                        LatLng latLng = new LatLng(locLatitude, locLongitude);
                                        if (markerView != null) {
                                            markerView.setPosition(latLng);
                                            markerView.setIcon(icon);
                                            markerView.setTitle("You Are Here");

                                        } else if (markerView == null){
                                            markerView = mapboxMap.addMarker(new MarkerViewOptions().position(new LatLng(locLatitude, locLongitude)));
                                            markerView.setIcon(icon);
                                            markerView.setTitle("You Are Here");
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            */


                    }
                },0,5000);

                //-- Floating Action Button Click to go to Current Location--
                FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       if (currentLocation == null) {
                           Toast.makeText(getActivity(), "Locating Your Position...." , Toast.LENGTH_SHORT).show();
                       } else {
                           LatLng zoomLocation = new LatLng(currentLocation);
                           CameraPosition position = new CameraPosition.Builder()
                                   .target(zoomLocation)
                                   .zoom(20) // Sets the zoom
                                   .build(); // Creates a CameraPosition from the builder
                           mapboxMap.setCameraPosition(position);
                        }
                    }
                });

                //-- AutoCompleteTextViewTo OnCLick --
                autoCompleteTextViewTo.setOnItemClickListener(new AdapterView.OnItemClickListener() {


                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String selectedItem = (String) adapterView.getItemAtPosition(i);
                        Log.d("Selected Item", selectedItem);

                        Query locationQuery = databaseLocation.orderByChild("id").equalTo(selectedItem);
                        locationQuery.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {

                                    //-- Get Longitude and Latitude ---
                                    double locLatitude = (double) locationSnapshot.child("latitude").getValue();
                                    double locLongitude = (double) locationSnapshot.child("longitude").getValue();

                                    currentLocation = new LatLng(locLatitude, locLongitude);

                                    Log.d("LatLng", locLatitude + ", " + locLongitude);

                                    //-- Marker Icon --
                                    IconFactory iconFactory = IconFactory.getInstance(getActivity());
                                    Icon icon = iconFactory.fromResource(R.drawable.ic_destination_flag);

                                    //-- Set Marker on Map --
                                    LatLng latLng = new LatLng(locLatitude, locLongitude);
                                    if (locMarker != null) {
                                        locMarker.setPosition(latLng);
                                        locMarker.setIcon(icon);

                                    } else if(locMarker == null){
                                        locMarker = mapboxMap.addMarker(new MarkerViewOptions().position(new LatLng(locLatitude, locLongitude)));
                                        locMarker.setIcon(icon);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        });

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    //-------- START OF METHODS --------

    //---- MapBox onStart Method ----
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    //---- MapBox onResume Method ----
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    //---- MapBox onPause Method ----
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    //---- MapBox onStop Method ----
    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
        if(t != null){
            t.cancel();
            t = null;
            markerView = null;
        }
    }

    //---- MapBox onSaveInstanceState Method ----
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    //---- MapBox onLowMemory Method ----
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    //---- MapBox onDestory Method ----
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    //---- Format Data As JSON Method ----
    private String formatDataAsJSON() {
        JSONObject root = new JSONObject();
        JSONArray wifiFingerprint = new JSONArray();
        JSONObject fingerprint = new JSONObject();

        List<ScanResult> results = wmgr.getScanResults();
        String timeStamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + "";
        wmgr.startScan();

        for (ScanResult R : results) {
            if (R.SSID.equalsIgnoreCase("NYP-Student")) {
                try {
                    fingerprint.put("mac", R.BSSID.toString());
                    fingerprint.put("rssi", R.level);
                    wifiFingerprint.put(fingerprint);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            root.put("group", "interim01");
            root.put("username", "p3");
            root.put("time", timeStamp);
            root.put("wifi-fingerprint", wifiFingerprint);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return root.toString();
    }

    //-------- END OF METHODS --------

    //-------- START OF CLASS --------

    //---- PostTrackAPI Task Class ----
    public class PostTrackAPI extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpsURLConnection connection = null;
            BufferedReader reader = null;
            BufferedWriter writer = null;
            String result;

            try{
                //Connecting to API
                URL link = new URL(params[0]);
                connection = (HttpsURLConnection) link.openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestMethod("POST");
                connection.connect();

                //Writing to API
                OutputStream outputStream =  connection.getOutputStream();
                writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(formatDataAsJSON());
                writer.close();
                outputStream.close();

                //Reading results of Post
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                String line = null;
                StringBuffer sb = new StringBuffer();
                String finalJSON;

                while ((line = reader.readLine())!= null){
                   // Log.d("line value", line + "  ");
                    sb.append(line);
                   // Log.d("StringBuffer value", sb.toString() + "  ");
                }

                finalJSON = sb.toString();
               // Log.d("finalJSON value", finalJSON.toString() + "  ");

                JSONObject jsonObject = new JSONObject(finalJSON);
                result =  jsonObject.getString("location");
                Log.d("result value", result + "  ");
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null){
                    connection.disconnect();
                } try {
                    if(reader != null){
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
        }
    }

    //-------- END OF CLASS ---------
}
