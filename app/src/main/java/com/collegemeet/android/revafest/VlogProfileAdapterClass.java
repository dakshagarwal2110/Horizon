package com.collegemeet.android.revafest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class VlogProfileAdapterClass extends RecyclerView.Adapter<VlogProfileAdapterClass.VlogViewHolder> {


    private ArrayList<VlogFetchDetailsClass> list;
    private Context context;
    public static final String TAG = "MyTag";

    private FirebaseAuth mAuth;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    FirebaseFirestore firestore;



    public VlogProfileAdapterClass(Context context, ArrayList<VlogFetchDetailsClass> list) {

        this.context = context;
        this.list = list;
        mAuth = FirebaseAuth.getInstance();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        firestore = FirebaseFirestore.getInstance();



    }

    @NonNull
    @Override
    public VlogProfileAdapterClass.VlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_events_single_look, parent, false);
        return new VlogProfileAdapterClass.VlogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VlogProfileAdapterClass.VlogViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String image = list.get(position).getImage();

        String event_name = list.get(position).getEventName();




        //coordinates has postUserUid


//        holder.imagePost.setImageDrawable(context.getDrawable(Integer.parseInt(image)));
//        holder.imagePost.setImageResource(R.drawable.x);

        Glide.with(context)
                .load(Uri.parse(image))
                .error(R.drawable.loading_image)
                .into(holder.imagePost);


        holder.caption.setText(event_name);






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

    public class VlogViewHolder extends RecyclerView.ViewHolder {

        private ImageView imagePost;
        private TextView caption;

        public VlogViewHolder(@NonNull View itemView) {
            super(itemView);

            caption = itemView.findViewById(R.id.caption_event);
            imagePost = itemView.findViewById(R.id.event_feed_single_id);

        }


    }
}