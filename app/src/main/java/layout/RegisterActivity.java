package layout;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

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
                AddData();
                Intent intent = new Intent();
                intent.setClass(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
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
