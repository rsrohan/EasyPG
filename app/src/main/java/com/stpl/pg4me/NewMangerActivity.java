package com.stpl.pg4me;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class NewMangerActivity extends AppCompatActivity {

    EditText email, password;
    Button signup;
    FirebaseAuth firebaseAuth;

    String mail;
    String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_manger);
        email=findViewById(R.id.emailsignup);
        password=findViewById(R.id.passwordemailsignup);
        signup=findViewById(R.id.signupemail);

        firebaseAuth= FirebaseAuth.getInstance();
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mail =email.getText().toString();
                pass=password.getText().toString();
                if(mail =="" || pass =="" || !mail.contains("@"))
                {
                    Toast.makeText(NewMangerActivity.this, "Please fill all the details. Also check if email is correct.", Toast.LENGTH_SHORT).show();
                }else firebaseAuth.createUserWithEmailAndPassword(mail, pass)
                        .addOnCompleteListener(NewMangerActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    startActivity(new Intent(NewMangerActivity.this, ManagerDetailsActivity.class));
                                    Toast.makeText(NewMangerActivity.this, "Account created.", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else
                                    Toast.makeText(NewMangerActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
