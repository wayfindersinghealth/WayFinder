package layout;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import sg.com.singhealth.wayfinder.MainActivity;
import sg.com.singhealth.wayfinder.R;

/**
 * File Name: LearnFragment.java
 * Created By: AY17 P3 FYPJ NYP SIT
 * Description: -
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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    WifiManager wmgr;
    TextView locText;
    Button buttonLearn;
    ListView listViewLearn;
    String location;

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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //-- Change Action Bar Title --
        ((MainActivity) getActivity()).setActionBarTitle("Learn");

        //-- View --
        View rootView = inflater.inflate(R.layout.fragment_learn, container, false);

        //-- JSON --
        new JSONtask().execute("https://ml.internalpositioning.com/locations?group=wayFindp3");

        //-- WifiManager --
        wmgr = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        //-- EditText Learn Input--
        locText = (TextView) rootView.findViewById(R.id.locationText);

        //-- Button Learn Click --
        buttonLearn = (Button)rootView.findViewById(R.id.buttonLearn);
        buttonLearn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(wmgr.isWifiEnabled()){
                    WifiInfo wifiInfo = wmgr.getConnectionInfo();
                    if(wifiInfo.getSupplicantState().toString().equals("COMPLETED")) {
                        if(!locText.getText().toString().matches("")) {
                            formatDataAsJSON();
                        } else{
                            Toast.makeText(getActivity(), "Please Input Your Current Location." , Toast.LENGTH_SHORT).show();
                        }
                    }
                } else{
                    Toast.makeText(getActivity(), "Please Turn On Your WIFI Connection." , Toast.LENGTH_SHORT).show();
                }
            }
        });

        //-- ListView Learn --
        listViewLearn = (ListView)rootView.findViewById(R.id.listViewLearn);

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

    //---- Format Data As JSON Method ----
    private String formatDataAsJSON() {
        JSONObject root = new JSONObject();
        JSONArray wifiFingerprint = new JSONArray();
        JSONObject fingerprint = new JSONObject();

        List<ScanResult> results = wmgr.getScanResults();
        String timeStamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + "";
        wmgr.startScan();

        for (ScanResult R : results) {
            if (!R.SSID.equals("NYP-Student")) {
                try {
                    fingerprint.put("mac", R.BSSID.toString());
                    fingerprint.put("rssi", R.level);
                    wifiFingerprint.put(fingerprint);
                    location = locText.getText().toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            root.put("group", "wayfindp3");
            root.put("username", "P3");
            root.put("location", location);
            root.put("time", timeStamp);
            root.put("wifi-fingerprint", wifiFingerprint);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("JSON Value",root.toString());
        return root.toString();
    }



    //-------- END OF METHODS --------

    //-------- START OF CLASS --------


    public class JSONtask extends AsyncTask<String, String, String > {
        ArrayList<String> aList = new ArrayList<String>();

        @Override
        protected String doInBackground(String... params) {
            HttpsURLConnection connection = null;
            BufferedReader reader = null;

            try{
                URL link = new URL(params[0]);
                connection = (HttpsURLConnection) link.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader =  new BufferedReader(new InputStreamReader(stream));

                StringBuffer sBuffer = new StringBuffer();
                String line = "";

                while((line = reader.readLine()) != null){
                    sBuffer.append(line);
                }
                String finalJson = sBuffer.toString();
                JSONObject parentObject = new JSONObject(finalJson);
                JSONObject parentArray = parentObject.getJSONObject("locations");

                Iterator<String> iterator = parentArray.keys();
                while(iterator.hasNext()){
                    aList.add(iterator.next());
                }

                return aList.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(connection != null) {
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
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    getActivity(),
                    android.R.layout.simple_list_item_1,
                    aList );
            listViewLearn.setAdapter(arrayAdapter);
        }
    }
    //-------- END OF CLASS --------
}
