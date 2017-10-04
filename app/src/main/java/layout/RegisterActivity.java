package layout;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StreamDownloadTask;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sg.com.singhealth.wayfinder.R;
import sg.com.singhealth.wayfinder.User;

public class RegisterActivity extends AppCompatActivity {
    private TextView name,email,age,password,confirm;
    DatabaseReference databaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = (TextView) findViewById(R.id.Name);
        email = (TextView) findViewById(R.id.Email);
        age = (TextView) findViewById(R.id.Age);
        password = (TextView) findViewById(R.id.Password);
        confirm = (TextView) findViewById(R.id.confirm);

        databaseUser = FirebaseDatabase.getInstance().getReference("Users");





        Button RegisterButton = (Button)findViewById(R.id.Register);
        RegisterButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setClass(RegisterActivity.this, LoginActivity.class);
                String eEmail = "^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\\\.][A-Za-z]{2,3}([\\\\.][A-Za-z]{2})?$";


                if (email.getText().toString().equals("")){
                    Toast.makeText(RegisterActivity.this,"Email isn't null",Toast.LENGTH_SHORT).show();
                    return;
                }else if(name.getText().toString().equals("")){
                    Toast.makeText(RegisterActivity.this,"Username isn't null",Toast.LENGTH_SHORT).show();
                    return;
                }else if(age.getText().toString().equals("")){
                    Toast.makeText(RegisterActivity.this,"Age isn't null",Toast.LENGTH_SHORT).show();
                    return;
                }else if(password.getText().toString().equals("")){
                    Toast.makeText(RegisterActivity.this,"password isn't null",Toast.LENGTH_SHORT).show();
                    return;
                }else if(confirm.getText().toString().equals("")){
                    Toast.makeText(RegisterActivity.this,"confirm isn't null",Toast.LENGTH_SHORT).show();
                    return;
                }else if(!confirm.getText().toString().equals(password.getText().toString())){
                    Toast.makeText(RegisterActivity.this,"The password isn't match",Toast.LENGTH_SHORT).show();
                    return;


                }
                else {
                    if(password.getText().toString().length()<6){
                        Toast.makeText(RegisterActivity.this,"This password is too short",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(!email.getText().toString().matches(eEmail)){
                        Toast.makeText(RegisterActivity.this,"This email address is invalid",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    else {
                        AddData();
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });

    }
        public void AddData(){
            String Name = name.getText().toString();
            String Email = email.getText().toString();
            String Age = age.getText().toString();
            String Password = password.getText().toString();
            String Confirm = confirm.getText().toString();

            User saveData = new User(Name,Email,Age,Password,Confirm);
         databaseUser.child(Name).setValue(saveData);

        }
        }
