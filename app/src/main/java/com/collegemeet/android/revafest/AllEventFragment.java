package com.collegemeet.android.revafest;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class AllEventFragment extends Fragment {

    CardView tesla,mighty,treasure,cahoot,terrain,smackdown,robowar,robosoccor,xcellardo;
    TextView teslaN,mightyN,treasureN,cahootN,terrainN,smackdownN,robowarN,robosoccorN,xcellardoN;
    TextView teslaD,mightyD,treasureD,cahootD,terrainD,smackdownD,robowarD,robosoccorD,xcellardoD;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_event, container, false);


        tesla = view.findViewById(R.id.tomTesla); teslaN = view.findViewById(R.id.tomTeslaN); teslaD = view.findViewById(R.id.tomTeslaD);
        mighty = view.findViewById(R.id.migMan); mightyN = view.findViewById(R.id.migManN); mightyD = view.findViewById(R.id.migManD);
        treasure = view.findViewById(R.id.treasureq); treasureN = view.findViewById(R.id.treasureqN); treasureD = view.findViewById(R.id.treasureqD);
        cahoot = view.findViewById(R.id.cahoot); cahootN = view.findViewById(R.id.cahootN); cahootD = view.findViewById(R.id.cahootD);
        terrain = view.findViewById(R.id.terrainTraker); terrainN = view.findViewById(R.id.terrainTrakerN); terrainD = view.findViewById(R.id.terrainTrakerD);
        smackdown = view.findViewById(R.id.smackdown); smackdownN = view.findViewById(R.id.smackdownN); smackdownD = view.findViewById(R.id.smackdownD);
        robowar = view.findViewById(R.id.robowar); robowarN = view.findViewById(R.id.robowarN); robowarD = view.findViewById(R.id.robowarD);
        robosoccor = view.findViewById(R.id.robosoccer); robosoccorN = view.findViewById(R.id.robosoccerN); robosoccorD = view.findViewById(R.id.robosoccerD);
        xcellardo = view.findViewById(R.id.xcellerado); xcellardoN = view.findViewById(R.id.xcelleradoN); xcellardoD = view.findViewById(R.id.xcelleradoD);

        tesla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext() , EventDescriptionActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("event_name" , teslaN.getText().toString());
                bundle.putString("event_description" , teslaD.getText().toString());
                bundle.putInt("event_image" , R.drawable.tomorowt);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        mighty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext() , EventDescriptionActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("event_name" , mightyN.getText().toString());
                bundle.putString("event_description" , mightyD.getText().toString());
                bundle.putInt("event_image" , R.drawable.mightym);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        treasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext() , EventDescriptionActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("event_name" , treasureN.getText().toString());
                bundle.putString("event_description" , treasureD.getText().toString());
                bundle.putInt("event_image" , R.drawable.treasureq);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        cahoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext() , EventDescriptionActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("event_name" , cahootN.getText().toString());
                bundle.putString("event_description" , cahootD.getText().toString());
                bundle.putInt("event_image" , R.drawable.cahoot);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        terrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext() , EventDescriptionActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("event_name" , terrainN.getText().toString());
                bundle.putString("event_description" , terrainD.getText().toString());
                bundle.putInt("event_image" , R.drawable.terrain);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        smackdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext() , EventDescriptionActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("event_name" , smackdownN.getText().toString());
                bundle.putString("event_description" , smackdownD.getText().toString());
                bundle.putInt("event_image" , R.drawable.smackdown);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        robowar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext() , EventDescriptionActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("event_name" , robowarN.getText().toString());
                bundle.putString("event_description" , robowarD.getText().toString());
                bundle.putInt("event_image" , R.drawable.robowar);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        robosoccor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext() , EventDescriptionActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("event_name" , robosoccorN.getText().toString());
                bundle.putString("event_description" , robosoccorD.getText().toString());
                bundle.putInt("event_image" , R.drawable.robosoccer);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        xcellardo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext() , EventDescriptionActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("event_name" , xcellardoN.getText().toString());
                bundle.putString("event_description" , xcellardoD.getText().toString());
                bundle.putInt("event_image" , R.drawable.xeralado);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


        return view;
    }
}