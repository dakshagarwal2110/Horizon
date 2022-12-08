package com.collegemeet.android.revafest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class EditDetails extends AppCompatActivity {

    RelativeLayout profilePictureChange;


    private RelativeLayout genderChangeFragment, nameChangeFragment, dateOfBirthChangeFragment, emailChangeFragment;
    public RelativeLayout editDetailsParent;
    private FrameLayout container_fragment_details;
    private ImageView cancelDetails;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private StorageReference reference;
    public static final String TAG = "MyTag";
    private Uri profileImageUri;
    private ProgressDialog progressDialog;
    private RelativeLayout about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_details);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        reference = FirebaseStorage.getInstance().getReference();


        progressDialog = new ProgressDialog(this);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setNavigationBarColor(getResources().getColor(R.color.black));
        }
        progressDialog.setMessage("Profile image is updating...");
        profilePictureChange = findViewById(R.id.profile_picture_change_settings);


        profilePictureChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dexter.withContext(getApplicationContext()).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {


                        if (isNetworkAvailable(EditDetails.this)) {
                            mGetContentProblemsImagePicker.launch("image/*");
                        } else {
                            Toast.makeText(EditDetails.this, "Network not available", Toast.LENGTH_SHORT).show();
                        }


                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                        permissionToken.continuePermissionRequest();
                    }
                }).check();

            }



            final ActivityResultLauncher<String> mGetContentProblemsImagePicker = registerForActivityResult(new ActivityResultContracts.GetContent(),
                    result -> {
                        if (result != null) {


                            resizeImage(result);

                        }


                    });

        });
    }


    public Boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network nw = connectivityManager.getActiveNetwork();
            if (nw == null) {
                return false;
            }
            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
            return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
        } else {
            NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
            return nwInfo != null && nwInfo.isConnected();
        }
    }



    private void resizeImage(Uri data){

        CropImage.activity(data)
                .setMultiTouchEnabled(true)
                .setAspectRatio(4,4)
                .setMaxCropResultSize(5000 , 5000)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setOutputCompressQuality(50)
                .start(this);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult newResult = CropImage.getActivityResult(data);

            progressDialog.setCancelable(false);
            progressDialog.show();
            SharedPreferences.Editor editor;
            SharedPreferences preferences;
            preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            editor = preferences.edit();


            String path = mAuth.getCurrentUser().getUid() + System.currentTimeMillis() + "." + getExtension(newResult.getUri());
            StorageReference fileRef = reference.child("Profile images").child(path);

            //uploading photo in storage
            fileRef.putFile(newResult.getUri()).addOnSuccessListener(taskSnapshot -> {

                //getting token of profile image
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    profileImageUri = uri;


                    DocumentReference detailsReference = db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("everything")
                            .document("details");

                    db.runTransaction(transaction -> {

                        transaction.update(detailsReference , "profile image" , profileImageUri.toString());


                        return null;
                    }).addOnSuccessListener(o -> {

                        editor.putString(MainActivityContainingFragment.PROFILE_IMAGE, newResult.getUri().toString());
                        editor.apply();
                        Toast.makeText(getApplicationContext(), "Profile image updated...", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        finish();

                    }).addOnFailureListener(e -> {

                        Toast.makeText(getApplicationContext(), "Error occurred!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        finish();

                    });

                });


            }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Image not updated", Toast.LENGTH_SHORT).show());

        }


    }

    public String getExtension(Uri result) {
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(getApplicationContext().getContentResolver().getType(result));
    }
}