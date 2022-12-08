package com.collegemeet.android.revafest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class PostFeedAdapterClass extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<PostFeedDetailsClass> list;
    private DatabaseReference likereference;
    Boolean testclick = false, full = true;
    public static final String TAG = "MyTag";
    public static final int videoGrid = 1;
    public static final int imageGrid = 0;
    private FirebaseFirestore firestore;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;



    public PostFeedAdapterClass(Context context, ArrayList<PostFeedDetailsClass> list) {

        this.context = context;
        this.list = list;
        firestore = FirebaseFirestore.getInstance();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == videoGrid) {
            View view = LayoutInflater.from(context).inflate(R.layout.post_single_item_look, parent, false);
            return new VideoHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.post_single_item_look, parent, false);
            return new Holder(view);
        }


    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {


        if (holder.getClass() == PostFeedAdapterClass.VideoHolder.class) {
            //latter


            Log.d("ass" , "video");


        } else {


            ((Holder) holder).name.setText(list.get(position).getName());
            ((Holder) holder).nameCaption.setText(list.get(position).getName());
            ((Holder) holder).caption.setText(list.get(position).getCaption());
            ((Holder) holder).date.setText(list.get(position).getDate());
            String profileImageUri = list.get(position).getProfileImage();
            String postImageUri = list.get(position).getImageUri();
            String path = list.get(position).getPath();
            String uid = list.get(position).getUid();


            likereference = FirebaseDatabase.getInstance().getReference("likes").child("posts");



            Glide.with(context)
                    .load(Uri.parse(profileImageUri))
                    .placeholder(R.drawable.man)
                    .error(R.drawable.man)
                    .into(((Holder) holder).profileImage);

            Glide.with(context)
                    .load(Uri.parse(postImageUri))
                    .error(R.drawable.loading_image)
                    .into(((Holder) holder).postImage);


            ((Holder) holder).postImage.setOnTouchListener(new View.OnTouchListener() {

                GestureDetector gestureDetector = new GestureDetector(context.getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onDoubleTap(MotionEvent e) {

                        testclick = true;

                        likereference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (testclick) {

                                    if (snapshot.child(path).hasChild(uid)) {

                                        likereference.child(path).child(uid).removeValue();

                                    } else {

                                        likereference.child(path).child(uid).setValue(true);

                                    }
                                    ((Holder) holder).getlikebuttonstatus(path, uid);
                                    testclick = false;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {


                            }
                        });


                        return super.onDoubleTap(e);
                    }


                });

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event);
                    return true;
                }
            });






            ((Holder) holder).getlikebuttonstatus(path, uid);


            ((Holder) holder).likeImage.setOnClickListener(v -> {

                testclick = true;
                likereference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (testclick) {

                            if (snapshot.child(path).hasChild(uid)) {

                                likereference.child(path).child(uid).removeValue();

                            } else {

                                likereference.child(path).child(uid).setValue(true);

                            }
                            ((Holder) holder).getlikebuttonstatus(path, uid);
                            testclick = false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            });


        }
    }




    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (true) {
            return imageGrid;
        } else {
            return videoGrid;
        }

    }

    public class VideoHolder extends RecyclerView.ViewHolder {

        TextView name, likesCount, date, caption, nameCaption;
        ImageView likeImage, profileImage , postImage , tag , report , reportIcon;
        ProgressBar progressBar;

        public VideoHolder(@NonNull View itemView) {
            super(itemView);





        }


        public void getlikebuttonstatus(final String postkey, final String userid) {


            likereference = FirebaseDatabase.getInstance().getReference("likes").child("posts");
            likereference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(postkey).hasChild(userid)) {
                        int likecoun = (int) snapshot.child(postkey).getChildrenCount();

                        String likesString = likecoun + " likes";
                        likesCount.setText(likesString);
                        likeImage.setImageResource(R.drawable.ic_liked);
                    } else {
                        int likecoun = (int) snapshot.child(postkey).getChildrenCount();
                        String likesString = likecoun + " likes";
                        likesCount.setText(likesString);
                        likeImage.setImageResource(R.drawable.ic_unliked);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }


    public class Holder extends RecyclerView.ViewHolder {

        TextView name, likesCount, date, caption, nameCaption;
        ImageView likeImage, postImage, profileImage, fullScreen,tag , report , reportIcon;

        public Holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.post_feed_single_name);
            likesCount = itemView.findViewById(R.id.post_likes_count_feed);
            likeImage = itemView.findViewById(R.id.liked_post_feed);
            postImage = itemView.findViewById(R.id.image_feed_single_id);
            caption = itemView.findViewById(R.id.caption_post_feed);
            date = itemView.findViewById(R.id.date_post_feed);
            profileImage = itemView.findViewById(R.id.post_feed_user_profile_image);
            nameCaption = itemView.findViewById(R.id.name_caption_post_feed);

            reportIcon = itemView.findViewById(R.id.report_post_image_feed_icon);
        }



        public void getlikebuttonstatus(final String postkey, final String userid) {


            likereference = FirebaseDatabase.getInstance().getReference("likes").child("posts");
            likereference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(postkey).hasChild(userid)) {
                        int likecoun = (int) snapshot.child(postkey).getChildrenCount();

                        String likesString = likecoun + " likes";
                        likesCount.setText(likesString);
                        likeImage.setImageResource(R.drawable.ic_liked);
                    } else {
                        int likecoun = (int) snapshot.child(postkey).getChildrenCount();
                        String likesString = likecoun + " likes";
                        likesCount.setText(likesString);
                        likeImage.setImageResource(R.drawable.ic_unliked);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }
}
