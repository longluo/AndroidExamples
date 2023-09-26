package com.sabithpkcmnr.splashscreen;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ActivityHome extends AppCompatActivity {

    Button btClickMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Add all your codes here
        btClickMe = findViewById(R.id.btClickMe);
        btClickMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ActivityHome.this, "You clicked me. Thank you!", Toast.LENGTH_SHORT).show();
            }
        });

        //Happy coding :)

    }
}