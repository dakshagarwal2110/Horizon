package com.collegemeet.android.revafest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayDeque;
import java.util.Deque;

public class MainActivityContainingFragment extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {



    FirebaseAuth mAuth;
    private Deque<Integer> integerDeque;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Fragment activeFragment;
    public static final String TAG = "MyTago";
    Fragment myEventFragment, allEventFragment, memoriesFragment, notificationFragment , profileFragment;
    BottomNavigationView bottomNavigationView;
    boolean flag = true;

    public static final String USER_NAME = "username";
    public static final String YEAR = "country_name";
    public static final String ZEAL_ID = "lang_name";

    public static final String PROFILE_IMAGE = "prof_image";
    public static final String EMAIL = "city_name";
    public static final String ROLL_NUMBER = "roll_number";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_containing_fragment);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.black));
        }

        mAuth = FirebaseAuth.getInstance();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);


        if(preferences.getString(USER_NAME,"").equals("")
                || preferences.getString(PROFILE_IMAGE,"").equals("") || preferences.getString(YEAR,"").equals("")
                || preferences.getString(EMAIL,"").equals("") || preferences.getString(ROLL_NUMBER,"").equals("")
                || preferences.getString(ZEAL_ID,"").equals("")){

            if(isNetworkAvailable(MainActivityContainingFragment.this)){
                Log.d("checker_daksh" , "fetch called");
                fetchDetails();
            }else {
                Toast.makeText(MainActivityContainingFragment.this, "Network error!", Toast.LENGTH_SHORT).show();
            }
        }
        myEventFragment = new MyEventsFragment();
        allEventFragment = new AllEventFragment();
        memoriesFragment = new MemoriesFragment();
        notificationFragment = new NotificationFragment();
        profileFragment = new ProfileFragment();
        activeFragment = myEventFragment;


        getSupportFragmentManager().beginTransaction().add(R.id.feeds_fragment_container, myEventFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.feeds_fragment_container, allEventFragment).hide(myEventFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.feeds_fragment_container, memoriesFragment).hide(allEventFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.feeds_fragment_container, notificationFragment).hide(memoriesFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.feeds_fragment_container, profileFragment).hide(notificationFragment).commit();


        bottomNavigationView = findViewById(R.id.bottom_navigation);
        integerDeque = new ArrayDeque<>(6);
        integerDeque.push(R.id.ProfileFrag);
        loadFragment(profileFragment);
        bottomNavigationView.setSelectedItemId(R.id.ProfileFrag);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (integerDeque.contains(id)) {
                if (id == R.id.MyEventsFrag) {
                    if (integerDeque.size() != 1) {
                        if (flag) {
                            integerDeque.addFirst(R.id.ProfileFrag);
                            flag = false;
                        }
                    }
                }
                integerDeque.remove(id);
            }
            integerDeque.push(id);
            loadFragment(getFragment(item.getItemId()));
            return true;
        });


    }

    private void fetchDetails() {


        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(DetailsFetchWorkerClass.class)
                .build();


        WorkManager workManager = WorkManager.getInstance(this);

        workManager.enqueue(request);

    }

    private Fragment getFragment(int itemId) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        switch (itemId) {
            case R.id.MyEventsFrag:

                bottomNavigationView.getMenu().getItem(0).setChecked(true);
                editor.putString("active_fragment", "0");
                editor.apply();
                return myEventFragment;

            case R.id.AllEventsFrag:

                bottomNavigationView.getMenu().getItem(1).setChecked(true);
                editor.putString("active_fragment", "1");
                editor.apply();
                return allEventFragment;


            case R.id.MemoriesFrag:
                bottomNavigationView.getMenu().getItem(2).setChecked(true);

                editor.putString("active_fragment", "2");
                editor.apply();
                return memoriesFragment;

            case R.id.NotificationFrag:
                bottomNavigationView.getMenu().getItem(3).setChecked(true);

                editor.putString("active_fragment", "3");
                editor.apply();
                return notificationFragment;

            case R.id.ProfileFrag:
                bottomNavigationView.getMenu().getItem(4).setChecked(true);

                editor.putString("active_fragment", "4");
                editor.apply();
                return profileFragment;


        }
        bottomNavigationView.getMenu().getItem(4).setChecked(true);
        editor.putString("active_fragment", "4");
        editor.apply();

        return myEventFragment;

    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().hide(activeFragment).show(fragment).commit();

        activeFragment = fragment;
    }

    @Override
    public void onBackPressed() {
        integerDeque.pop();
        if (!integerDeque.isEmpty()) {
            loadFragment(getFragment(integerDeque.peek()));
        } else {
            finish();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

        SharedPreferences.Editor editor = sharedPreferences.edit();


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
}