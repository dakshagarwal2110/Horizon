package com.collegemeet.android.revafest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileFullSizeImageUser extends AppCompatActivity {

    private String uid;
    private String path, delPathStorage;
    private ImageView likeIv, deleteDaksh, deleteUser , report , reportIcon;
    private TextView likesCountTv;
    private StorageReference mRef;
    private DatabaseReference likereference;
    private boolean testclick = false;



    ProgressBar progressBar;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    ScrollView scrollView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_full_size_image_user);
        mRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        firestore = FirebaseFirestore.getInstance();
        deleteUser = findViewById(R.id.delete_post_profile_user);
        scrollView = findViewById(R.id.scroll_profile_post_view);
        report = findViewById(R.id.report_post_profile_searched);
        reportIcon = findViewById(R.id.report_post_profile_searched_icon);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.black));
        }




        TextView nameTv = findViewById(R.id.post_feed_single_name_gridp);
        ImageView profileIv = findViewById(R.id.post_feed_user_profile_image_gridp);
        likeIv = findViewById(R.id.liked_post_feed_gridp);
        TextView captionTv = findViewById(R.id.caption_post_feed_gridp);
        likesCountTv = findViewById(R.id.post_likes_count_feedap);
        TextView dateTv = findViewById(R.id.date_post_feed_gridp);
        ImageView postIv = findViewById(R.id.image_feed_single_id_gridp);




        if (getIntent() != null) {

            String name = getIntent().getExtras().getString("name");
            String profileImage = getIntent().getExtras().getString("profileImage");
            path = getIntent().getExtras().getString("path");
            String caption = getIntent().getExtras().getString("caption");
            String postImage = getIntent().getExtras().getString("postImage");
            String date = getIntent().getExtras().getString("date");
            uid = getIntent().getExtras().getString("uid");
            String type = getIntent().getExtras().getString("type");
            String coordinates = getIntent().getExtras().getString("coor");//get coordinates
            if (type.equals("photo")) {
                delPathStorage = "Posts/" + path;

            } else {
                delPathStorage = "videos/" + path;
            }


            if (mAuth.getCurrentUser() != null) {
                if (mAuth.getCurrentUser().getUid().equals(uid) || mAuth.getCurrentUser().getUid().equals("9qrUgMd2Y6UFmrQvLb99VGruz9Y2")) {
                    deleteUser.setVisibility(View.VISIBLE);
                } else {
                    deleteUser.setVisibility(View.GONE);
                }
            }
            deleteUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ProgressDialog progressDialog = new ProgressDialog(ProfileFullSizeImageUser.this);
                    progressDialog.setTitle("Are you sure you want to delete this vlog?");
                    progressDialog.setProgressStyle(1);
                    progressDialog.setProgress(0);
                    progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progressDialog.dismiss();//dismiss dialog
                        }
                    });


                    progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            mRef.child(delPathStorage).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {


                                    firestore.collection("All colleges posts").document(coordinates).collection("posts")
                                            .document(path).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("deleteCheck", "deleted from college");
                                            firestore.collection("users").document(uid).collection("everything")
                                                    .document("post").collection("All post").document(path).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.d("deleteCheck", "deleted user");
                                                    progressDialog.dismiss();
                                                    Snackbar.make(scrollView, "Post is deleted , refresh your feed to view changes.", Snackbar.LENGTH_LONG).show();

                                                }
                                            });

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            firestore.collection("users").document(uid).collection("everything")
                                                    .document("post").collection("All post").document(path).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.d("deleteCheck", "deleted user");
                                                    Snackbar.make(scrollView, "Post is deleted , refresh your feed to view changes.", Snackbar.LENGTH_LONG).show();
                                                    progressDialog.dismiss();

                                                }
                                            });
                                        }
                                    });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    firestore.collection("All colleges posts").document(coordinates).collection("posts")
                                            .document(path).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("deleteCheck", "deleted from college");
                                            firestore.collection("users").document(uid).collection("everything")
                                                    .document("post").collection("All post").document(path).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.d("deleteCheck", "deleted user");
                                                    Snackbar.make(scrollView, "Post is deleted , refresh your feed to view changes.", Snackbar.LENGTH_LONG).show();

                                                }
                                            });

                                        }
                                    });
                                }
                            });

                        }
                    });


                    progressDialog.setCancelable(false);
                    progressDialog.show();

                }
            });


            report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (reportIcon.getVisibility() == View.GONE) {
                        reportIcon.setVisibility(View.VISIBLE);
//                    Log.d("report_detail" , list.get(position).getUid());
//                    Log.d("report_detail" , list.get(position).getCoordinates());
                    } else {
                        reportIcon.setVisibility(View.GONE);

                    }
                }
            });


            nameTv.setText(name);
            captionTv.setText(caption);
            dateTv.setText(date);

            Glide.with(this)
                    .load(Uri.parse(profileImage))

                    .into(profileIv);


            if (type.equals("photo")) {
                postIv.setVisibility(View.VISIBLE);

                Glide.with(this)
                        .load(Uri.parse(postImage))

                        .into(postIv);


            } else {


            }
        }

    }

    private void deletePost(String path, String uid, String coordinates, String delPathStorage) {


        Log.d("deleteCheck", "path is :" + path);
        Log.d("deleteCheck", "uid is :" + uid);
        Log.d("deleteCheck", "coor is :" + coordinates);
        Log.d("deleteCheck", "stor is :" + delPathStorage);

        firestore.collection("users").document(uid).collection("everything")
                .document("post").collection("All post").document(path).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("deleteCheck", "deleted user");
                firestore.collection("All colleges posts").document(coordinates).collection("posts")
                        .document(path).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("deleteCheck", "deleted from college");
                        mRef.child(delPathStorage).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("deleteCheck", "deleted from storage");
                            }
                        });

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                firestore.collection("All colleges posts").document(coordinates).collection("posts")
                        .document(path).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("deleteCheck", "deleted from college");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mRef.child(delPathStorage).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("deleteCheck", "deleted from storage");
                            }
                        });
                    }
                });
            }
        });


    }

    public void getlikebuttonstatus(final String postkey, final String userid) {


        likereference = FirebaseDatabase.getInstance().getReference("likes").child("posts");
        likereference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postkey).hasChild(userid)) {
                    int likecoun = (int) snapshot.child(postkey).getChildrenCount();

                    String likesString = likecoun + " likes";
                    likesCountTv.setText(likesString);
                    likeIv.setImageResource(R.drawable.ic_liked);
                } else {
                    int likecoun = (int) snapshot.child(postkey).getChildrenCount();
                    String likesString = likecoun + " likes";
                    likesCountTv.setText(likesString);
                    likeIv.setImageResource(R.drawable.ic_unliked);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();



    }


}