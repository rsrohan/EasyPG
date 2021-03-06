package com.stpl.pg4me;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.stpl.pg4me.Adapter.RecyclerAdapter;
import com.stpl.pg4me.Class.BoardedTenantClass;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManagerMainActivity extends AppCompatActivity {
    Button logout, addtenant;
    RecyclerView recyclerView;
    DatabaseReference ref ;
    List<BoardedTenantClass> tenants= new ArrayList<BoardedTenantClass>();
    FirebaseUser firebaseUser;
    String TAG="ManagerMainActivity";
    FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_main);
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseAnalytics= FirebaseAnalytics.getInstance(this);
        if (firebaseUser==null)
        {
            logFirebaseEvent("toWelcomeActivity", "firebase_user_is_null");
            startActivity(new Intent(ManagerMainActivity.this, WelcomeActivity.class));
            finish();
        }

        if (firebaseUser != null)
        {
            for (UserInfo profile : firebaseUser.getProviderData()) {
                // Id of the provider (ex: google.com)
                String providerId = profile.getProviderId();
                if (providerId.equals("phone")){
                    logFirebaseEvent("toTenantActivity", "firebase_user_is_not_null_with_phone");

                    startActivity(new Intent(ManagerMainActivity.this, TenantMainActivity.class));
                    finish();
                }else {
                    logout=findViewById(R.id.logout);
                    logout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(ManagerMainActivity.this, WelcomeActivity.class));
                            finish();
                        }
                    });
                    addtenant=findViewById(R.id.addtenantbtn);
                    addtenant.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(ManagerMainActivity.this, AddTenantActivity.class));
                        }
                    });
                    recyclerView=findViewById(R.id.recyclerview);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    ref= FirebaseDatabase.getInstance().getReference("PG").child(firebaseUser.getUid()).child("OnboardedTenants");
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            tenants.clear();
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                            {
                                BoardedTenantClass tenant=dataSnapshot1.getValue(BoardedTenantClass.class);
                                tenants.add(tenant);
                            }
                            RecyclerAdapter recyclerAdapter= new RecyclerAdapter(tenants, getApplicationContext());
                            recyclerView.setAdapter(recyclerAdapter);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    private void logFirebaseEvent(String eventName, String eventDetails)
    {
        Bundle params = new Bundle();
        params.putString(TAG, eventDetails);
        mFirebaseAnalytics.logEvent(eventName, params);
    }
}
