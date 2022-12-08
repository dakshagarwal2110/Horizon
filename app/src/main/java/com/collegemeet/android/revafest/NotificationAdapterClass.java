package com.collegemeet.android.revafest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class NotificationAdapterClass extends RecyclerView.Adapter<NotificationAdapterClass.NotificationViewHolder> {


    private ArrayList<VlogFetchDetailsClass> list;
    private Context context;
    public static final String TAG = "MyTag";
    private DatabaseReference likereference;
    Boolean testclick = false;
    public static final String PLAY_VLOG_URI_ON_CLICK_KEY = "play_vlog_video_feed";
    public static final String PLAY_VLOG_CAPTION_ON_CLICK_KEY = "play_vlog_video_caption_feed";
    public static final String PLAY_VLOG_NAME_ON_CLICK_KEY = "play_vlog_video_name_feed";
    public static final String PLAY_VLOG_USER_PROFILE_IMAGE_URI_ON_CLICK_KEY = "play_vlog_video_profile_image_feed";
    private FirebaseAuth mAuth;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    FirebaseFirestore firestore;
    private StorageReference mRef;


    public NotificationAdapterClass(Context context, ArrayList<VlogFetchDetailsClass> list) {

        this.context = context;
        this.list = list;
        mAuth = FirebaseAuth.getInstance();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        firestore = FirebaseFirestore.getInstance();
        mRef = FirebaseStorage.getInstance().getReference();


    }

    @NonNull
    @Override
    public NotificationAdapterClass.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_notification_look, parent, false);
        return new NotificationAdapterClass.NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapterClass.NotificationViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String image = list.get(position).getImage();

        String event_name = list.get(position).getEventName();




        //coordinates has postUserUid




//        holder.imagePost.setImageDrawable(context.getDrawable(Integer.parseInt(image)));

        Glide.with(context)
                .load(Uri.parse(image))
                .error(R.drawable.loading_image)
                .into(holder.imagePost);
        holder.eventName.setText(event_name);

        holder.inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String imageUrl;

                if(event_name.equals("Tomorrow's Tesla")){
                    imageUrl = "https://firebasestorage.googleapis.com/v0/b/elseif-98c56.appspot.com/o/WhatsApp%20Image%202022-09-24%20at%2010.55.00%20PM.jpeg?alt=media&token=e704dbb8-f4ca-4d1c-aad1-c431281d8dc3";
                }else if(event_name.equals("Mighty Manoeuvre")){
                    imageUrl = "https://firebasestorage.googleapis.com/v0/b/elseif-98c56.appspot.com/o/WhatsApp%20Image%202022-09-24%20at%2010.55.14%20PM.jpeg?alt=media&token=337f0209-240b-4aa2-bf41-8a82d2f26e6d";
                }else if(event_name.equals("TREASUREQ")){
                    imageUrl = "https://firebasestorage.googleapis.com/v0/b/elseif-98c56.appspot.com/o/WhatsApp%20Image%202022-09-24%20at%2010.56.01%20PM%20-%20Copy.jpeg?alt=media&token=adef039a-55a6-43db-8795-95d3a2d0e642";
                }else if(event_name.equals("Terrain Tracker")){
                    imageUrl = "https://firebasestorage.googleapis.com/v0/b/elseif-98c56.appspot.com/o/WhatsApp%20Image%202022-09-24%20at%207.04.43%20PM.jpeg?alt=media&token=072ba8a4-6655-40a5-922d-5038fb1cf192";
                }else if(event_name.equals("SMACKDOWN")){
                    imageUrl = "https://firebasestorage.googleapis.com/v0/b/elseif-98c56.appspot.com/o/WhatsApp%20Image%202022-09-24%20at%207.05.15%20PM.jpeg?alt=media&token=7010696a-9887-44e0-8303-8a2e8332acc1";
                }else if(event_name.equals("ROBOSOCCER")){
                    imageUrl = "https://firebasestorage.googleapis.com/v0/b/elseif-98c56.appspot.com/o/WhatsApp%20Image%202022-09-24%20at%207.07.04%20PM.jpeg?alt=media&token=c2fc0f96-ec55-4ba7-83b0-712a8fb3df01";
                }else if(event_name.equals("XCELLARADO")){
                    imageUrl = "https://firebasestorage.googleapis.com/v0/b/elseif-98c56.appspot.com/o/WhatsApp%20Image%202022-09-24%20at%207.07.24%20PM.jpeg?alt=media&token=9a6e3677-33e4-4249-859d-ec8fc1c35910";
                }else if(event_name.equals("ROBOWARS")){
                    imageUrl = "https://firebasestorage.googleapis.com/v0/b/elseif-98c56.appspot.com/o/WhatsApp%20Image%202022-09-24%20at%207.07.46%20PM.jpeg?alt=media&token=24eefc96-7754-48e8-a0f1-3409382c0739";
                }else if(event_name.equals("CAHOOT")){
                    imageUrl = "https://firebasestorage.googleapis.com/v0/b/elseif-98c56.appspot.com/o/WhatsApp%20Image%202022-09-24%20at%207.08.49%20PM.jpeg?alt=media&token=651f66d9-6808-45a4-9686-cb03aa07ef60";
                }else {
                    imageUrl = "sasa";
                }

                HashMap<Object, String> map = new HashMap<>();
                map.put("requested_user", FirebaseAuth.getInstance().getCurrentUser().getUid());
                map.put("eventName", event_name);
                map.put("eventImage", imageUrl);




                firestore.collection("users").document(mAuth.getCurrentUser().getUid()).collection("myEvents")
                        .document(event_name).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.getResult().exists()){

                            Toast.makeText(context, "You have already registered for this event", Toast.LENGTH_SHORT).show();
                            holder.inviteButton.setText("Already registered");


                        }else {

                            firestore.collection("users").document(mAuth.getCurrentUser().getUid()).collection("myEvents")
                                    .document(event_name).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show();
                                    holder.inviteButton.setText("Yaay! registered");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "Error occurred", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }

                    }
                });



            }
        });






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
        return position;
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {

        private ImageView imagePost;
        private TextView eventName;
        private Button inviteButton;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            eventName = itemView.findViewById(R.id.not_event_name);
            imagePost = itemView.findViewById(R.id.not_image);
            inviteButton = itemView.findViewById(R.id.not_invite_accept);

        }




    }
}