package layout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Preconditions;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import sg.com.singhealth.wayfinder.MainActivity;
import sg.com.singhealth.wayfinder.R;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ImageButton buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignin;

    private ProgressDialog progressDiaglog;

    private FirebaseAuth firebaseAuth;


    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
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
        final View rootView = inflater.inflate(R.layout.fragment_register, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        /*
        if(firebaseAuth.getCurrentUser() != null){
            getActivity().finish();
            startActivity(new Intent(getActivity().getApplicationContext(),ProfileFragment.class));
        }
        */

        textViewSignin =(TextView)rootView.findViewById(R.id.textViewSignin);
        buttonRegister = (ImageButton) rootView.findViewById(R.id.buttonRegister);
        editTextEmail = (EditText)rootView.findViewById(R.id.editTexEmail);
        editTextPassword = (EditText)rootView.findViewById(R.id.editTexPassword);

        progressDiaglog = new ProgressDialog(getActivity());

        textViewSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment;
                fragment = new LoginFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view == buttonRegister){
                    registerUser();
                }
                buttonRegister.setOnClickListener(this);

            }
            public void registerUser(){
                final String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                if (email.matches("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$")){

                }else {
                    Toast.makeText(getActivity(), "Email Error" , Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length()<6){
                    Toast.makeText(getActivity(), "Password Is Less Than Six character" , Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(email)){
                    //email is empty
                    Toast.makeText(getActivity(), "Please Enter Your Email" , Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    //password is empty
                    Toast.makeText(getActivity(), "Please Enter Your Password" , Toast.LENGTH_SHORT).show();
                    return;
                }

                final ProgressDialog Dialog = new ProgressDialog(getActivity());
                Dialog.setProgress(0);
                Dialog.setTitle("Registering User...");
                Dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                Dialog.setMax(100);
                Dialog.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int progress =0;
                        while (progress<100){
                            try {
                                Thread.sleep(20);
                                progress++;
                                Dialog.setProgress(progress);
                            }catch (InterruptedException e){
                                e.printStackTrace();
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Log.d(TAG,"Email sent.");
                                                }
                                            }
                                        });
                            }
                        }
                        Dialog.cancel();
                    }
                }).start();

                /*
                Dialog.setMessage("Registering User...");
                Dialog.show();
                */

                firebaseAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){

                                    Toast.makeText(getActivity(),"Welcome",Toast.LENGTH_LONG).show();
                                    Fragment fragment;
                                    fragment = new LoginFragment();
                                    FragmentManager fragmentManager = getFragmentManager();
                                    fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();

                                }else {
                                    Toast.makeText(getActivity(),"Could not register, Please Try Again",Toast.LENGTH_SHORT).show();
                                    Dialog.dismiss();
                                    return;
                                }
                            }
                        });

            }

        });;

        // Inflate the layout for this fragment
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

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }
    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
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
}
