package com.stpl.pg4me;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.stpl.pg4me.Class.BoardedTenantClass;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class TenantMainActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    TextView name, phone, room, rent;
    CircleImageView profile;
    FirebaseUser firebaseUser;
    Button logout, editprofile;
    FirebaseAuth mAuth;
    StorageReference storageReference;
    String url;
    String TAG="TenantMainActivity";
    FirebaseAnalytics mFirebaseAnalytics;



    BoardedTenantClass tenant = new BoardedTenantClass();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_main);
        name=findViewById(R.id.profilename);
        phone=findViewById(R.id.profilephone);
        room=findViewById(R.id.profileroom);
        rent=findViewById(R.id.profilerent);
        profile = findViewById(R.id.profiletenant);
        editprofile = findViewById(R.id.editprofile);
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseAnalytics =FirebaseAnalytics.getInstance(this);
        try
        {
            StorageReference storageRef =
                    FirebaseStorage.getInstance().getReference(firebaseUser.getPhoneNumber());
            storageRef.getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Got the download URL for 'users/me/profile.png'
                            url=uri.toString();
                           // Toast.makeText(getApplicationContext(), url, Toast.LENGTH_SHORT).show();
                            Glide.with(getApplicationContext()).load(url).into(profile);
                        }}).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        }catch (DatabaseException e)
        {
            Toast.makeText(this, "failed to load image"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        logout=findViewById(R.id.logouttenant);

        databaseReference=FirebaseDatabase.getInstance().getReference("Tenants");
        storageReference= FirebaseStorage.getInstance().getReference(firebaseUser.getPhoneNumber());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    if (dataSnapshot1.getKey().equals(firebaseUser.getUid()))
                    {

                        tenant=dataSnapshot1.child("Details").getValue(BoardedTenantClass.class);
                        name.setText(tenant.getTenantname());
                        logFirebaseEvent("findingTenant", tenant.getTenantname());
                        phone.setText(tenant.getTenantphone());
                        room.setText("ROOM: "+tenant.getTenantroom());
                        rent.setText("RENT AMOUNT: Rs "+tenant.getTenantrentamount());
                        break;
                    }
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        mAuth=FirebaseAuth.getInstance();


        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TenantMainActivity.this, EditProfileActivity.class));

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(TenantMainActivity.this, WelcomeActivity.class));
            }
        });

    }

    private void logFirebaseEvent(String eventName, String eventDetails)
    {
        Bundle params = new Bundle();
        params.putString(TAG, eventDetails);
        mFirebaseAnalytics.logEvent(eventName, params);
    }

}
