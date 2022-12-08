package com.collegemeet.android.revafest;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;


public class ProfileFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String TAG = "MyTag";

    public static final String POST_TYPE_PREFERENCE = "post_type_preference";

    FragmentAdderAdapter adapter;
    TabLayout profileTabLayout;
    ViewPager2 profilePager;
    FirebaseFirestore db;
    FloatingActionButton fab;
    FirebaseAuth mAuth;
    TextView userNameProfileTextView;
    TextView collegeProfileTextView, about;
    ImageView profileImage;
    SharedPreferences.Editor editor;
    SharedPreferences preferences;
    ProgressBar loading;
    CoordinatorLayout layout;
    private View view;
    private BroadcastReceiver mInternetConnectionReceiver;
    ImageButton settingsHamburger;
    TextView admin;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        fab = view.findViewById(R.id.add_content);
        about = view.findViewById(R.id.about_current_user_profile);
        loading = view.findViewById(R.id.loadProfileInitialDetails);
        layout = view.findViewById(R.id.layout_of_whole_profile_fragment);
        userNameProfileTextView = view.findViewById(R.id.profile_name_current_user);
        collegeProfileTextView = view.findViewById(R.id.college_name_profile_current_user);
        profileImage = view.findViewById(R.id.profile_image1);
        admin = view.findViewById(R.id.admin_current_user);


        settingsHamburger = view.findViewById(R.id.settings_hamburger);


        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

//        if(mAuth.getCurrentUser().getUid().equals("6682FudOcQO6hddbORt3rmXWBih2")){
//            admin.setVisibility(View.VISIBLE);
//        }else {
//            admin.setVisibility(View.GONE);
//        }


        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        editor = preferences.edit();

//        admin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getContext() , AddPostsActivity.class);
//
//                editor.putString("newEvent" , "newOne");
//                editor.apply();
//                intent.putExtra(POST_TYPE_PREFERENCE, "photo");
//                startActivity(intent);
//            }
//        });
        if (preferences.getString(MainActivityContainingFragment.USER_NAME, "").equals("") && preferences.getString(MainActivityContainingFragment.PROFILE_IMAGE, "").equals("")) {
            layout.setVisibility(View.INVISIBLE);
            loading.setVisibility(View.VISIBLE);
        } else {
            layout.setVisibility(View.VISIBLE);
            loading.setVisibility(View.INVISIBLE);
        }


        if (preferences.contains(MainActivityContainingFragment.PROFILE_IMAGE) && preferences.contains(MainActivityContainingFragment.USER_NAME)
                && preferences.contains(MainActivityContainingFragment.EMAIL) && preferences.contains(MainActivityContainingFragment.YEAR)
                && preferences.contains(MainActivityContainingFragment.ZEAL_ID)) {

            Uri profileImageUrl = Uri.parse(preferences.getString(MainActivityContainingFragment.PROFILE_IMAGE, ""));

            Glide.with(view.getContext())
                    .load(profileImageUrl)
                    .placeholder(R.drawable.revafestlogo)
                    .error(R.drawable.revafestlogo)
                    .into(profileImage);


            collegeProfileTextView.setText(preferences.getString(MainActivityContainingFragment.ZEAL_ID, ""));
            userNameProfileTextView.setText(preferences.getString(MainActivityContainingFragment.USER_NAME, ""));


        } else {
            setDetails();
        }

        Glide.with(view.getContext())
                .load(preferences.getString(MainActivityContainingFragment.PROFILE_IMAGE, ""))
                .into(profileImage);


        profileTabLayout = view.findViewById(R.id.profileTabLayoutCurrentUser1);
        profilePager = view.findViewById(R.id.profileViewPagerCurrentUser1);


        settingsHamburger.setOnClickListener(viewa -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(Objects.requireNonNull(getContext()), R.style.BottomSheetDialogTheme);
            View bottomSheetView = LayoutInflater.from(getContext().getApplicationContext()).inflate(R.layout.bottom_settings_fragment,
                    view.findViewById(R.id.bottom_sheet_container));


            bottomSheetView.findViewById(R.id.settings_edit_details).setOnClickListener(view110 -> {
                bottomSheetDialog.dismiss();
                Intent intent = new Intent(getContext(), EditDetails.class);
                startActivity(intent);

            });

            bottomSheetView.findViewById(R.id.settings_logout).setOnClickListener(view16 -> {
                bottomSheetDialog.dismiss();

                Intent intent = new Intent(getContext(), LoginActivity.class);
                editor.remove(MainActivityContainingFragment.YEAR);
                editor.remove(MainActivityContainingFragment.USER_NAME);
                editor.remove(MainActivityContainingFragment.ZEAL_ID);
                editor.remove(MainActivityContainingFragment.EMAIL);
                editor.remove(MainActivityContainingFragment.PROFILE_IMAGE);
                editor.apply();

                mAuth.signOut();
                startActivity(intent);
                getActivity().finish();
            });
            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();

        });


        userNameProfileTextView.setText(preferences.getString(MainActivityContainingFragment.USER_NAME, ""));
        collegeProfileTextView.setText(preferences.getString(MainActivityContainingFragment.ZEAL_ID, ""));


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(Objects.requireNonNull(getContext()), R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(getContext().getApplicationContext()).inflate(R.layout.bottom_add_content,
                        v.findViewById(R.id.bottom_content_type_container));
                bottomSheetView.findViewById(R.id.upload_photo_layout_bottom).setOnClickListener(view112 -> {
                    bottomSheetDialog.dismiss();
                    Intent intent = new Intent(getContext(), AddPostsActivity.class);
                    intent.putExtra(POST_TYPE_PREFERENCE, "photo");
                    startActivity(intent);
                });
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();


            }
        });


        FragmentManager manager = getParentFragmentManager();
        adapter = new FragmentAdderAdapter(manager, getLifecycle());
        profilePager.setAdapter(adapter);
        profileTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                profilePager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        profilePager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                profileTabLayout.selectTab(profileTabLayout.getTabAt(position));
            }
        });


        return view;

    }

    private void setDetails() {


        //create worker class here ... to fetch details from server
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(DetailsFetchWorkerClass.class)
                .build();


        WorkManager workManager = WorkManager.getInstance(getContext());

        workManager.enqueue(request);


        //jo phle ka pura removed code tha ab woh DetailsFetchWorkerClass mai hai
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (s.equals(MainActivityContainingFragment.USER_NAME)) {

            userNameProfileTextView.setText(sharedPreferences.getString(MainActivityContainingFragment.USER_NAME, "User"));

        }

        if (s.equals(MainActivityContainingFragment.PROFILE_IMAGE)) {

            Uri profileImageUrl = Uri.parse(sharedPreferences.getString(MainActivityContainingFragment.PROFILE_IMAGE, ""));
            Glide.with(getContext())
                    .load(profileImageUrl)
                    .into(profileImage);
        }


        if (s.equals((MainActivityContainingFragment.ZEAL_ID))) {

            String aboutText = (sharedPreferences.getString(MainActivityContainingFragment.ZEAL_ID, "Fest-ID"));
            collegeProfileTextView.setText(aboutText);

        }


    }
}