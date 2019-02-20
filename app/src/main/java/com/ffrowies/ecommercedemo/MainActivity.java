package com.ffrowies.ecommercedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btnMainJoinNow, btnMainLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnMainJoinNow = (Button) findViewById(R.id.btn_main_join_now);
        btnMainLogin = (Button) findViewById(R.id.btn_main_login);
    }
}
