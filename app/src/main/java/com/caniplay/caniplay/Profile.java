package com.caniplay.caniplay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Profile extends AppCompatActivity {

private TextView emailProfile,userNameProfile, fullNameProfile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        emailProfile= (TextView) findViewById(R.id.emailProfile);
        userNameProfile= (TextView) findViewById(R.id.userNameProfile);
        fullNameProfile = (TextView) findViewById(R.id.fullNameProfile);
        loadData();
    }


    @Override
    public void onBackPressed() {


        Intent main = new Intent(MyApplication.getAppContext(), MainActivity.class);
     startActivity(main);
    }


    public void loadData(){

        SharedPreferences prefs =
                getSharedPreferences("UserLogin", Context.MODE_PRIVATE);

        userNameProfile.setText(prefs.getString("userName", "default"));
        fullNameProfile.setText(prefs.getString("fullName", "default"));
        emailProfile.setText(prefs.getString("email", "default"));

    }
}
