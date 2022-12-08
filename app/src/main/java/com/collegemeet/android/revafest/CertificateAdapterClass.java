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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class CertificateAdapterClass extends RecyclerView.Adapter<CertificateAdapterClass.VlogViewHolder> {


    private ArrayList<VlogFetchDetailsClass> list;
    private Context context;
    public static final String TAG = "MyTag";

    private FirebaseAuth mAuth;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    FirebaseFirestore firestore;



    public CertificateAdapterClass(Context context, ArrayList<VlogFetchDetailsClass> list) {

        this.context = context;
        this.list = list;
        mAuth = FirebaseAuth.getInstance();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        firestore = FirebaseFirestore.getInstance();



    }

    @NonNull
    @Override
    public CertificateAdapterClass.VlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.certificate_design, parent, false);
        return new CertificateAdapterClass.VlogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CertificateAdapterClass.VlogViewHolder holder, @SuppressLint("RecyclerView") int position) {

        String event_name = list.get(position).getEventName();




        //coordinates has postUserUid


//        holder.imagePost.setImageDrawable(context.getDrawable(Integer.parseInt(image)));
//        holder.imagePost.setImageResource(R.drawable.x);

        holder.name.setText(preferences.getString(MainActivityContainingFragment.USER_NAME , "Daksh agarwal"));
        holder.event.setText(event_name);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Download started...", Toast.LENGTH_SHORT).show();
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

    public class VlogViewHolder extends RecyclerView.ViewHolder {


        private TextView name , event;
        private ImageView imageView;

        public VlogViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name_cer);
            event = itemView.findViewById(R.id.xxxx);
            imageView = itemView.findViewById(R.id.download_certificate);

        }


    }
}