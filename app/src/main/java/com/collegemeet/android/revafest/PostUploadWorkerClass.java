package com.collegemeet.android.revafest;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PostUploadWorkerClass extends Worker {


    private static final String TAG = "MyTag";
    private final StorageReference reference;
    private final FirebaseAuth mAuth;
    FirebaseFirestore db;
    Context context;
    String postUri;
    String caption;
    String postType;
    int number_of_tagged;
    ArrayList<String> uidsTagged;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    ArrayList<String> uidTaggeds;
    public static final String TAGO = "MyTago";

    public PostUploadWorkerClass(@NonNull Context context, @NonNull WorkerParameters workerParams) {

        super(context, workerParams);
        this.context = context;
        db = FirebaseFirestore.getInstance();
        reference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


    }

    @NonNull
    @Override
    public Result doWork() {


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();


        try {

            Map<String, Object> postDetails = new HashMap<>();
            Map<String, Object> postDetailsTagged = new HashMap<>();
            Data data = getInputData();
//            uidsTagged = new ArrayList<>();
            String[] uidsTagged;
            uidTaggeds = new ArrayList<>();


            postUri = data.getKeyValueMap().get(AddPostsActivity.POST_URI_KEY).toString();
            caption = data.getKeyValueMap().get(AddPostsActivity.CAPTION_MAP_KEY).toString();
            postType = data.getKeyValueMap().get(AddPostsActivity.TYPE_OF_POST).toString();


            number_of_tagged = (int) data.getKeyValueMap().get(AddPostsActivity.YOTAL_TAGGED_USERS);
            if (number_of_tagged != 0) {
                uidsTagged = (String[]) data.getKeyValueMap().get(AddPostsActivity.UID_OF_TAGGED_TO_WORKER_CLASS);
                uidTaggeds = new ArrayList<>(Arrays.asList(uidsTagged));
            }


//            new ArrayList<String>(Arrays.asList(l))


            if (number_of_tagged != 0) {
                if (uidTaggeds != null) {
                    postDetailsTagged.put("totalTagged", number_of_tagged);
                    postDetailsTagged.put("uidsTagged", uidTaggeds);
                }

            }


            String date = (String) android.text.format.DateFormat.format("MMMM dd, yyyy h:mm a", new java.util.Date());


            postDetails.put("caption", caption);

            postDetails.put("date", date);
            postDetails.put("time in millis", System.currentTimeMillis());
            postDetails.put("uid", mAuth.getCurrentUser().getUid());

            //to decide VideoView or ImageView save postType ....

            postDetails.put("type_of_post", postType);


            if (postUri.equals("")) {
                return Result.failure();
            }
            String path = System.currentTimeMillis() + mAuth.getCurrentUser().getUid() + getExtension();
            postDetails.put("path", path);
            DocumentReference documentReference = db.collection("users").document(mAuth.getCurrentUser().getUid())
                    .collection("everything").document("post")
                    .collection("All post").document(path);


            StorageReference fileRef = reference.child("Posts").child(path);
            fileRef.putFile(Uri.parse(postUri)).addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {


                postDetails.put("post uri", uri.toString());
                editor.putString(AddPostsActivity.PATH_POST, path);
                postDetails.put("coordinates", "fest");
                editor.putInt(AddPostsActivity.POST_UPLOAD, 1);
                editor.putString(AddPostsActivity.PATH_POST_STORAGE, "Posts/" + path);

                editor.apply();
                Log.d("oops_done", "post storage");

                //path helps to create named document in storage and college coordinates folder to fetch also to delete


                //for owner details uploaded

                documentReference.set(postDetails).addOnSuccessListener(unused -> {

                    Log.d("oops_done", "post users");


                    if (number_of_tagged != 0) {
                        if (uidTaggeds != null) {
                            db.collection("users").document(mAuth.getCurrentUser().getUid())
                                    .collection("everything").document("post")
                                    .collection("All post tags").document(path)
                                    .collection("TaggedUsers").document(path).
                                    set(postDetailsTagged).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    editor.putInt(AddPostsActivity.POST_UPLOAD, 2);
                                    editor.putString(AddPostsActivity.PATH_POST, path);
                                    editor.apply();


                                    //start




                                    //fetching coordinates...
                                    DocumentReference detailsDocumentFirebase = db.collection("users")
                                            //here can be problem
                                            .document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                                            .collection("everything")
                                            .document("details");
                                    detailsDocumentFirebase.get().addOnSuccessListener(documentSnapshot -> {



                                        postDetails.put("coordinates", "fest");


                                        db.collection("All colleges posts").document("fest")
                                                .collection("posts").document(path).set(postDetails).addOnSuccessListener(unused1 -> {


                                            if (number_of_tagged != 0) {
                                                if (uidTaggeds != null) {
                                                    db.collection("All colleges posts").document("fest")
                                                            .collection("college tags").document(path).set(postDetailsTagged).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            editor.putString(AddPostsActivity.COMPLETED_STRING, "done");
                                                            editor.putInt(AddPostsActivity.POST_UPLOAD, 3);
                                                            editor.putString(AddPostsActivity.PATH_POST, "");
                                                            editor.putString(AddPostsActivity.PATH_POST_STORAGE, "");
                                                            editor.apply();
                                                        }
                                                    });
                                                }

                                            } else {
                                                editor.putString(AddPostsActivity.COMPLETED_STRING, "done");
                                                editor.putInt(AddPostsActivity.POST_UPLOAD, 3);
                                                editor.putString(AddPostsActivity.PATH_POST, "");
                                                editor.putString(AddPostsActivity.PATH_POST_STORAGE, "");
                                                editor.apply();
                                            }


                                        });


                                    });




                                    //end


                                }
                            });
                        }

                    } else {
                        editor.putInt(AddPostsActivity.POST_UPLOAD, 2);
                        editor.putString(AddPostsActivity.PATH_POST, path);
                        editor.apply();

                        //fetching coordinates...
                        DocumentReference detailsDocumentFirebase = db.collection("users")
                                //here can be problem
                                .document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                                .collection("everything")
                                .document("details");
                        detailsDocumentFirebase.get().addOnSuccessListener(documentSnapshot -> {


                            String collegeCoordinates = documentSnapshot.getString("college coordinates");

                            postDetails.put("coordinates", "fest");


                            db.collection("All colleges posts").document("fest")
                                    .collection("posts").document(path).set(postDetails).addOnSuccessListener(unused1 -> {


                                if (number_of_tagged != 0) {
                                    if (uidTaggeds != null) {
                                        db.collection("All colleges posts").document("fest")
                                                .collection("college tags").document(path).set(postDetailsTagged).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                editor.putString(AddPostsActivity.COMPLETED_STRING, "done");
                                                editor.putInt(AddPostsActivity.POST_UPLOAD, 3);
                                                editor.putString(AddPostsActivity.PATH_POST, "");
                                                editor.putString(AddPostsActivity.PATH_POST_STORAGE, "");
                                                editor.apply();
                                            }
                                        });
                                    }

                                } else {
                                    editor.putString(AddPostsActivity.COMPLETED_STRING, "done");
                                    editor.putInt(AddPostsActivity.POST_UPLOAD, 3);
                                    editor.putString(AddPostsActivity.PATH_POST, "");
                                    editor.putString(AddPostsActivity.PATH_POST_STORAGE, "");
                                    editor.apply();
                                }


                            });


                        });


                    }





                }).addOnFailureListener(e -> {
                    Toast.makeText(context, "Upload failed", Toast.LENGTH_SHORT).show();

                });


            })).addOnProgressListener(snapshot -> {

                long percentUploaded = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                editor.putString(AddPostsActivity.PROGRESS_STRING, percentUploaded + "% uploaded");
                editor.apply();


//                Toast.makeText(context, "uploaded " + (int)percentUploaded + "%", Toast.LENGTH_SHORT).show();


            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.success();
    }

    public String getExtension() {
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(context.getContentResolver().getType(Uri.parse(postUri)));
    }


}