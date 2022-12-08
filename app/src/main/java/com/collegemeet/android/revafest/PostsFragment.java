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
import androidx.recyclerview.widget.GridLayoutManager;
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


public class PostsFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {


    ArrayList<PostFeedDetailsClass> list;
    private HashSet<String> hashSet;
    grid_profile_adapter_class adapter;
    ProgressBar progressBar;
    private static final String TAG = "MyTag";
    RecyclerView recyclerView;
    LinearLayoutManager manager;
    FirebaseFirestore fireStore;
    String key = null;
    Button refresh;
    RelativeLayout internetError;
    boolean isScrolling = false;
    int totalItems, currentItems, scrolledOutItems;
    SharedPreferences preferences;
    private String userCollegeName;
    private FirebaseAuth mAuth;
    private String uid;
    LinearLayout noPost;
    SwipeRefreshLayout refreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_posts, container, false);

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        hashSet = new HashSet<>();
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        refreshLayout = view.findViewById(R.id.postProfileRefresh);

        internetError = view.findViewById(R.id.internet_not_available_post_profile);
        refresh = view.findViewById(R.id.refresh_button_post_profile);
        recyclerView = view.findViewById(R.id.profile_post_recycler_view);
        recyclerView.setHasFixedSize(true);
        manager = new GridLayoutManager(getContext() , 3);
        progressBar = view.findViewById(R.id.profile_post_progress_bar);
        fireStore = FirebaseFirestore.getInstance();
        recyclerView.setLayoutManager(manager);
        list = new ArrayList<>();
        adapter = new grid_profile_adapter_class(getContext(), list);
        recyclerView.setAdapter(adapter);
        noPost = view.findViewById(R.id.no_post_profile);

        if (isNetworkAvailable(getContext())) {
            fetchPosts();
        } else {
            internetError.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Network error!", Toast.LENGTH_SHORT).show();
        }
        refresh.setOnClickListener(v -> {
            if (isNetworkAvailable(getContext())) {
                internetError.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                fetchPosts();
            } else {
                internetError.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Network error!", Toast.LENGTH_SHORT).show();
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
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isNetworkAvailable(getContext())) {

                    key = null;
                    hashSet.clear();
                    list.clear();
                    adapter.notifyDataSetChanged();
                    fetchPosts();
                } else {
                    internetError.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Network error!", Toast.LENGTH_SHORT).show();
                }
                refreshLayout.setRefreshing(false);
            }
        });



        return view;
    }



    private void fetchPosts() {

        if (key == null) {
            progressBar.setVisibility(View.VISIBLE);

            fireStore.collection("users").document(uid)
                    .collection("everything").document("post").collection("All post").orderBy("path", Query.Direction.DESCENDING).limit(1).addSnapshotListener((value, error) -> {
                if (value.getDocuments().size() == 0) {

                    noPost.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                } else {

                    noPost.setVisibility(View.GONE);




                    for (DocumentChange documentChange : value.getDocumentChanges()) {

                        if (hashSet.contains(documentChange.getDocument().getString("path"))) {
                            Log.d(TAG, "already have this data");
                            progressBar.setVisibility(View.GONE);
                        } else {

                            if (documentChange != null && documentChange.getType() == DocumentChange.Type.ADDED) {

                                hashSet.add(documentChange.getDocument().getString("path"));


                                //list
                                list.add(new PostFeedDetailsClass(preferences.getString(MainActivityContainingFragment.USER_NAME,""),
                                        preferences.getString(MainActivityContainingFragment.PROFILE_IMAGE,""),
                                        documentChange.getDocument().getString("date"),
                                        documentChange.getDocument().getString("path"),
                                        documentChange.getDocument().getString("post uri"),
                                        documentChange.getDocument().getString("caption"),
                                        uid ,
                                        documentChange.getDocument().getString("type_of_post"),
                                        documentChange.getDocument().getString("coordinates"),
                                        documentChange.getDocument().getString("frame uri")));

                                adapter.notifyDataSetChanged();

                                isScrolling = true;

                                progressBar.setVisibility(View.GONE);
                                key = documentChange.getDocument().getString("path");
                            }else{
                                progressBar.setVisibility(View.GONE);
                            }
                        }


                    }
                }

            });

        } else {
            progressBar.setVisibility(View.VISIBLE);


            fireStore.collection("users").document(uid)
                    .collection("everything").document("post").collection("All post").orderBy("path", Query.Direction.DESCENDING).startAfter(key).limit(1).addSnapshotListener((value, error) -> {


                if (value == null) {

                    isScrolling = false;
                    progressBar.setVisibility(View.GONE);

                } else {



                    for (DocumentChange documentChange : value.getDocumentChanges()) {

                        if (hashSet.contains(documentChange.getDocument().getString("path"))) {
                            Log.d(TAG, "already have this data");
                            progressBar.setVisibility(View.GONE);

                        } else {

                            if (documentChange != null && documentChange.getType() == DocumentChange.Type.ADDED) {

                                hashSet.add(documentChange.getDocument().getString("path"));

                                //list
                                list.add(new PostFeedDetailsClass(preferences.getString(MainActivityContainingFragment.USER_NAME,""),
                                        preferences.getString(MainActivityContainingFragment.PROFILE_IMAGE,"")
                                        , documentChange.getDocument().getString("date"),
                                        documentChange.getDocument().getString("path"),
                                        documentChange.getDocument().getString("post uri"),
                                        documentChange.getDocument().getString("caption"), uid
                                        , documentChange.getDocument().getString("type_of_post"),
                                        documentChange.getDocument().getString("coordinates"),
                                        documentChange.getDocument().getString("frame uri")));


                                adapter.notifyDataSetChanged();

                                isScrolling = true;

                                progressBar.setVisibility(View.GONE);
                                key = documentChange.getDocument().getString("path");

                            }else{
                                progressBar.setVisibility(View.GONE);
                            }
                        }


                    }

                }


            });
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());


    }


}