package com.collegemeet.android.revafest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    TextView alreadyUser;
    String rollno, password, confirmPassword, name, email , year;
    Button signIn;

    EditText emailEt, passwordEt, confirmPasswordEt, nameEt , rollNoEt , yearEt;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private final String REVA_FEST_ID = "zealIds";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //ek kaam kar agar verification link send na ho toh jab woh login kara toh dobara send hojai.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.black));
        }

        emailEt = findViewById(R.id.email_signup_et);
        passwordEt = findViewById(R.id.pass_signup_ets);
        confirmPasswordEt = findViewById(R.id.cnf_pass_signup_ets);
        nameEt = findViewById(R.id.name_signup_ets);
        alreadyUser = findViewById(R.id.already_user);
        rollNoEt = findViewById(R.id.rollno_signup_ets);
        yearEt = findViewById(R.id.yaer_signup_ets);

        progressBar = findViewById(R.id.signupProgress_bar);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        signIn = findViewById(R.id.button_signUp);


        alreadyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                hideSoftKeyBoard(view);

                if(isNetworkAvailable(SignUpActivity.this)){

                    if (validateName() && validateEmail() && validatePassword() && validateConfirmPassword() && validateYear() && validateRollno()) {


                        progressBar.setVisibility(View.VISIBLE);
                        signIn.setVisibility(View.GONE);
                        Toast.makeText(SignUpActivity.this, "fine", Toast.LENGTH_SHORT).show();



                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {

                                    Toast.makeText(SignUpActivity.this, "Account created please verify via email also check spam", Toast.LENGTH_SHORT).show();
                                    if (mAuth.getCurrentUser() != null) {


                                        Map<Object, String> user = new HashMap<>();

                                        String eventId = name.substring(0,3) + email.substring(0,3);
                                        user.put("name", name);
                                        user.put("email", email);
                                        user.put("profile image", "https://firebasestorage.googleapis.com/v0/b/myfirebaseapp-73558.appspot.com/o/user_no_profile_image.png?alt=media&token=c6ab381a-ab3c-48e1-a4fa-995e5eeb385e");
                                        user.put("rollNo", rollno);
                                        user.put("year_branch" , year);
                                        user.put("eventId" , eventId);

                                        FirebaseUser userD = mAuth.getCurrentUser();


                                        db.collection("users").document(mAuth.getCurrentUser().getUid())
                                                .collection("everything").document("details").set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {


                                                Map<Object, String> usernew = new HashMap<>();
                                                usernew.put("event_id" , eventId);
                                                usernew.put("uid" , userD.getUid());


                                                db.collection(REVA_FEST_ID).document(eventId).set(usernew).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        progressBar.setVisibility(View.GONE);
                                                        signIn.setVisibility(View.VISIBLE);
                                                        sendVerificationCode();
                                                    }
                                                });





                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressBar.setVisibility(View.GONE);
                                                signIn.setVisibility(View.VISIBLE);
                                                Toast.makeText(SignUpActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }

                                }else {
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                        progressBar.setVisibility(View.GONE);
                                        signIn.setVisibility(View.VISIBLE);
                                        emailEt.setError("Email already in use, just login");
                                    } else {
                                        progressBar.setVisibility(View.GONE);
                                        signIn.setVisibility(View.VISIBLE);
                                        Toast.makeText(SignUpActivity.this, "Error while Signing in", Toast.LENGTH_SHORT).show();
                                    }
                                }


                            }
                        });


                    }else {
                        Toast.makeText(SignUpActivity.this, "failed", Toast.LENGTH_SHORT).show();
                    }


                }

            }
        });


    }


    private boolean validateEmail() {
        email = emailEt.getText().toString().trim();
        if (email.isEmpty()) {
            emailEt.setError("Field can't be empty");
            return false;
        } else if (email.startsWith("/") || email.startsWith(".")) {
            emailEt.setError("email cannot start with / or .");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEt.setError("Enter valid e-mail");
            return false;
        } else {
            emailEt.setError(null);
            return true;
        }
    }



    private boolean validateName() {

        name = nameEt.getText().toString().trim();
        if (name.isEmpty()) {
            nameEt.setError("Field can't be empty");
            return false;
        } else if (name.startsWith("/") || name.startsWith(".")) {
            nameEt.setError("name cannot start with / or .");
            return false;
        } else {
            nameEt.setError(null);
            return true;
        }
    }

    private boolean validateRollno() {

        rollno = rollNoEt.getText().toString().trim();
        if (rollno.isEmpty()) {
            rollNoEt.setError("Field can't be empty");
            return false;
        } else if (rollno.startsWith("/") || rollno.startsWith(".")) {
            rollNoEt.setError("name cannot start with / or .");
            return false;
        } else {
            rollNoEt.setError(null);
            return true;
        }
    }
    private boolean validateYear() {

        year = yearEt.getText().toString().trim();
        if (year.isEmpty()) {
            yearEt.setError("Field can't be empty");
            return false;
        } else if (year.startsWith("/") || year.startsWith(".")) {
            yearEt.setError("name cannot start with / or .");
            return false;
        } else {
            yearEt.setError(null);
            return true;
        }
    }


    private boolean validatePassword() {
        password = passwordEt.getText().toString();
        if (password.isEmpty()) {
            passwordEt.setError("Field can't be empty");
            return false;
        } else if (password.length() < 7) {
            passwordEt.setError("Password too short");
            return false;
        } else {
            passwordEt.setError(null);
            return true;
        }
    }

    private boolean validateConfirmPassword() {
        confirmPassword = confirmPasswordEt.getText().toString();
        if (!confirmPassword.equals(password)) {
            confirmPasswordEt.setError("Password and confirm password don't match");
            return false;
        } else {
            confirmPasswordEt.setError(null);
            return true;
        }
    }



    public Boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network nw = connectivityManager.getActiveNetwork();
            if (nw == null) {
                return false;
            }
            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
            return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
        } else {
            NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
            return nwInfo != null && nwInfo.isConnected();
        }
    }
    private void hideSoftKeyBoard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void sendVerificationCode() {

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(this , LoginActivity.class);
                    startActivity(intent);
                    mAuth.signOut();
                    Toast.makeText(this, "Verification link send on E-mail please check you spam messages", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    signIn.setVisibility(View.VISIBLE);
                    finish();
                } else {
                    progressBar.setVisibility(View.GONE);
                    signIn.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Error in sending link!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


}