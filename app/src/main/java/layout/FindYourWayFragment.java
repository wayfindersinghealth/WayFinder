package layout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import com.graphhopper.GraphHopper;
import com.graphhopper.util.Constants;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerView;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.PathWrapper;
import com.graphhopper.util.Helper;
import com.graphhopper.util.Parameters.Algorithms;
import com.graphhopper.util.Parameters.Routing;
import com.graphhopper.util.PointList;
import com.graphhopper.util.ProgressListener;
import com.graphhopper.util.StopWatch;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import sg.com.singhealth.wayfinder.AndroidDownloader;
import sg.com.singhealth.wayfinder.AndroidHelper;
import sg.com.singhealth.wayfinder.GHAsyncTask;
import sg.com.singhealth.wayfinder.LocTracker;
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
public class FindYourWayFragment extends Fragment implements SensorEventListener {
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
    private static MarkerView markerViewCurrent = null;
    MarkerView locMarker = null;
    WifiManager wmgr;
    AutoCompleteTextView autoCompleteTextViewTo;
    ArrayList<String> aList = new ArrayList<String>();
    DatabaseReference databaseLocation;


    private OnFragmentInteractionListener mListener;
    private double locLatitude = 0;
    private double locLongitude = 0;
    boolean movements = false;
    SensorEvent sEvent;


    //-- Graphhopper Variables --
    private File mapsFolder;
    private volatile boolean prepareInProgress = false;
    private String currentArea = "singapore7";
    private GraphHopper hopper;
    private volatile boolean shortestPathRunning = false;
    private LatLng end;
    private String downloadURL;
    private List<LatLng> calculatedPoints = new ArrayList<>();
    private List<Polyline> calculatedPolylines = new ArrayList<>();
    private List<LatLng> points;
    private List<Polyline> polylines = new ArrayList<Polyline>();

    //-- Significant MotionSensor --
    private SensorManager sensorManager;
    private boolean color = false;
    private View view;
    private long lastUpdate;

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

        //-- Set Graphhopper Map Folder --
        boolean greaterOrEqKitkat = Build.VERSION.SDK_INT >= 19;
        if (greaterOrEqKitkat) {
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                logUser("GraphHopper is not usable without an external storage!");
                return;
            }
            mapsFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "/graphhopper/maps/");
        } else
            mapsFolder = new File(Environment.getExternalStorageDirectory(), "/graphhopper/maps/");

        if (!mapsFolder.exists())
            mapsFolder.mkdirs();

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
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

        //-- Init Singapore7 GMap
        initFiles(currentArea);

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
                mapboxMap.setMaxZoomPreference(20);
                mapboxMap.setMinZoomPreference(18.7);


                            if (movements = true) {
                                float[] values = sEvent.values;
                                // Movement
                                float x = values[0];
                                float y = values[1];
                                float z = values[2];

                                float accelationSquareRoot = (x * x + y * y + z * z)
                                        / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
                                long actualTime = sEvent.timestamp;
                                if (accelationSquareRoot >= 1.25) //
                                {
                                    if (actualTime - lastUpdate < 200) {
                                        return;
                                    }
                                    lastUpdate = actualTime;

                                    int times = 10;
                                    boolean truth = true;
                                    ArrayList<LocTracker> locationArray = new ArrayList<>();

                                    try {
                                        for (int t = 0; t < times; t++) {
                                            String locations = new PostTrackAPI().execute("https://ml.internalpositioning.com/track").get();

                                            if (locations != null) {
                                                Log.d("Location Name API", locations);

                                                if (locationArray.size() == 0) {
                                                    LocTracker thisLoc = new LocTracker();
                                                    thisLoc.setCounter(1);
                                                    thisLoc.setLocationName(locations);
                                                    locationArray.add(thisLoc);

                                                } else {
                                                    for (int i = 0; i < locationArray.size(); i++) {
                                                        if (locationArray.get(i).getLocationName().equalsIgnoreCase(locations)) {
                                                            locationArray.get(i).setCounter(locationArray.get(i).getCounter() + 1);
                                                            truth = true;
                                                            break;
                                                        } else {
                                                            truth = false;
                                                        }
                                                    }
                                                    if (!truth) {
                                                        LocTracker location = new LocTracker();
                                                        location.setLocationName(locations);
                                                        location.setCounter(1);
                                                        locationArray.add(location);
                                                    }
                                                }

                                            }
                                        }
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    }

                                    //Before Sorting ArrayList
                                    for (int i = 0; i < locationArray.size(); i++) {
                                        Log.d("Before Sort", locationArray.get(i).getLocationName() + ", " + locationArray.get(i).getCounter());
                                    }

                                    //Comparing and Sorting based on least counter to most counter
                                    int maxCounter;
                                    String maxLocation = null;
                                    if (locationArray.size() == 1) {
                                        for (int h = 0; h < locationArray.size(); h++) {
                                            maxLocation = locationArray.get(h).getLocationName();
                                            maxCounter = locationArray.get(h).getCounter();
                                        }
                                    } else if (locationArray.size() > 0) {
                                        //Sorting arraylist according to Smallest to Largest Counter
                                        Collections.sort(locationArray, new Comparator<LocTracker>() {
                                            @Override
                                            public int compare(LocTracker locTracker, LocTracker t1) {
                                                return Integer.compare(locTracker.getCounter(), t1.getCounter());
                                            }
                                        });

                                        //After Sorting ArrayList
                                        for (int j = 0; j < locationArray.size(); j++) {
                                            Log.d("After Sort", locationArray.get(j).getLocationName() + ", " + locationArray.get(j).getCounter());
                                        }
                                        Log.d("Last Item After Sort", locationArray.get(locationArray.size() - 1).getLocationName() + ", " + locationArray.get(locationArray.size() - 1).getCounter());
                                        maxLocation = locationArray.get(locationArray.size() - 1).getLocationName();

                                    }

                                    //-- Compare to Database --
                                    if (maxLocation == null) {
                                        Log.d("maxLocation: ", "NULL");
                                    } else {
                                        String finalLocation = maxLocation.toUpperCase();
                                        Log.d("Final Location", finalLocation);
                                        Log.d("-----------------------", "-----------------------");

                                        Query locationQuery = databaseLocation.orderByChild("id").equalTo(finalLocation);
                                        locationQuery.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {
                                                    //-- Get Longitude and Latitude --
                                                    locLatitude = (double) locationSnapshot.child("latitude").getValue();
                                                    locLongitude = (double) locationSnapshot.child("longitude").getValue();

                                                    currentLocation = new LatLng(locLatitude, locLongitude);
                                                    Log.d("LatLng", locLatitude + ", " + locLongitude);


                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }

                                }

                                if (locLatitude != 0 && locLongitude != 0) {
                                    //-- Set Marker on Map --
                                    LatLng latLng = new LatLng(locLatitude, locLongitude);

                                    //-- Marker Icon --
                                    IconFactory iconFactory = IconFactory.getInstance(getActivity());
                                    Icon icon = iconFactory.fromResource(R.drawable.ic_curr_location);

                                    if (markerView == null) {
                                        markerView = mapboxMap.addMarker(new MarkerViewOptions().position(new LatLng(locLatitude, locLongitude)));
                                        markerView.setIcon(icon);
                                        markerView.setTitle("You Are Here");
                                    } else {
                                        if (!(markerViewCurrent == null)) {
                                            calcPath(locLatitude, locLongitude, markerViewCurrent.getPosition().getLatitude(), markerViewCurrent.getPosition().getLongitude(), mapboxMap);
                                        }
                                        markerView.setPosition(latLng);
                                        markerView.setIcon(icon);
                                        markerView.setTitle("You Are Here");
                                    }
                                }


                            }
                            movements = false;





                //-- OnClick To Set Destination Flag Marker --
                mapboxMap.setOnMapLongClickListener(new MapboxMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng point) {
                        if (!isReady()) {
                            logUser("Load Map or Graph failed!");
                            return;
                        }
                        if (shortestPathRunning) {
                            logUser("Calculation still in progress");
                            return;
                        }
                        //-- Destination Marker Icon --
                        IconFactory iconFactory = IconFactory.getInstance(getActivity());
                        Icon icon = iconFactory.fromResource(R.drawable.ic_destination_flag);

                        if (currentLocation != null && end == null) {
                            end = point;
                            shortestPathRunning = true;

                            // Add the marker to the map
                            if(markerViewCurrent == null){
                                markerViewCurrent = mapboxMap.addMarker(new MarkerViewOptions().position(end));
                                markerViewCurrent.setIcon(icon);
                                markerViewCurrent.setTitle("Destination");
                            }else{
                                markerViewCurrent.setPosition(end);
                                markerViewCurrent.setIcon(icon);
                                markerViewCurrent.setTitle("Destination");
                            }

                            // Calculate Shortest Path
                            calcPath(currentLocation.getLatitude(), currentLocation.getLongitude(), end.getLatitude(), end.getLongitude(), mapboxMap);

                        } else if (currentLocation != null && end != null) {
                            end = point;
                            shortestPathRunning = true;

                            // Add the marker to the map
                            markerViewCurrent.setPosition(point);
                            markerViewCurrent.setIcon(icon);
                            markerViewCurrent.setTitle("Destination");

                            // Calculate Shortest Path
                            calcPath(currentLocation.getLatitude(), currentLocation.getLongitude(), end.getLatitude(), end.getLongitude(), mapboxMap);
                        }else {
                            Toast.makeText(getActivity(), "Locating, Please Wait." , Toast.LENGTH_SHORT).show();
                        }

                    }
                });


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

                                    end = new LatLng(locLatitude, locLongitude);

                                    Log.d("LatLng", locLatitude + ", " + locLongitude);

                                    //-- WayPoint Destination for Routing --

                                    //-- Marker Icon --
                                    IconFactory iconFactory = IconFactory.getInstance(getActivity());
                                    Icon icon = iconFactory.fromResource(R.drawable.ic_destination_flag);

                                    //-- Set Marker on Map --
                                    LatLng latLng = new LatLng(locLatitude, locLongitude);

                                    if (markerViewCurrent != null) {
                                        markerViewCurrent.setPosition(latLng);
                                        markerViewCurrent.setIcon(icon);

                                    } else if (markerViewCurrent == null){
                                        markerViewCurrent = mapboxMap.addMarker(new MarkerViewOptions().position(new LatLng(locLatitude, locLongitude)));
                                        markerViewCurrent.setIcon(icon);
                                    }

                                    calcPath(currentLocation.getLatitude(), currentLocation.getLongitude(), end.getLatitude(), end.getLongitude(), mapboxMap);
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

    @Override
    public void onSensorChanged(SensorEvent event) {
        sEvent = event;
        movements = true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void registerSensorListener() {
        sensorManager.registerListener(this, sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0), SensorManager.SENSOR_DELAY_NORMAL);
    }
//
//    private void unregisterSensorListener() {
//        mSensorManager.unregisterListener(this);
//    }

    //---- MapBox onStart Method ----
    @Override
    public void onStart() {
        markerView = null;
        super.onStart();
        mapView.onStart();
        Log.d("onStart", "On start method");
    }

    //---- MapBox onResume Method ----
    @Override
    public void onResume() {
        markerView = null;
        super.onResume();
        registerSensorListener();
        mapView.onResume();
        Log.d("onResume", "On resume method");

    }

    //---- MapBox onPause Method ----
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        Log.d("onPause", "On pause method");
        if (t != null){
            t.cancel();
            t = null;
            markerView = null;
        }
    }

    //---- MapBox onStop Method ----
    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
        Log.d("onStop", "On stop method");
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

    //-------- Graphhopper Methods --------

    //---- IsReady Method ----
    boolean isReady() {
        // only return true if already loaded
        if (hopper != null)
            return true;

        if (prepareInProgress) {
            logUser("Preparation still in progress");
            return false;
        }
        logUser("Prepare finished but hopper not ready. This happens when there was an error while loading the files");
        return false;
    }

    //---- InitFiles Method ----
    private void initFiles(String area) {
        prepareInProgress = true;
        currentArea = area;
        downloadingFiles();
    }


    //---- LoadMap Method ----
    void loadMap(File areaFolder) {
        loadGraphStorage();
    }

    //---- DownloadingFiles Method ----   Maybe no need this method -
    void downloadingFiles() {
        final File areaFolder = new File(mapsFolder, currentArea + "-gh");
        if (downloadURL == null || areaFolder.exists()) {
            loadMap(areaFolder);
            return;
        }

        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Downloading and uncompressing " + downloadURL);
        dialog.setIndeterminate(false);
        dialog.setMax(100);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.show();

        new GHAsyncTask<Void, Integer, Object>() {
            protected Object saveDoInBackground(Void... _ignore)
                    throws Exception {
                String localFolder = Helper.pruneFileEnd(AndroidHelper.getFileName(downloadURL));
                localFolder = new File(mapsFolder, localFolder + "-gh").getAbsolutePath();
                log("downloading & unzipping " + downloadURL + " to " + localFolder);
                AndroidDownloader downloader = new AndroidDownloader();
                downloader.setTimeout(30000);
                downloader.downloadAndUnzip(downloadURL, localFolder,
                        new ProgressListener() {
                            @Override
                            public void update(long val) {
                                publishProgress((int) val);
                            }
                        });
                return null;
            }

            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                dialog.setProgress(values[0]);
            }

            protected void onPostExecute(Object _ignore) {
                dialog.dismiss();
                if (hasError()) {
                    String str = "An error happened while retrieving maps:" + getErrorMessage();
                    log(str, getError());
                    logUser(str);
                } else {
                    loadMap(areaFolder);
                }
            }
        }.execute();
    }


    //---- LoadGraphStorage Method ----
    void loadGraphStorage() {
//        logUser("loading graph (" + Constants.VERSION + ") ... ");
        new GHAsyncTask<Void, Void, Path>() {
            protected Path saveDoInBackground(Void... v) throws Exception {
                GraphHopper tmpHopp = new GraphHopper().forMobile();
                tmpHopp.load(new File(mapsFolder, currentArea).getAbsolutePath() + "-gh");
                log("found graph " + tmpHopp.getGraphHopperStorage().toString() + ", nodes:" + tmpHopp.getGraphHopperStorage().getNodes());
                hopper = tmpHopp;
                return null;
            }

            protected void onPostExecute(Path o) {
                if (hasError()) {
                    logUser("An error happened while creating graph:"
                            + getErrorMessage());
                } else {
//                    logUser("Finished loading graph.");
                }
                finishPrepare();
            }
        }.execute();
    }


    //----FinishPrepare Method ----
    private void finishPrepare() {
        prepareInProgress = false;
    }

    //---- CalcPath Method ----
    public void calcPath(final double fromLat, final double fromLon,
                         final double toLat, final double toLon, final MapboxMap mapboxMap) {

        log("calculating path ...");
        new AsyncTask<Void, Void, PathWrapper>() {
            float time;

            protected PathWrapper doInBackground(Void... v) {
                StopWatch sw = new StopWatch().start();
                GHRequest req = new GHRequest(fromLat, fromLon, toLat, toLon).
                        setAlgorithm(Algorithms.DIJKSTRA_BI);
                req.getHints().
                        put(Routing.INSTRUCTIONS, "false");
                GHResponse resp = hopper.route(req);
                time = sw.stop().getSeconds();
                return resp.getBest();
            }

            protected void onPostExecute(PathWrapper resp) {
                if (!resp.hasErrors()) {
                    if(polylines != null){
                        for(int j = 0; j< polylines.size(); j++){
                            mapboxMap.removePolyline(polylines.get(j));
                        }

                        log("from:" + fromLat + "," + fromLon + " to:" + toLat + ","
                                + toLon + " found path with distance:" + resp.getDistance()
                                / 1000f + ", nodes:" + resp.getPoints().getSize() + ", time:"
                                + time + " " + resp.getDebugInfo());

                        points = createPathLayer(resp);

                        if (points.size() > 0) {
                            for (int i = 0; i < points.size() - 1; i++) {
                                // Draw polyline on map
                                Polyline polyline = mapboxMap.addPolyline(new PolylineOptions()
                                        .add(points.get(i))
                                        .add(points.get(i + 1))
                                        .color(Color.parseColor("#F27777"))
                                        .width(3));
                                polylines.add(polyline);
                            }
                            setCalculatedPointsAndPolylines(points, polylines);
                        }
                    }else{
                        log("from:" + fromLat + "," + fromLon + " to:" + toLat + ","
                                + toLon + " found path with distance:" + resp.getDistance()
                                / 1000f + ", nodes:" + resp.getPoints().getSize() + ", time:"
                                + time + " " + resp.getDebugInfo());


                        points = createPathLayer(resp);

                        if (points.size() > 0) {
                            for (int i = 0; i < points.size() - 1; i++) {
                                // Draw polyline on map
                                Polyline polyline = mapboxMap.addPolyline(new PolylineOptions()
                                        .add(points.get(i))
                                        .add(points.get(i + 1))
                                        .color(Color.parseColor("#F27777"))
                                        .width(3));
                                polylines.add(polyline);
                            }
                        }
                        setCalculatedPointsAndPolylines(points, polylines);
                    }


                } else {
                    logUser("Error:" + resp.getErrors());
                }
                shortestPathRunning = false;
            }
        }.execute();
    }

    //---- List CreatePathLayer Method ----
    private List<LatLng> createPathLayer(PathWrapper response) {
        List<LatLng> geoPoints = new ArrayList<>();
        PointList pointList = response.getPoints();
        for (int i = 0; i < pointList.getSize(); i++) {
            geoPoints.add(new LatLng(pointList.getLatitude(i), pointList.getLongitude(i)));
        }
        return geoPoints;
    }

    //---- SetCalculatedPointsAndPolylines Method ----
    private void setCalculatedPointsAndPolylines(List<LatLng> points, List<Polyline> polylines) {
        calculatedPoints = points;
        calculatedPolylines = polylines;
    }

    //---- Log Method ----
    private void log(String str) {
        Log.i("GH", str);
    }

    //---- Log Method ----
    private void log(String str, Throwable t) {
        Log.i("GH", str, t);
    }

    //---- LogUser Method ----
    private void logUser(String str) {
        log(str);
        Toast.makeText(getActivity(), str, Toast.LENGTH_LONG).show();
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
            root.put("group", "test05");
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
            String result = null;

            try{
                do {
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
                    Log.d("finalJSON value", finalJSON.toString() + "  ");

                    JSONObject jsonObject = new JSONObject(finalJSON);
                    //Log.d("JSONObject" , jsonObject + "");
                    result =  jsonObject.getString("location");
                    //Log.d("result value", result + "  ");
                    return result;

                } while (result == null);

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
