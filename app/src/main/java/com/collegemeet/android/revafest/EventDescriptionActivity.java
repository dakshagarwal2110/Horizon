package com.collegemeet.android.revafest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class EventDescriptionActivity extends AppCompatActivity {

    ImageView eventImage;
    TextView eventName , eventDescription;
    Button regPay , regFree;
    String pay_free;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_description);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.black));
        }

        eventImage = findViewById(R.id.event_image);
        eventDescription = findViewById(R.id.event_description);
        eventName = findViewById(R.id.event_name);
        regPay = findViewById(R.id.Registerpaid);
        regFree = findViewById(R.id.Registerfree);

        if(getIntent() != null){
            int image = getIntent().getExtras().getInt("event_image");

            eventImage.setImageDrawable(getDrawable(image));
            eventDescription.setText(getIntent().getExtras().getString("event_description"));
            eventName.setText(getIntent().getExtras().getString("event_name"));


            if(eventName.getText().toString().equals("Tomorrow's Tesla") || eventName.getText().toString().equals("Mighty Manoeuvre") ||
                    eventName.getText().toString().equals("ROBOWARS") || eventName.getText().toString().equals("XCELLARADO")){
                regFree.setVisibility(View.GONE);
                regPay.setVisibility(View.VISIBLE);
                pay_free = "1";

            }else {
                regFree.setVisibility(View.VISIBLE);
                regPay.setVisibility(View.GONE);
                pay_free = "2";
            }

            regFree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    Intent intent = new Intent(EventDescriptionActivity.this , RegisterActivity.class);
//
                    Bundle bundle = new Bundle();
                    bundle.putString("event_namee" , eventName.getText().toString());
//
                    bundle.putInt("event_imagee" , image);
                    bundle.putString("pay_free" , pay_free);
                    Log.d("ohoo" , eventName.getText().toString());
                    Log.d("ohoo" , image + "");
                    intent.putExtras(bundle);

                    startActivity(intent);


                }
            });

            regPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    Intent intent = new Intent(EventDescriptionActivity.this , RegisterActivity.class);
//
                    Bundle bundle = new Bundle();
                    bundle.putString("event_namee" , eventName.getText().toString());
//
                    bundle.putInt("event_imagee" , image);
                    bundle.putString("pay_free" , pay_free);
                    Log.d("ohoo" , eventName.getText().toString());
                    Log.d("ohoo" , image + "");
                    intent.putExtras(bundle);

                    startActivity(intent);


                }
            });


        }



    }
}