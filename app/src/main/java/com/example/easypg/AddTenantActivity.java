package com.example.easypg;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.easypg.Class.NotOnboardedTenantsClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddTenantActivity extends AppCompatActivity {
    EditText name, phone, room, rentamount;
    Button addtodatabase;
    NotOnboardedTenantsClass notOnboardedTenants = new NotOnboardedTenantsClass();
    FirebaseUser firebaseUser;

    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tenant);
        name=findViewById(R.id.tenantname);
        phone=findViewById(R.id.tenantphone);
        room= findViewById(R.id.room);
        rentamount=findViewById(R.id.rentamount);
        addtodatabase=findViewById(R.id.addtodatabase);
        databaseReference= FirebaseDatabase.getInstance().getReference("PG");
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        addtodatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tenantname = name.getText().toString();
                String tenantphone= phone.getText().toString();
                String tenantroom =room.getText().toString();
                String tenantrentamount= rentamount.getText().toString();
                if (tenantname.isEmpty() || tenantphone.isEmpty() || tenantroom.isEmpty() || tenantrentamount.isEmpty())
                {
                    Toast.makeText(AddTenantActivity.this, "fill all details.", Toast.LENGTH_SHORT).show();
                }else
                {
                    notOnboardedTenants.setName(tenantname);
                    notOnboardedTenants.setPhone(tenantphone);
                    notOnboardedTenants.setRoom(tenantroom);
                    notOnboardedTenants.setRentamount(tenantrentamount);
                    try{
                        databaseReference.child(firebaseUser.getUid()).child("NotOnboardedTenants").child(tenantphone).setValue(notOnboardedTenants);
                        Toast.makeText(AddTenantActivity.this, "added", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddTenantActivity.this, ManagerMainActivity.class));
                        finish();
                    }catch (DatabaseException e)
                    {
                        Toast.makeText(AddTenantActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    }

                }
            }
        });
    }
}
