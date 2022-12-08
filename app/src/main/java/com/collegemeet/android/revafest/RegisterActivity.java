package com.collegemeet.android.revafest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    ImageView eventImage;
    TextView eventNamee;
    Button inviteSearch, pay_invite, free_invite, free_indivisual, pay_indivisual;
    LinearLayout layout;
    EditText zealId1, zealId2, zealId3, zealId4;
    SharedPreferences preferences;
    FirebaseFirestore firestore;
    int image;
    String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        eventImage = findViewById(R.id.event_image_reg);
        eventNamee = findViewById(R.id.event_name_reg);
        inviteSearch = findViewById(R.id.inviteTeamMembers);
        layout = findViewById(R.id.card_team_invite);
        zealId1 = findViewById(R.id.z1);
        zealId2 = findViewById(R.id.z2);
        zealId3 = findViewById(R.id.z3);
        zealId4 = findViewById(R.id.z4);
        pay_invite = findViewById(R.id.RegisterpaidInvite);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        firestore = FirebaseFirestore.getInstance();
        free_invite = findViewById(R.id.RegisterfreeInvite);
        free_indivisual = findViewById(R.id.Register_indivisually_free);
        pay_indivisual = findViewById(R.id.Register_indivisually_pay);





        if (getIntent() != null) {
            image = getIntent().getExtras().getInt("event_imagee");
            String eventName = getIntent().getExtras().getString("event_namee");
            String pay_free = getIntent().getExtras().getString("pay_free");


            eventImage.setImageDrawable(getDrawable(image));

            eventNamee.setText(eventName);

            if(eventName.equals("Tomorrow's Tesla")){
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/elseif-98c56.appspot.com/o/WhatsApp%20Image%202022-09-24%20at%2010.55.00%20PM.jpeg?alt=media&token=e704dbb8-f4ca-4d1c-aad1-c431281d8dc3";
            }else if(eventName.equals("Mighty Manoeuvre")){
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/elseif-98c56.appspot.com/o/WhatsApp%20Image%202022-09-24%20at%2010.55.14%20PM.jpeg?alt=media&token=337f0209-240b-4aa2-bf41-8a82d2f26e6d";
            }else if(eventName.equals("TREASUREQ")){
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/elseif-98c56.appspot.com/o/WhatsApp%20Image%202022-09-24%20at%2010.56.01%20PM%20-%20Copy.jpeg?alt=media&token=adef039a-55a6-43db-8795-95d3a2d0e642";
            }else if(eventName.equals("Terrain Tracker")){
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/elseif-98c56.appspot.com/o/WhatsApp%20Image%202022-09-24%20at%207.04.43%20PM.jpeg?alt=media&token=072ba8a4-6655-40a5-922d-5038fb1cf192";
            }else if(eventName.equals("SMACKDOWN")){
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/elseif-98c56.appspot.com/o/WhatsApp%20Image%202022-09-24%20at%207.05.15%20PM.jpeg?alt=media&token=7010696a-9887-44e0-8303-8a2e8332acc1";
            }else if(eventName.equals("ROBOSOCCER")){
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/elseif-98c56.appspot.com/o/WhatsApp%20Image%202022-09-24%20at%207.07.04%20PM.jpeg?alt=media&token=c2fc0f96-ec55-4ba7-83b0-712a8fb3df01";
            }else if(eventName.equals("XCELLARADO")){
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/elseif-98c56.appspot.com/o/WhatsApp%20Image%202022-09-24%20at%207.07.24%20PM.jpeg?alt=media&token=9a6e3677-33e4-4249-859d-ec8fc1c35910";
            }else if(eventName.equals("ROBOWARS")){
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/elseif-98c56.appspot.com/o/WhatsApp%20Image%202022-09-24%20at%207.07.46%20PM.jpeg?alt=media&token=24eefc96-7754-48e8-a0f1-3409382c0739";
            }else if(eventName.equals("CAHOOT")){
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/elseif-98c56.appspot.com/o/WhatsApp%20Image%202022-09-24%20at%207.08.49%20PM.jpeg?alt=media&token=651f66d9-6808-45a4-9686-cb03aa07ef60";
            }
            if (eventName.equals("Mighty Manoeuvre") || eventName.equals("TREASUREQ") || eventName.equals("ROBOSOCCER") ||
                    eventName.equals("XCELLARADO")) {


                inviteSearch.setVisibility(View.VISIBLE);
                but(pay_free);

            } else {
                inviteSearch.setVisibility(View.GONE);
                buta(pay_free);
            }

            inviteSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    layout.setVisibility(View.VISIBLE);
                    zealId1.setText(preferences.getString(MainActivityContainingFragment.ZEAL_ID, ""));

                }
            });
            pay_invite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    firestore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("myEvents")
                            .document(eventName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.getResult().exists()) {
                                Toast.makeText(RegisterActivity.this, "Already registered", Toast.LENGTH_SHORT).show();
                                pay_invite.setText("Already registered");

                            } else {

                                firestore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .collection("paids").document(eventName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.getResult().exists()) {

                                            pay_invite.setText("Sending invites");
                                            send_invites();

                                        } else {
                                            pay_invite.setText("PAY");
                                            Intent intent = new Intent(RegisterActivity.this, PayActivity.class);
                                            intent.putExtra("event_name", eventName);
                                            startActivity(intent);
                                        }
                                    }
                                });

                            }

                        }
                    });
                }
            });

            free_invite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    firestore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("myEvents")
                            .document(eventName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.getResult().exists()) {
                                Toast.makeText(RegisterActivity.this, "Already registered", Toast.LENGTH_SHORT).show();
                                free_invite.setText("already registered");
                            } else {
                                send_invites();
                            }

                        }
                    });
                }
            });


            free_indivisual.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    firestore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("myEvents")
                            .document(eventName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.getResult().exists()) {
                                Toast.makeText(RegisterActivity.this, "Already registered", Toast.LENGTH_SHORT).show();
                            } else {
                                add_event();
                            }

                        }
                    });
                }
            });


            pay_indivisual.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    firestore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("myEvents")
                            .document(eventName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.getResult().exists()) {
                                Toast.makeText(RegisterActivity.this, "Already registered", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, "pay and register", Toast.LENGTH_SHORT).show();
                                firestore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .collection("paids").document(eventName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.getResult().exists()) {
                                            add_event();

                                        } else {
                                            pay_invite.setText("PAY");
                                            Intent intent = new Intent(RegisterActivity.this, PayActivity.class);
                                            intent.putExtra("event_name", eventName);
                                            startActivity(intent);
                                        }
                                    }
                                });
                            }

                        }
                    });
                }
            });


        }


    }

    private void add_event() {

        HashMap<Object, String> map = new HashMap<>();
        map.put("requested_user", FirebaseAuth.getInstance().getCurrentUser().getUid());
        map.put("eventName", eventNamee.getText().toString());
        map.put("eventImage", imageUrl);
        firestore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("myEvents").document(eventNamee.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {

                    Toast.makeText(RegisterActivity.this, "You have already registered for this event", Toast.LENGTH_SHORT).show();

                } else {
                    firestore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .collection("myEvents").document(eventNamee.getText().toString()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(RegisterActivity.this, "Yaay! Registered", Toast.LENGTH_SHORT).show();
                            free_indivisual.setText("Yaay! Registered");

                        }
                    });
                }
            }
        });

    }

    private void buta(String pay_free) {

        if (pay_free.equals("1")) {
            pay_indivisual.setVisibility(View.VISIBLE);
            free_indivisual.setVisibility(View.GONE);
        } else {
            pay_indivisual.setVisibility(View.GONE);
            free_indivisual.setVisibility(View.VISIBLE);
        }

    }

    private void send_invites() {

        TextView x;

        for (int i = 1; i < 5; i++) {

            if (i == 1) {
                x = zealId1;
            } else if (i == 2) {
                x = zealId2;
            } else if (i == 3) {
                x = zealId3;
            } else if (i == 4) {
                x = zealId4;
            } else {
                x = zealId1;
            }


            int L = i;
            firestore.collection("zealIds").document(x.getText().toString()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Log.d("SASAAS", documentSnapshot.getString("uid"));

                    HashMap<Object, String> map = new HashMap<>();
                    map.put("requested_user", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    map.put("eventName", eventNamee.getText().toString());
                    map.put("eventImage", imageUrl);


                    firestore.collection("users").document(documentSnapshot.getString("uid")).collection("notifications")
                            .document(eventNamee.getText().toString()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(RegisterActivity.this, "User " + L + " invitation send", Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(RegisterActivity.this, "Zeal id doesn't exist", Toast.LENGTH_SHORT).show();
                }
            });
        }


        //here

        HashMap<Object, String> map = new HashMap<>();
        map.put("requested_user", FirebaseAuth.getInstance().getCurrentUser().getUid());
        map.put("eventName", eventNamee.getText().toString());
        map.put("eventImage", imageUrl);
        firestore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("myEvents").document(eventNamee.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {

                    Toast.makeText(RegisterActivity.this, "You have already registered for this event", Toast.LENGTH_SHORT).show();

                } else {
                    firestore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .collection("myEvents").document(eventNamee.getText().toString()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(RegisterActivity.this, "Yaay! Registered and invites sent", Toast.LENGTH_SHORT).show();
                            free_invite.setText("Yaay! Registered");

                        }
                    });
                }
            }
        });


    }

    private void but(String pay_free) {
        if (pay_free.equals("1")) {
            pay_invite.setVisibility(View.VISIBLE);
            free_invite.setVisibility(View.GONE);
        } else {
            pay_invite.setVisibility(View.GONE);
            free_invite.setVisibility(View.VISIBLE);
        }
    }
}