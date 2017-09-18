package layout;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.SupportMapFragment;

import java.util.Map;

import sg.com.singhealth.wayfinder.MainActivity;
import sg.com.singhealth.wayfinder.R;

/**
 * File Name: FindYourWayFragment.java
 * Created By: AY17 P3 FYPJ NYP SIT
 * Description: -
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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private MapView mapView;
    private LatLng currentLatLng;

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

        //-- MapBox MapView --
        mapView = (MapView) rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {

            //-- Customize map with markers, polylines, etc. --
            //-- MapBox URL --
            mapboxMap.setStyleUrl(getString((R.string.mapbox_url)));

            //-- MapBox Zoom On Location --
            final LatLng zoomLocation = new LatLng(1.3792949602146791, 103.84983998176449);
            CameraPosition position = new CameraPosition.Builder()
                .target(zoomLocation)
                .zoom(19) // Sets the zoom
                .build(); // Creates a CameraPosition from the builder
            mapboxMap.setCameraPosition(position);

            //-- MapBox Marker Location --
            MarkerViewOptions markerViewOptions = new MarkerViewOptions().position(new LatLng(1.379292, 103.849695));

            mapboxMap.addMarker(markerViewOptions);

            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 2000);

            /*
            //mapboxMap.setMinZoom(19);
            position = new CameraPosition.Builder()
                .target(currentLatLng)
                .zoom(20) // Sets the zoom
                .tilt(60)
                .build(); // Creates a CameraPosition from the builder
            */

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
    //-- MapBox onStart Method --
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    //-- MapBox onResume Method --
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    //-- MapBox onPause Method --
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    //-- MapBox onStop Method --
    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    //-- MapBox onSaveInstanceState Method --
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    //-- MapBox onLowMemory Method --
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    //-- MapBox onDestory Method --
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    //-------- END OF METHODS --------
}
