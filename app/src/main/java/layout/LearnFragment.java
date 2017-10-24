package layout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerView;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import sg.com.singhealth.wayfinder.LocationDetail;
import sg.com.singhealth.wayfinder.MainActivity;
import sg.com.singhealth.wayfinder.R;

/**
 * File Name: LearnFragment.java
 * Created By: AY17 P3 FYPJ NYP SIT
 * Description: Before Starting Application
 *              Requires Changes at line 425, 426, 444
 *              Delete FireBase Data
 */

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LearnFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LearnFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LearnFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // Mapbox Variable
    private static final LatLngBounds NYPBLKL_BOUNDS = new LatLngBounds.Builder()
            .include(new LatLng(1.378921, 103.849596))
            .include(new LatLng(1.379427, 103.849945))
            .build();
    MapView mapView;
    private static MarkerView markerView;
    private static MarkerView existingMarker;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Toast toastAp;

    WifiManager wmgr;
    TextView locText;
    Button buttonLearn;
    String loc;
    JSONObject root = new JSONObject();
    ArrayList<LocationDetail> locationList = new ArrayList<LocationDetail>();
    final JSONArray wifiFingerprint = new JSONArray();

    DatabaseReference databaseLocation;

    public LearnFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LearnFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LearnFragment newInstance(String param1, String param2) {

        LearnFragment fragment = new LearnFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(getActivity(), getString(R.string.access_token));

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //-- Change Action Bar Title --
        ((MainActivity) getActivity()).setActionBarTitle("Learn");

        //-- View --
        final View rootView = inflater.inflate(R.layout.fragment_learn, container, false);

        //-- WifiManager --
        wmgr = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);


        //-- EditText Learn Input --
        locText = (TextView) rootView.findViewById(R.id.locationText);



        //-- MapBox MapView --
        mapView = (MapView) rootView.findViewById(R.id.mapViewLearn);
        mapView.onCreate(savedInstanceState);

        IconFactory iconFactory = IconFactory.getInstance(getActivity());
        final Icon icon = iconFactory.fromResource(R.drawable.ic_red_pin);

        IconFactory icFac = IconFactory.getInstance(getActivity());
        final Icon existingLocationIcon = icFac.fromResource(R.drawable.ic_existing_location);

        mapView.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(final MapboxMap mapboxMap) {

                mapboxMap.setOnMapLongClickListener(new MapboxMap.OnMapLongClickListener() {

                    @Override
                    public void onMapLongClick(@NonNull LatLng point) {
                        if(markerView != null) {
                            markerView.remove();
                            LatLng pos = new LatLng(point.getLatitude(), point.getLongitude());
                            markerView = mapboxMap.addMarker(new MarkerViewOptions().position(pos).icon(icon));

                        }else {
                            LatLng pos = new LatLng(point.getLatitude(), point.getLongitude());
                            markerView = mapboxMap.addMarker(new MarkerViewOptions().position(pos).icon(icon));
                        }
                    }
                });

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
                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 2000);

                //-- Set Camera LatLng Bounds --
                mapboxMap.setLatLngBoundsForCameraTarget(NYPBLKL_BOUNDS);

                mapboxMap.setMaxZoomPreference(20);
                mapboxMap.setMinZoomPreference(19);

                //-- Connecting to DB --
                databaseLocation = FirebaseDatabase.getInstance().getReference("locations");
                //-- Retrieving from DB --
                databaseLocation.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot locationSnapshot : dataSnapshot.getChildren()){
                            String locName =  locationSnapshot.child("id").getValue().toString();
                            Double latitude = (Double) locationSnapshot.child("latitude").getValue();
                            Double longitude = (Double) locationSnapshot.child("longitude").getValue();
                            LocationDetail locDetail = new LocationDetail(locName, latitude, longitude);
                            locationList.add(locDetail);
                        }

                        for (int m =0; m<locationList.size(); m++){
                            LatLng pos1 = new LatLng(locationList.get(m).getLatitude(), locationList.get(m).getLongitude());
                            existingMarker = mapboxMap.addMarker(new MarkerViewOptions().position(pos1).icon(existingLocationIcon));
                            existingMarker.setTitle(locationList.get(m).getId());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }
        });

        //-- Button Learn Click --
        buttonLearn = (Button)rootView.findViewById(R.id.buttonLearn);
        buttonLearn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(wmgr.isWifiEnabled()){
                    WifiInfo wifiInfo = wmgr.getConnectionInfo();
                    if(wifiInfo.getSupplicantState().toString().equals("COMPLETED")) {
                        if(!locText.getText().toString().matches("")) {
                            if(markerView != null) {
                                try {
                                    formatDataAsJSON();
                                    toastAp = Toast.makeText(getActivity(), "Finding AP", Toast.LENGTH_LONG);
                                    toastAp.show();
                                } finally {
                                    addLocation(markerView.getPosition());
                                }
                                //Hide Keyboard After Pressing Button
                                ((MainActivity) getActivity()).hideKeyboard(rootView);
                            }
                            else {
                                Toast.makeText(getActivity(), "Tap On Map To Add Geolocation." , Toast.LENGTH_SHORT).show();
                                ((MainActivity) getActivity()).hideKeyboard(rootView);
                            }
                        } else {
                            Toast.makeText(getActivity(), "Please Input Your Current Location." , Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), "Please Turn On Your WIFI Connection." , Toast.LENGTH_SHORT).show();
                }
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
    public void formatDataAsJSON() {
        CountDownTimer timer = new CountDownTimer(120000, 1000) {
            String timeStamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + "";
            ArrayList<JSONObject> fpArray = new ArrayList<>();

            @Override
            public void onTick(long l) {
                wmgr.startScan();

                if(fpArray.size() == 0){
                    ArrayList<ScanResult> results = (ArrayList<ScanResult>) wmgr.getScanResults();
                    for (int i=0; i <results.size(); i++) {
                        if (results.get(i).SSID.equalsIgnoreCase("NYP-Student")) {
                            try {
                                JSONObject fingerprint = new JSONObject();
                                fingerprint.put("mac", results.get(i).BSSID);
                                fingerprint.put("rssi", results.get(i).level);
                                fpArray.add(fingerprint);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    Log.d("fpArray1", fpArray.toString());

                } else {
                    if (fpArray.size() > 0) {
                        ArrayList<ScanResult> results = (ArrayList<ScanResult>) wmgr.getScanResults();
                        for (int i = 0; i < results.size(); i++) {
                            if (results.get(i).SSID.equalsIgnoreCase("NYP-Student")) {
                                boolean test = true;
                                try {
                                    JSONObject fingerprint = new JSONObject();
                                    fingerprint.put("mac", results.get(i).BSSID);

                                    for (int j = 0; j < fpArray.size(); j++) {
                                        if (fpArray.get(j).get("mac").toString().equalsIgnoreCase(fingerprint.get("mac").toString())) {
                                            int currLvl =  results.get(i).level;
                                            int avgLvl = (currLvl + (int)fpArray.get(j).get("rssi"))/2 ;
                                            fpArray.get(j).put("rssi", avgLvl);
                                            test = true;
                                            break;
                                        } else {
                                            test = false;
                                        }
                                    }
                                    if(!test){
                                        fingerprint.put("rssi", results.get(i).level);
                                        fpArray.add(fingerprint);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        Log.d("fpArray2", fpArray.toString());
                    }
                }
                buttonLearn.setEnabled(false);
                locText.setEnabled(false);
                //Toast.makeText(getActivity(), "Finding AP", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                try {
                    buttonLearn.setEnabled(false);
                    locText.setEnabled(false);
                    loc = locText.getText().toString();
                    root.put("group", "interim01");
                    root.put("username", "p3");
                    root.put("location", loc);
                    root.put("time", timeStamp);
                    for (int i =0; i<fpArray.size(); i++){
                        JSONObject fingerprint = new JSONObject();
                        fingerprint.put("mac", fpArray.get(i).get("mac"));
                        fingerprint.put("rssi", fpArray.get(i).get("rssi"));
                        wifiFingerprint.put(fingerprint);
                    }
                    Log.d("wifiFingerprint", wifiFingerprint + " ");
                    root.put("wifi-fingerprint", wifiFingerprint);
                    Log.d("wifi Detail3", root.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new PostLearnAPI().execute("https://ml.internalpositioning.com/learn");
                new GetCalculateAPI().execute("https://ml.internalpositioning.com/calculate?group=interim01");
                Toast.makeText(getActivity(), "Inserted Into Repository" , Toast.LENGTH_SHORT).show();
                buttonLearn.setEnabled(true);
                locText.setEnabled(true);

            }
        };
        timer.start();
    }

    //---- addLocation Methods ----
    private void addLocation(LatLng position){
        String locationName = locText.getText().toString().toUpperCase();
        double latitude = position.getLatitude();
        double longitude = position.getLongitude();

        if(!TextUtils.isEmpty(locationName)){
            String id = locationName;
            LocationDetail locDetail = new LocationDetail(id,latitude, longitude);
            databaseLocation.child(id).setValue(locDetail);
        }
    }

    //-------- END OF METHODS --------

    //-------- START OF CLASS --------

    //---- PostLearnAPI Task Class ----
    public class PostLearnAPI extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... params) {
            HttpsURLConnection connection = null;
            BufferedReader reader = null;
            BufferedWriter writer = null;
            String result;
            String jsonResult = null;

            try {
                jsonResult = root.toString();
                Log.d("JSON Result" , jsonResult + " ");
            } finally {
                try {
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
                    writer.write(jsonResult);
                    writer.close();
                    outputStream.close();

                    //Reading results of Post
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                    String line = null;
                    StringBuilder sb = new StringBuilder();

                    while ((line = reader.readLine())!= null){
                        sb.append(line);
                    }
                    result = sb.toString();
                    return result;

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
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
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            Log.d("Result of Post", result + " ");
            while(wifiFingerprint.length() > 0){
                for (int m=0; m<wifiFingerprint.length(); m++){
                    wifiFingerprint.remove(m);
                }
            }

            Log.d("wifiFingerprint Removed", wifiFingerprint + " ");
        }
    }

    //---- GetCalculateAPI Class -----
    public class GetCalculateAPI extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpsURLConnection connection = null;
            BufferedReader reader = null;

            try{
                //Connecting to API
                URL link = new URL(params[0]);
                connection = (HttpsURLConnection) link.openConnection();
                connection.connect();

                //Reading results of Post
                InputStream stream = connection.getInputStream();
                reader =  new BufferedReader(new InputStreamReader(stream));

                StringBuffer sBuffer = new StringBuffer();
                String line = "";

                while((line = reader.readLine()) != null){
                    sBuffer.append(line);
                }
                String finalJson = sBuffer.toString();

                return finalJson.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if(reader != null) {
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
