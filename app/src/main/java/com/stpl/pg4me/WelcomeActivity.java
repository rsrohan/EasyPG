package com.stpl.pg4me;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;




public class WelcomeActivity extends AppCompatActivity {
    Button managerBtn, tenantBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        managerBtn= findViewById(R.id.managerbtn);
        tenantBtn= findViewById(R.id.tenantbtn);

        managerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this, EmailLoginActivity.class));
                finish();
            }
        });
        tenantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // tourGuide.cleanUp();
               startActivity(new Intent(WelcomeActivity.this, PhoneLoginActivity.class));
               finish();
            }
        });
    }
}
