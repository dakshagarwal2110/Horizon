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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashSet;

public class MemoriesFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {



    ArrayList<PostFeedDetailsClass> list;
    private HashSet<String> hashSet;
    PostFeedAdapterClass adapter;
    ProgressBar progressBar;
    private static final String TAG = "MyTag";
    RecyclerView recyclerView;
    LinearLayoutManager manager;
    FirebaseFirestore fireStore;
    String key = null;
    Button continueButton;
    RelativeLayout internetError;
    boolean isScrolling = false;
    int totalItems, currentItems, scrolledOutItems;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private String userCollegeName;
    LinearLayout noPost;
    private FirebaseAuth mAuth;

    SwipeRefreshLayout swipeRefreshLayout;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_memories, container, false);

        mAuth = FirebaseAuth.getInstance();
        hashSet = new HashSet<>();

        swipeRefreshLayout = view.findViewById(R.id.swipePostFeed);







        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = preferences.edit();





        noPost = view.findViewById(R.id.no_post_feed);

        recyclerView = view.findViewById(R.id.postFeedRecyclerView);
        recyclerView.setHasFixedSize(true);
        manager = new LinearLayoutManager(getContext());
        progressBar = view.findViewById(R.id.post_feed_progress_bar);
        fireStore = FirebaseFirestore.getInstance();
        recyclerView.setLayoutManager(manager);
        list = new ArrayList<>();
        adapter = new PostFeedAdapterClass(getContext(), list);
        recyclerView.setAdapter(adapter);




        if (isNetworkAvailable(getContext())) {
            fetchPosts();
        } else {
            internetError.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Network error!", Toast.LENGTH_SHORT).show();
        }



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

                if (isScrolling && (currentItems + scrolledOutItems) == totalItems) {
                    isScrolling = false;
                    if (isNetworkAvailable(getContext())) {


                        fetchPosts();
                    } else {
                        Toast.makeText(getContext(), "Network error!", Toast.LENGTH_SHORT).show();
                    }
                }
                //daksh infinite scroll app doing fine...bt taking time ok...

            }
        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


                list.clear();
                adapter.notifyDataSetChanged();
                key = null;
                hashSet.clear();

                if (isNetworkAvailable(getContext())) {
                    fetchPosts();
                } else {
                    internetError.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Network error!", Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);


            }
        });

        return view;
    }


    private void fetchPosts() {

        if (key == null) {


            progressBar.setVisibility(View.VISIBLE);

            fireStore.collection("All colleges posts").document("fest")
                    .collection("posts").orderBy("path", Query.Direction.DESCENDING).limit(1).addSnapshotListener((value, error) -> {
                if (value.getDocuments().size() == 0) {

                    noPost.setVisibility(View.VISIBLE);

                } else {

                    noPost.setVisibility(View.GONE);

                    for (DocumentChange documentChange : value.getDocumentChanges()) {


                        if (hashSet.contains(documentChange.getDocument().getString("path"))) {

                            Log.d(TAG, "already have this data");


                        } else {

                            if (documentChange.getType() == DocumentChange.Type.ADDED) {

                                String path = documentChange.getDocument().getString("path");
                                hashSet.add(path);


                                String uidUser = documentChange.getDocument().getString("uid");


                                if (true) {


                                    fireStore.collection("users").document(uidUser).collection("everything")
                                            .document("details").get().addOnSuccessListener(documentSnapshot -> {

//                                    String profileImage = documentSnapshot.getString("profile image");
                                        String name = documentSnapshot.getString("name");


                                        if (documentSnapshot.getString("profile image") != null) {
                                            list.add(new PostFeedDetailsClass(name,
                                                    documentSnapshot.getString("profile image"),
                                                    documentChange.getDocument().getString("date"),
                                                    documentChange.getDocument().getString("path"),
                                                    documentChange.getDocument().getString("post uri"),
                                                    documentChange.getDocument().getString("caption"),
                                                    mAuth.getCurrentUser().getUid(),
                                                    documentChange.getDocument().getString("coordinates"),
                                                    documentChange.getDocument().getString("type_of_post"),
                                                    documentChange.getDocument().getString("uid"),
                                                    documentChange.getDocument().getString("frame uri")));
                                        } else {
                                            list.add(new PostFeedDetailsClass(name,
                                                    "https://firebasestorage.googleapis.com/v0/b/myfirebaseapp-73558.appspot.com/o/user_no_profile_image.png?alt=media&token=c6ab381a-ab3c-48e1-a4fa-995e5eeb385e",
                                                    documentChange.getDocument().getString("date"),
                                                    documentChange.getDocument().getString("path"),
                                                    documentChange.getDocument().getString("post uri"),
                                                    documentChange.getDocument().getString("caption"),
                                                    mAuth.getCurrentUser().getUid(), documentChange.getDocument().getString("coordinates"),
                                                    documentChange.getDocument().getString("type_of_post"),
                                                    documentChange.getDocument().getString("uid"),
                                                    documentChange.getDocument().getString("frame uri")));
                                        }

                                        progressBar.setVisibility(View.GONE);
                                        adapter.notifyDataSetChanged();
                                        isScrolling = true;
                                        key = documentChange.getDocument().getString("path");
                                    }).addOnFailureListener(e -> Log.d(TAG, "failure: " + e.getMessage()));
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    key = documentChange.getDocument().getString("path");
                                    if (isNetworkAvailable(getContext())) {
                                        fetchPosts();
                                    } else {
                                        internetError.setVisibility(View.VISIBLE);
                                        recyclerView.setVisibility(View.GONE);
                                        Toast.makeText(getContext(), "Network error!", Toast.LENGTH_SHORT).show();
                                    }
                                }


                            }
                        }


                    }
                }
                progressBar.setVisibility(View.GONE);
            });

        } else {

            progressBar.setVisibility(View.VISIBLE);

            fireStore.collection("All colleges posts").document("fest")
                    .collection("posts").orderBy("path", Query.Direction.DESCENDING).startAfter(key).limit(3).addSnapshotListener((value, error) -> {


                if (value == null) {

                    isScrolling = false;

                } else {


                    for (DocumentChange documentChange : value.getDocumentChanges()) {


                        String path = documentChange.getDocument().getString("path");
                        if (hashSet.contains(path)) {

                            Log.d(TAG, "already have this data");
                        } else {

                            if (documentChange.getType() == DocumentChange.Type.ADDED) {

                                hashSet.add(documentChange.getDocument().getString("path"));


//
                                String uidUser = documentChange.getDocument().getString("uid");

                                if (true) {


                                    fireStore.collection("users").document(uidUser).collection("everything")
                                            .document("details").get().addOnSuccessListener(documentSnapshot -> {

//                                        String profileImage = documentSnapshot.getString("profile image");
                                        String name = documentSnapshot.getString("name");

                                        if (documentSnapshot.getString("profile image") != null) {
                                            list.add(new PostFeedDetailsClass(name,
                                                    documentSnapshot.getString("profile image"),
                                                    documentChange.getDocument().getString("date"),
                                                    documentChange.getDocument().getString("path"),
                                                    documentChange.getDocument().getString("post uri"),
                                                    documentChange.getDocument().getString("caption"),
                                                    mAuth.getCurrentUser().getUid(), documentChange.getDocument().getString("coordinates"),
                                                    documentChange.getDocument().getString("type_of_post"),
                                                    documentChange.getDocument().getString("uid"),
                                                    documentChange.getDocument().getString("frame uri")));
                                        } else {
                                            list.add(new PostFeedDetailsClass(name,
                                                    "https://firebasestorage.googleapis.com/v0/b/myfirebaseapp-73558.appspot.com/o/user_no_profile_image.png?alt=media&token=c6ab381a-ab3c-48e1-a4fa-995e5eeb385e",
                                                    documentChange.getDocument().getString("date"),
                                                    documentChange.getDocument().getString("path"),
                                                    documentChange.getDocument().getString("post uri"),
                                                    documentChange.getDocument().getString("caption"),
                                                    mAuth.getCurrentUser().getUid(), documentChange.getDocument().getString("coordinates"),
                                                    documentChange.getDocument().getString("type_of_post"),
                                                    documentChange.getDocument().getString("uid"),
                                                    documentChange.getDocument().getString("frame uri")));
                                        }


                                        progressBar.setVisibility(View.GONE);
                                        adapter.notifyDataSetChanged();
                                        key = documentChange.getDocument().getString("path");

                                        isScrolling = true;
                                    }).addOnFailureListener(e -> Log.d(TAG, "failure: " + e.getMessage()));

                                } else {

                                    progressBar.setVisibility(View.GONE);
                                    key = documentChange.getDocument().getString("path");
                                    if (isNetworkAvailable(getContext())) {
                                        fetchPosts();
                                    } else {
                                        internetError.setVisibility(View.VISIBLE);
                                        recyclerView.setVisibility(View.GONE);
                                        Toast.makeText(getContext(), "Network error!", Toast.LENGTH_SHORT).show();
                                    }
                                }


                            }
                        }


                    }

                }
                progressBar.setVisibility(View.GONE);

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
        if(s.equals("active_fragment")) {
            if (preferences.getString("active_fragment", "-1").equals("2")) {

                fetchPosts();
            }
        }
    }
}