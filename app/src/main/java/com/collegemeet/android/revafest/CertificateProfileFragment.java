package com.collegemeet.android.revafest;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashSet;


public class CertificateProfileFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {


    private static final String TAG = "MyTag";
    private RecyclerView recyclerView;
    private CertificateAdapterClass adapter;

    private HashSet<String> hashSet;
    private String key = null;
    private LinearLayoutManager manager;
    private FirebaseFirestore firestore;
    private ArrayList<VlogFetchDetailsClass> list;
    private int totalItems, currentItems, scrolledOutItems;
    private Boolean isScrolling = false;
    SharedPreferences preferences;




    private FirebaseAuth mAuth;
    private String uid;

    SwipeRefreshLayout refreshLayout;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_certificate_profile, container, false);





        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setNavigationBarColor(getResources().getColor(R.color.black));
        }


        hashSet = new HashSet<>();

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());


        firestore = FirebaseFirestore.getInstance();

        recyclerView = view.findViewById(R.id.cer_feed_recycler_view);
        manager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        list = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        adapter = new CertificateAdapterClass(getContext(), list);
        recyclerView.setAdapter(adapter);
        refreshLayout = view.findViewById(R.id.refreshCerFeed);



        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


                list.clear();
                adapter.notifyDataSetChanged();
                key = null;
                hashSet.clear();
                if (isNetworkAvailable(getContext())) {
                    fetchVlogs();
                }
                refreshLayout.setRefreshing(false);


            }
        });



        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = manager.getChildCount();
                totalItems = manager.getItemCount();
                scrolledOutItems = manager.findFirstVisibleItemPosition();
                //yaha pe jaise hi laga ab khatam hona wala hai fetch karne shuru kar diyo...
                if (isScrolling && (currentItems + scrolledOutItems) == totalItems) {
                    isScrolling = false;
                    if (isNetworkAvailable(getContext())) {
                        fetchVlogs();
                    } else {
                        Toast.makeText(getContext(), "Network error!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        if(isNetworkAvailable(getContext())){

            fetchVlogs();
        }else {
            Toast.makeText(getContext() , "Network error!" , Toast.LENGTH_SHORT).show();
        }


        return view;
    }


    private void fetchVlogs() {

        if (key == null) {


            firestore.collection("users").document(uid)
                    .collection("myEvents").orderBy("eventName", Query.Direction.DESCENDING).limit(6).addSnapshotListener((value, error) -> {



                if (value == null) {

                    isScrolling = false;

                } else {

                    for (DocumentChange documentChange : value.getDocumentChanges()) {


                        if (hashSet.contains(documentChange.getDocument().getString("evenName"))) {
                            Log.d(TAG, "already have this data");

                        } else {
                            if (documentChange.getDocument().getString("eventName") != null ||
                                    documentChange.getDocument().getString("eventImage") != null) {

                                if (documentChange != null && documentChange.getType() == DocumentChange.Type.ADDED) {

                                    hashSet.add(documentChange.getDocument().getString("eventName"));

                                    //list
                                    list.add(new VlogFetchDetailsClass(documentChange.getDocument().getString("eventImage")
                                            , documentChange.getDocument().getString("eventName")));


                                    adapter.notifyDataSetChanged();

                                    isScrolling = true;


                                    key = documentChange.getDocument().getString("eventName");

                                }

                            } else {
                                adapter.notifyDataSetChanged();

                                isScrolling = true;


                                key = documentChange.getDocument().getString("eventName");
                            }


                        }


                    }

                }

            });

        } else {



            firestore.collection("users").document(uid)
                    .collection("myEvents").orderBy("eventName", Query.Direction.DESCENDING).startAfter(key).limit(1)
                    .addSnapshotListener((value, error) -> {


                        if (value == null) {

                            isScrolling = false;

                        } else {

                            for (DocumentChange documentChange : value.getDocumentChanges()) {


                                if (hashSet.contains(documentChange.getDocument().getString("evenName"))) {
                                    Log.d(TAG, "already have this data");

                                } else {
                                    if (documentChange.getDocument().getString("eventName") != null ||
                                            documentChange.getDocument().getString("eventImage") != null) {

                                        if (documentChange != null && documentChange.getType() == DocumentChange.Type.ADDED) {

                                            hashSet.add(documentChange.getDocument().getString("eventName"));

                                            //list
                                            list.add(new VlogFetchDetailsClass(documentChange.getDocument().getString("eventImage")
                                                    , documentChange.getDocument().getString("eventName")));


                                            adapter.notifyDataSetChanged();

                                            isScrolling = true;


                                            key = documentChange.getDocument().getString("eventName");

                                        }

                                    } else {
                                        adapter.notifyDataSetChanged();

                                        isScrolling = true;


                                        key = documentChange.getDocument().getString("eventName");
                                    }


                                }


                            }

                        }


                    });
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        list.clear();
        adapter.notifyDataSetChanged();
        key = null;
        hashSet.clear();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }
}