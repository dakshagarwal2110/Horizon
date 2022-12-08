package com.collegemeet.android.revafest;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class DetailsFetchWorkerClass extends Worker {


    private final Context context;
    private final FirebaseAuth mAuth;
    private final FirebaseFirestore db;
    SharedPreferences.Editor editor;
    SharedPreferences preferences;

    public DetailsFetchWorkerClass(Context context, WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public Result doWork() {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        editor = preferences.edit();
        if(mAuth.getCurrentUser() != null){
            DocumentReference detailsDocumentFirebase = db.collection("users")
                    //here can be problem
                    .document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                    .collection("everything")
                    .document("details");
            detailsDocumentFirebase.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    if(documentSnapshot.getString("year_branch") != null){
                        editor.putString(MainActivityContainingFragment.YEAR, documentSnapshot.getString("year_branch"));
                    }else {
                        editor.putString(MainActivityContainingFragment.YEAR, "Branch/Year");
                    }
                    if(documentSnapshot.getString("eventId") != null){
                        editor.putString(MainActivityContainingFragment.ZEAL_ID, documentSnapshot.getString("eventId"));
                    }
                    if(documentSnapshot.getString("email") != null){
                        editor.putString(MainActivityContainingFragment.EMAIL, documentSnapshot.getString("email"));
                    }
                    if(documentSnapshot.getString("name") != null){
                        editor.putString(MainActivityContainingFragment.USER_NAME, documentSnapshot.getString("name"));
                    }else {
                        editor.putString(MainActivityContainingFragment.USER_NAME, "User");
                    }

                    if(documentSnapshot.getString("profile image") != null){
                        editor.putString(MainActivityContainingFragment.PROFILE_IMAGE, documentSnapshot.getString("profile image"));
                    }else {
                        editor.putString(MainActivityContainingFragment.PROFILE_IMAGE, "profile image");
                    }

                    editor.apply();

                } else {
                    Toast.makeText(context, "Details not found...", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return Result.success();
    }
}