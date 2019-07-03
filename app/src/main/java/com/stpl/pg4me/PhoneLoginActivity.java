package com.stpl.pg4me;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.stpl.pg4me.Class.BoardedTenantClass;
import com.stpl.pg4me.Class.NotOnboardedTenantsClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {
    EditText phone, verificationcode;

    Button login, getverificationcode;
    FirebaseAuth mAuth;
    String codesent;
    DatabaseReference databaseReference, databaseReference2;
    FirebaseUser firebaseUser;
    NotOnboardedTenantsClass tenant = new NotOnboardedTenantsClass();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        phone=findViewById(R.id.phone);
        verificationcode=findViewById(R.id.passwordphone);
        login=findViewById(R.id.loginusingphone);
        getverificationcode=findViewById(R.id.getverified);
        mAuth=FirebaseAuth.getInstance();


        getverificationcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVerified();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifysignincode();
            }
        });


    }

    private void verifysignincode() {
        String code = verificationcode.getText().toString();
        if(code.isEmpty())
        {
            Toast.makeText(this, "enter the verification code to continue.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            try{
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codesent, code);
                signInWithPhoneAuthCredential(credential);
            }catch (Exception e)
            {
                Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
            }


        }

    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential)
    {

        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if (task.isSuccessful())
                {
                    boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                    if (isNewUser)
                    {
                        databaseReference= FirebaseDatabase.getInstance().getReference("PG");
                        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
                        databaseReference2=FirebaseDatabase.getInstance().getReference("Tenants").child(firebaseUser.getUid()).child("Details");

                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                boolean exist = false;
                                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                                {
                                    for (DataSnapshot dataSnapshot2: dataSnapshot1.child("NotOnboardedTenants").getChildren())
                                    {
                                        if(dataSnapshot2.getKey().equals(firebaseUser.getPhoneNumber()))
                                        {
                                            exist=true;
                                            //Toast.makeText(TenantMainActivity.this, firebaseUser.getPhoneNumber(), Toast.LENGTH_SHORT).show();
                                            tenant=dataSnapshot2.getValue(NotOnboardedTenantsClass.class);
                                            BoardedTenantClass btc = new BoardedTenantClass();
                                            btc.setTenantname(tenant.getName());
                                            btc.setTenantphone(tenant.getPhone());
                                            btc.setTenantroom(tenant.getRoom());
                                            btc.setTenantrentamount(tenant.getRentamount());
                                            btc.setTenantpgid(dataSnapshot1.getKey());
                                            DatabaseReference ref = dataSnapshot1.child("OnboardedTenants").child(firebaseUser.getUid()).getRef();
                                            try{
                                                ref.setValue(btc);
                                                databaseReference2.setValue(btc);

                                            }catch (DatabaseException e)
                                            {
                                                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                            break;
                                        }
                                    }

                                }
                                if (!exist)
                                {
                                    Toast.makeText(getApplicationContext(), "Tenant do not exist in NotOnboardedTenants", Toast.LENGTH_LONG).show();
                                    firebaseUser.delete();
                                    startActivity(new Intent(PhoneLoginActivity.this, WelcomeActivity.class));
                                    finish();
                                }
                                else
                                {
                                    startActivity(new Intent(PhoneLoginActivity.this, TenantMainActivity.class));
                                    finish();
                                }



                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }else
                    {
                        startActivity(new Intent(PhoneLoginActivity.this, TenantMainActivity.class));
                        finish();
                    }


                }
                else
                {
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                    {
                        Toast.makeText(PhoneLoginActivity.this, "invalid code.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void getVerified()
    {
        String phonenumber=phone.getText().toString();
        if(phonenumber.isEmpty() || phonenumber.length()<13)
        {
            Toast.makeText(this, "please enter number properly.", Toast.LENGTH_SHORT).show();
        }
        else{
            PhoneAuthProvider.getInstance().verifyPhoneNumber(phonenumber, 60, TimeUnit.SECONDS, this, mcallbacks);
            Toast.makeText(this, "please check your message box and type the verification code below.", Toast.LENGTH_SHORT).show();

        }


    }
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mcallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            codesent=s;
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(PhoneLoginActivity.this, WelcomeActivity.class));
        finish();
    }
}
