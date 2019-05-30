package com.example.easypg;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.easypg.Class.BoardedTenantClass;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST_CODE=1;
    private static final int GALLERY_REQUEST = 2;
    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE=3;

    ProgressDialog pd;
    Bitmap photo;
    StorageReference storageReference;
    DatabaseReference databaseReference ,ref;
    FirebaseUser firebaseUser;
    private Uri imageuri;
    private StorageTask uploadTask;
    Button savedetails, clickpicture, selectpicture;
    EditText editname, editphone;
    HashMap<String, Object> map =new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        savedetails=findViewById(R.id.savedetails);
        clickpicture=findViewById(R.id.takepicture);
        selectpicture=findViewById(R.id.selectpicture);
        editname=findViewById(R.id.editname);
        editphone=findViewById(R.id.editphone);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("Tenants");


        ref=FirebaseDatabase.getInstance().getReference("ProfilePicture");


        pd=new ProgressDialog(this);
        storageReference= FirebaseStorage.getInstance().getReference();


         databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    if (dataSnapshot1.getKey().equals(firebaseUser.getUid()))
                    {

                        BoardedTenantClass tenant=dataSnapshot1.child("Details").getValue(BoardedTenantClass.class);
                        editname.setText(tenant.getTenantname());
                        editphone.setText(tenant.getTenantphone());
                        break;
                    }
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        clickpicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkpermissionfromdevice())
                {
                    requestPermission();
                }
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);


            }
        });
        selectpicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkPermissionForReadExtertalStorage())
                {
                    try {
                        requestPermissionForReadExtertalStorage();
                    } catch (Exception e) {
                        Toast.makeText(EditProfileActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
                Intent galleryintent = new Intent();
                galleryintent.setType("image/*");
                galleryintent.setAction(Intent.ACTION_GET_CONTENT);
                //galleryintent.setAction(Intent.ACTION_PICK);
                startActivityForResult(galleryintent,GALLERY_REQUEST);

            }
        });
        savedetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name2 = editname.getText().toString();
                String phone2 = editphone.getText().toString();
                if(name2.isEmpty() || phone2.isEmpty() || phone2.length()!=13 )
                {
                    Toast.makeText(EditProfileActivity.this, "check details", Toast.LENGTH_SHORT).show();
                }else
                {

                    map.put("tenantname", name2);
                    map.put("tenantphone", phone2);
                    databaseReference.child(firebaseUser.getUid()).child("Details").updateChildren(map);
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("PG");
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                            {
                                for (DataSnapshot dataSnapshot2: dataSnapshot1.child("OnboardedTenants").getChildren())
                                {
                                    if(dataSnapshot2.getKey().equals(firebaseUser.getUid()))
                                    {
                                        dataSnapshot2.getRef().updateChildren(map);
                                        break;

                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    startActivity(new Intent(EditProfileActivity.this, TenantMainActivity.class));
                    finish();
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==GALLERY_REQUEST && resultCode==RESULT_OK)
        {

            imageuri=data.getData();
            if (uploadTask!=null && uploadTask.isInProgress())
            {
                Toast.makeText(this, "uploading", Toast.LENGTH_SHORT).show();
            }else
            {
                uploadimage();
            }
        }
        if (requestCode==CAMERA_REQUEST_CODE && resultCode==RESULT_OK)
        {
            photo = (Bitmap) data.getExtras().get("data");
            pd.setMessage("uploading");
            pd.show();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            byte[] b = stream.toByteArray();
            StorageReference filepath = storageReference.child(firebaseUser.getPhoneNumber());
            filepath.putBytes(b).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    pd.dismiss();
                    Toast.makeText(EditProfileActivity.this, "uploading finish", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(EditProfileActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }
            });
            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String mUri = uri.toString();
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("imageURL", mUri);
                    try{
                        ref.child(firebaseUser.getPhoneNumber()).setValue(map);
                    }catch (DatabaseException e)
                    {
                        Toast.makeText(EditProfileActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }


    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = getApplicationContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }
    public void requestPermissionForReadExtertalStorage() throws Exception {
        try {
            ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void uploadimage(){
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("uploading");
        pd.show();
        if (imageuri!=null)
        {
            final StorageReference fileReference=storageReference.child(firebaseUser.getPhoneNumber());
            uploadTask=fileReference.putFile(imageuri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful())
                    {


                        String mUri = task.getResult().toString();
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL", mUri);
                        try{
                            ref.child(firebaseUser.getPhoneNumber()).setValue(map);
                        }catch (DatabaseException e)
                        {
                            Toast.makeText(EditProfileActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                        pd.dismiss();

                    }else
                    {
                        pd.dismiss();
                        Toast.makeText(EditProfileActivity.this, "failed to upload", Toast.LENGTH_SHORT).show();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }else{
            Toast.makeText(this, "no image selected", Toast.LENGTH_SHORT).show();
        }

    }
    private boolean checkpermissionfromdevice()
    {
        int writeexstorageresult= ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readexstorageresult= ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        return writeexstorageresult == PackageManager.PERMISSION_GRANTED && readexstorageresult==PackageManager.PERMISSION_GRANTED && camera == PackageManager.PERMISSION_GRANTED;

    }
    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE
        },  1000);
    }
}
