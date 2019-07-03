package com.stpl.pg4me;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.stpl.pg4me.Class.ManagerDetailsClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ManagerDetailsActivity extends AppCompatActivity {

    Button datepick, register;
    EditText name, phone, pgname, pincode;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    String dateString, namemanager, phonemanager, pgnamee, pgpincode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_details);
        name=findViewById(R.id.managername);
        phone=findViewById(R.id.managerphone);
        pgname=findViewById(R.id.pgname);
        pincode=findViewById(R.id.pgpincode);
        datepick=findViewById(R.id.date);
        register=findViewById(R.id.register);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("PG").child(firebaseUser.getUid());

        datepick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDate();

            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                namemanager=name.getText().toString();
                phonemanager=phone.getText().toString();
                pgnamee=pgname.getText().toString();
                pgpincode=pincode.getText().toString();
                if(namemanager.isEmpty() || phonemanager.isEmpty() || pgnamee.isEmpty() ||pgpincode.isEmpty() || dateString.isEmpty())
                {
                    Toast.makeText(ManagerDetailsActivity.this, "Fill all details", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    ManagerDetailsClass details= new ManagerDetailsClass();
                    details.setName(namemanager);
                    details.setPhone(phonemanager);
                    details.setPgname(pgnamee);
                    details.setPincode(pgpincode);
                    details.setDateCreated(dateString);
                    try{
                        databaseReference.child("PGDetails").setValue(details);
                        startActivity(new Intent(ManagerDetailsActivity.this, ManagerMainActivity.class));
                        finish();
                    }catch (DatabaseException e)
                    {
                        Toast.makeText(ManagerDetailsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    }

                }
            }
        });



    }

    private void getDate() {
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpd =new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf=new SimpleDateFormat("dd MMMM yyyy");
                calendar.set(year, month, dayOfMonth);
                dateString = sdf.format(calendar.getTime());
            }
        }, year, month, day);
        dpd.show();
        dpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(final DialogInterface dialog) {
                dialog.dismiss();

            }
        });

    }


}
