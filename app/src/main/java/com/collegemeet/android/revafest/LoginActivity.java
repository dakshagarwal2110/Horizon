package com.collegemeet.android.revafest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    TextView noAccount;
    EditText emailEt , passwordEt;
    Button loginButton;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    private boolean emailVerificationChecker;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.black));
        }

        noAccount = findViewById(R.id.not_a_user);
        emailEt = findViewById(R.id.email_login);
        loginButton = findViewById(R.id.loginButton);
        passwordEt = findViewById(R.id.password_login);
        mAuth  = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressLogin_login);

        noAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this , SignUpActivity.class);
                startActivity(intent);
            }
        });

//        login button action
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //here login code starts


                hideSoftKeyBoard(view);

                if (isNetworkAvailable(LoginActivity.this)) {
                    String emailEditTextLogin = emailEt.getText().toString().trim();
                    String password = passwordEt.getText().toString();


                    if (validateEmail() && validatePassword()) {



                        progressBar.setVisibility(View.VISIBLE);
                        loginButton.setVisibility(View.GONE);


                        mAuth.signInWithEmailAndPassword(emailEditTextLogin, password).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                isEmailVerifiedMethod();
                            } else {
                                if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                    emailEt.setError("Email not in use");
                                    progressBar.setVisibility(View.GONE);
                                    loginButton.setVisibility(View.VISIBLE);
                                } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    passwordEt.setError("Password incorrect...");
                                    progressBar.setVisibility(View.GONE);
                                    loginButton.setVisibility(View.VISIBLE);
                                } else {
//                                    Snackbar snackbar = Snackbar
//                                            .make(loginLayout, "Network error", Snackbar.LENGTH_LONG);
//                                    snackbar.show();

                                    Toast.makeText(LoginActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    loginButton.setVisibility(View.VISIBLE);
                                }

                            }

                        });

                    }


                } else {
//                    Snackbar snackbar = Snackbar
//                            .make(loginLayout, "Network error", Snackbar.LENGTH_LONG);
//                    snackbar.show();

                    Toast.makeText(LoginActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    loginButton.setVisibility(View.VISIBLE);
                }




            }
        });




    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private boolean validateEmail() {
        String email = emailEt.getText().toString().trim();
        if (email.isEmpty()) {
            emailEt.setError("Field can't be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEt.setError("Enter valid e-mail");
            return false;
        } else {
            emailEt.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String password = passwordEt.getText().toString();
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

    private void hideSoftKeyBoard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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


    private void isEmailVerifiedMethod() {
        user = mAuth.getCurrentUser();
        if (user == null) throw new AssertionError();
        emailVerificationChecker = user.isEmailVerified();
        if (emailVerificationChecker) {
            sendToMainActivity();
            progressBar.setVisibility(View.GONE);
            loginButton.setVisibility(View.VISIBLE);
            finish();
        } else {

            Toast.makeText(LoginActivity.this, "Please verify your account ,link sent to your e-mail please check e-mail spam also", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            loginButton.setVisibility(View.VISIBLE);
            sendVerificationCode();
            mAuth.signOut();

        }
    }

    private void sendVerificationCode() {

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    Toast.makeText(LoginActivity.this, "Verification link sent to your e-mail please check e-mail spam messages also", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(LoginActivity.this, "Error in sending code", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.VISIBLE);
                loginButton.setVisibility(View.GONE);
            });
        } else {
            progressBar.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.GONE);
        }
    }

    private void sendToMainActivity(){

        Intent intent = new Intent(LoginActivity.this, MainActivityContainingFragment.class);
        startActivity(intent);

    }




    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            sendToMainActivity();
            finish();
        }
    }


}