package com.collegemeet.android.revafest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class grid_profile_adapter_class extends RecyclerView.Adapter {


    private Context context;
    private ArrayList<PostFeedDetailsClass> list;
    public static final String TAG = "MyTag";
    public static final int videoGrid=1;
    public static final int imageGrid=0;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;


    public grid_profile_adapter_class(Context context, ArrayList<PostFeedDetailsClass> list) {

        this.context = context;
        this.list = list;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == videoGrid){
            View view = LayoutInflater.from(context).inflate(R.layout.profile_image_grid_view_single_item, parent, false);
            return new VideoHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.profile_image_grid_view_single_item, parent, false);
            return new ImageHolder(view);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        String postImageUri = list.get(position).getImageUri();
        String frameImageUri = list.get(position).getFrameUri();

        if(holder.getClass() == VideoHolder.class){


            if(frameImageUri != null){
                Glide.with(context)

                        .load(Uri.parse(frameImageUri))


                        .into(((VideoHolder)holder).videoPostImage);
            }else {
                Glide.with(context)

                        .load(Uri.parse(postImageUri))


                        .into(((VideoHolder)holder).videoPostImage);
            }






            holder.itemView.setOnClickListener(v -> {



                Intent intent = new Intent(context , profileFullSizeImage.class);
                Bundle bundle = new Bundle();

                bundle.putString("profileImage",list.get(position).getProfileImage());
                bundle.putString("name",list.get(position).getName());
                bundle.putString("path",list.get(position).getPath());
                bundle.putString("caption",list.get(position).getCaption());
                bundle.putString("postImage",list.get(position).getImageUri());
                bundle.putString("date",list.get(position).getDate());
                bundle.putString("uid",list.get(position).getUid());
                bundle.putString("type",list.get(position).getCoordinates());
                Log.d("deleteCheck" , list.get(position).getPostUserUid());
                bundle.putString("coor",list.get(position).getPostUserUid());


                intent.putExtras(bundle);
                context.startActivity(intent);

            });

        }else{
            Glide.with(context)
                    .load(Uri.parse(postImageUri))


                    .into(((ImageHolder)holder).postImage);

            holder.itemView.setOnClickListener(v -> {

                Intent intent = new Intent(context , ProfileFullSizeImageUser.class);
                Bundle bundle = new Bundle();

                bundle.putString("profileImage",list.get(position).getProfileImage());
                bundle.putString("name",list.get(position).getName());
                bundle.putString("path",list.get(position).getPath());
                bundle.putString("caption",list.get(position).getCaption());
                bundle.putString("postImage",list.get(position).getImageUri());
                bundle.putString("date",list.get(position).getDate());
                bundle.putString("uid",list.get(position).getUid());
                bundle.putString("type",list.get(position).getCoordinates());
                bundle.putString("coor",list.get(position).getPostUserUid());


                intent.putExtras(bundle);
                context.startActivity(intent);

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
        if(list.get(position).getCoordinates().equals("photo")){
            return imageGrid;
        }else{
            return videoGrid;
        }

    }

    public class ImageHolder extends RecyclerView.ViewHolder {

        ImageView postImage;

        public ImageHolder(@NonNull View itemView) {
            super(itemView);

            postImage = itemView.findViewById(R.id.image_grid_view);

        }


    }
    public class VideoHolder extends RecyclerView.ViewHolder {

        ImageView videoPostImage;

        public VideoHolder(@NonNull View itemView) {
            super(itemView);

            videoPostImage = itemView.findViewById(R.id.image_grid_view);

        }


    }
}