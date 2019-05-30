package com.example.easypg;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.easypg.Class.BoardedTenantClass;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class TenantDetailsActivity extends AppCompatActivity {
    TextView name , phone, rent, room;
    CircleImageView dp;
    String phonetenant;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_details);
        name = findViewById(R.id.profilename2);
        phone=findViewById(R.id.profilephone2);
        room = findViewById(R.id.profileroom2);
        rent = findViewById(R.id.profilerent2);
        dp = findViewById(R.id.profiletenant2);
        phonetenant = getIntent().getStringExtra("Phone");
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("PG").child(firebaseUser.getUid()).child("OnboardedTenants");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {

                    BoardedTenantClass tenant =dataSnapshot1.getValue(BoardedTenantClass.class);
                    if(tenant.getTenantphone().equals(phonetenant))
                    {
                        name.setText(tenant.getTenantname());
                        phone.setText(tenant.getTenantphone());
                        room.setText("ROOM: "+tenant.getTenantroom());
                        rent.setText("Rent Amount: Rs "+tenant.getTenantrentamount());
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        try
        {
            StorageReference storageRef =
                    FirebaseStorage.getInstance().getReference(phonetenant);
            storageRef.getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Got the download URL for 'users/me/profile.png'
                            url=uri.toString();
                            // Toast.makeText(getApplicationContext(), url, Toast.LENGTH_SHORT).show();
                            Glide.with(getApplicationContext()).load(url).into(dp);
                        }}).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        }catch (DatabaseException e)
        {
            Toast.makeText(this, "failed to load image"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
