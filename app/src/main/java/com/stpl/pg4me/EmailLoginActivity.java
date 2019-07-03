package com.stpl.pg4me;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class EmailLoginActivity extends AppCompatActivity {
    EditText email, password;
    Button login;
    TextView createnewuser;
    FirebaseAuth firebaseAuth;
    String mail,pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_login);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);

        firebaseAuth=FirebaseAuth.getInstance();
        login=findViewById(R.id.loginusingemail);
        createnewuser= findViewById(R.id.createnewusingemail);
        createnewuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmailLoginActivity.this, NewMangerActivity.class));
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mail=email.getText().toString();
                pass=password.getText().toString();
                if (mail=="" || pass=="")
                {
                    Toast.makeText(EmailLoginActivity.this, "Please fill all details.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    firebaseAuth.signInWithEmailAndPassword(mail, pass)
                            .addOnCompleteListener(EmailLoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful())
                                    {
                                        startActivity(new Intent(EmailLoginActivity.this, ManagerMainActivity.class));
                                        email.setText("");
                                        password.setText("");
                                        finish();
                                    }else{
                                        Toast.makeText(EmailLoginActivity.this, "Authentication failed, check your password. MAKE SURE YOU ARE HAVING AN ACCOUNT.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(EmailLoginActivity.this, WelcomeActivity.class));
        finish();
    }
}
