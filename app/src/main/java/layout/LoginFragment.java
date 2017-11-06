package layout;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import sg.com.singhealth.wayfinder.R;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ImageButton buttonSignIn;
    private TextView textViewForget;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FragmentManager manager;
    private FragmentTransaction ft;
    private Button button_backStart;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);

        manager = getFragmentManager();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){

          //  getActivity().finish();
        //    startActivity(new Intent(getActivity(),ProfileFragment.class));

        }

        buttonSignIn = (ImageButton) rootView.findViewById(R.id.buttonSignin);
        textViewForget = (TextView)rootView.findViewById(R.id.textViewForget) ;
        editTextEmail = (EditText)rootView.findViewById(R.id.editTexEmail);
        editTextPassword = (EditText)rootView.findViewById(R.id.editTexPassword);

        textViewForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                {
                    final FirebaseUser user = firebaseAuth.getCurrentUser();
                    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    final String emailAddress = editTextEmail.getText().toString();
                    if (emailAddress.equals("")) {
                        Toast.makeText(getActivity(), "Please enter your Email", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else {
                    firebaseAuth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Email sent.");
                                        Toast.makeText(getActivity(), "Email sent successful", Toast.LENGTH_SHORT).show();
                                        firebaseAuth.signOut();
                                        Fragment fragment;
                                        fragment = new LoginFragment();
                                        FragmentManager fragmentManager = getFragmentManager();
                                        fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();

                                    } else {
                                        Toast.makeText(getActivity(), "Email sent Error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                }
            }
        });

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonSignIn.setOnClickListener(this);
                progressDialog = new ProgressDialog(getActivity());
                if (view == buttonSignIn){
                    userLogin();
                }
            }

            private void userLogin(){
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();

                if (TextUtils.isEmpty(email)){
                    //email is empty
                    Toast.makeText(getActivity(), "Please enter Email" , Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    //password is empty
                    Toast.makeText(getActivity(), "Please enter Password" , Toast.LENGTH_SHORT).show();
                    return;
                }

                final ProgressDialog Dialog = new ProgressDialog(getActivity());
                Dialog.setMessage("Login...");
                Dialog.show();

                firebaseAuth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                            //    ProgressDialog.dismiss;
                                if(task.isSuccessful()){
                                    Dialog.dismiss();
                                    Toast.makeText(getActivity(),"Welcome",Toast.LENGTH_SHORT).show();

                                    Fragment fragment;
                                    fragment = new LearnFragment();
                                    FragmentManager fragmentManager = getFragmentManager();
                                    fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
                                }

                                else{
                                    Dialog.dismiss();
                                    Toast.makeText(getActivity(),"Email or Password is Error",Toast.LENGTH_SHORT).show();
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
