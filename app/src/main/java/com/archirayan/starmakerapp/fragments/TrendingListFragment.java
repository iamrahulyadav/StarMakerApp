package com.archirayan.starmakerapp.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.archirayan.starmakerapp.R;
import com.archirayan.starmakerapp.adapter.FriendsRecommedAdapter;
import com.archirayan.starmakerapp.adapter.TrendingAdapter;
import com.archirayan.starmakerapp.model.FollowList;
import com.archirayan.starmakerapp.model.FollowingListResponse;
import com.archirayan.starmakerapp.model.GetSongList;
import com.archirayan.starmakerapp.model.GetTrendingSongList;
import com.archirayan.starmakerapp.model.SongListResponse;
import com.archirayan.starmakerapp.model.TrendingListResponse;
import com.archirayan.starmakerapp.utils.Constant;
import com.archirayan.starmakerapp.utils.Utils;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrendingListFragment extends Fragment {

    private static final String TAG = "SongListFragment";
    RecyclerView rvList, recycler_friendfollowing;
    TrendingAdapter songAdapter;
    private ArrayList<GetTrendingSongList> getSongList;
    private ProgressDialog pd;
    private FriendsRecommedAdapter friendsRecommedAdapter;
    private ArrayList<FollowList> followLists;
    private SwipeRefreshLayout swipe_view;

    public TrendingListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trending_list, container, false);
    }

    @Override
    public boolean getUserVisibleHint() {

        if (isVisible()) {
            songAdapter = new TrendingAdapter(getActivity(), getSongList);
            rvList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            rvList.setAdapter(songAdapter);
        }
        return isVisible();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvList = view.findViewById(R.id.item_list);
        recycler_friendfollowing = view.findViewById(R.id.recycler_friendfollowing);
        recycler_friendfollowing.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));
        //friendsRecommedAdapter = new FriendsRecommedAdapter(getActivity());
        //recycler_friendfollowing.setAdapter(friendsRecommedAdapter);
        getFollowList();
        getSongList();
        //songAdapter = new SongAdapter(getActivity());
        rvList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        swipe_view = view.findViewById(R.id.swipe_view);

        swipe_view.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFollowList();
                swipe_view.setRefreshing(false);
            }
        });
    }

    private void getFollowList() {
        followLists = new ArrayList<>();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("user_id", Utils.ReadSharePrefrence(getActivity(), Constant.USERID));

        Log.e(TAG, "URL:" + Constant.URL + "user_list.php?" + params);
        Log.e(TAG, params.toString());
        client.post(getActivity(), Constant.URL + "user_list.php?", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e(TAG, "RESPONSE-" + response);
                FollowingListResponse model = new Gson().fromJson(new String(String.valueOf(response)), FollowingListResponse.class);
                if (model.getStatus().equals("true")) {
                    followLists = model.getData();
                    friendsRecommedAdapter = new FriendsRecommedAdapter(getActivity(), followLists);
                    //RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    // recycler_friendfollowing.setLayoutManager(mLayoutManager);
                    //  recycler_friendfollowing.setItemAnimator(new DefaultItemAnimator());
                    recycler_friendfollowing.setAdapter(friendsRecommedAdapter);
                    friendsRecommedAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e(TAG, throwable.getMessage());
            }
        });
    }

    private void getSongList() {
        pd = new ProgressDialog(getActivity());
        pd.setCancelable(true);
        pd.show();
        getSongList = new ArrayList<>();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("user_id", Utils.ReadSharePrefrence(getActivity(), Constant.USERID));
        params.put("flag", "trending");

        Log.e(TAG, "URL:" + Constant.URL + "song_list.php?" + params);
        Log.e(TAG, params.toString());
        client.post(getActivity(), Constant.URL + "song_list.php?", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response)
            {
                super.onSuccess(statusCode, headers, response);
                Log.e(TAG, "RESPONSE-" + response);
                TrendingListResponse model = new Gson().fromJson(new String(String.valueOf(response)), TrendingListResponse.class);
                pd.dismiss();
                if (model.getStatus().equals("true"))
                {
                    getSongList = model.getData();
                    songAdapter = new TrendingAdapter(getActivity(), getSongList);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    rvList.setLayoutManager(mLayoutManager);
                    rvList.setItemAnimator(new DefaultItemAnimator());
                    rvList.setAdapter(songAdapter);
                    songAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e(TAG, throwable.getMessage());

            }
        });
    }
}