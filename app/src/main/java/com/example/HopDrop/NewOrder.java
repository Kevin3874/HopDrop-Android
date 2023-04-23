package com.example.HopDrop;

import static com.example.HopDrop.LoginActivity.username_string;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;
import android.app.ProgressDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NewOrder extends AppCompatActivity {
    FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private boolean mediaUploaded = false;
    Uri imageuri;
    private StorageReference mStorageref = FirebaseStorage.getInstance().getReference();
    private DatabaseReference mDatabaseref = FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button sbtn = (Button) findViewById(R.id.save_btn);

        sbtn.setOnClickListener(v -> {
            // put them into a new object then put that object into firestore
            // update "from" so that it is a dropdown
            TextInputLayout fromLayout = findViewById(R.id.from);
            TextInputLayout toLayout = findViewById(R.id.to);
            TextInputLayout feeLayout = findViewById(R.id.fee);
            TextInputLayout detailsLayout = findViewById(R.id.additional_details);
            String from = fromLayout.getEditText().getText().toString();
            String to = toLayout.getEditText().getText().toString();
            String fee = feeLayout.getEditText().getText().toString();
            String details = detailsLayout.getEditText().getText().toString();

            if (TextUtils.isEmpty(from) || TextUtils.isEmpty(to) || TextUtils.isEmpty(fee)) {
                Toast.makeText(getApplicationContext(), "Please fill out the required fields", Toast.LENGTH_SHORT).show();
            } else if (!mediaUploaded) {
                Toast.makeText(getApplicationContext(), "Please upload your QR code", Toast.LENGTH_SHORT).show();
            } else {
                if (fee.toString().chars().filter(ch -> ch == '.').count() > 1) {
                    Toast.makeText(getApplicationContext(), "Please fix the fee input", Toast.LENGTH_SHORT).show();
                } else {
                    DocumentReference userRef = rootRef.collection("user_id").document(username_string);
                    rootRef.collection("orders").document("orders").get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            //add to firebase for all orders
                            Order order = new Order(username_string, from, to, fee, details);
                            rootRef.collection("orders").add(order);
                            //add to user's collection
                            userRef.update("currentOrders", FieldValue.arrayUnion(order));
                        } else {
                            Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
                        }
                    });
                    finish();
                }
            }
        });


        Button qbtn = findViewById(R.id.quit_btn);
        qbtn.setOnClickListener(v -> finish());

        Button qrbtn = findViewById(R.id.upload_qr);
        qrbtn.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 0);
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.show();

        if (imageuri !=null){
            StorageReference filereference  = mStorageref.child(System.currentTimeMillis()+
                    "."+getFileExtension(imageuri));

            filereference.putFile(imageuri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Toast.makeText(Upload_Photos.this, "Upload Successfull", Toast.LENGTH_SHORT).show();
                            filereference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadUrl = uri;
                                    Upload upload = new Upload(downloadUrl.toString());
                                    progressDialog.show();
                                    String  uploadId = mDatabaseref.push().getKey();
                                    mDatabaseref.child(uploadId).setValue(upload);
                                    progressDialog.setCanceledOnTouchOutside(false);
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.setMessage("Uploaded  " +(int)progress+"%");



                        }
                    });

        }else
            Toast.makeText(this, "Please Select a Image", Toast.LENGTH_SHORT).show();
    }

    private String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}