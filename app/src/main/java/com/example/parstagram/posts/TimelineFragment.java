package com.example.parstagram.posts;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.parstagram.databinding.FragmentTimelineBinding;
import com.example.parstagram.users.LoginActivity;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class TimelineFragment extends Fragment {

    public static final String TAG = MainActivity.class.getSimpleName();
    protected final List<Post> mPosts = new ArrayList<>();
    protected FragmentTimelineBinding mBinding;
    protected PostsAdapter mPostsAdapter;

    public TimelineFragment() {
        // Required empty public constructor
    }

    protected void populateHomeTimeline(boolean isRefreshing) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(20);
        query.addDescendingOrder("createdAt");

        query.findInBackground((posts, e) -> {
            if (e != null) {
                Log.e(TAG, "Could not query post", e);
                return;
            }

            if (isRefreshing) {
                mPosts.clear();
                mBinding.swipeContainer.setRefreshing(false);
            }
            mPosts.addAll(posts);
            mPostsAdapter.notifyDataSetChanged();
        });
    }

    private void goLoginActivity() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding.logoutButton.setOnClickListener(v ->
                ParseUser.logOutInBackground(e -> {
                    if (e != null) {
                        Toast.makeText(getContext(), "Couldn't log out", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();
                    goLoginActivity();
                }));

        mPostsAdapter = new PostsAdapter(getContext(), mPosts, (v, position) -> {
            Intent intent = new Intent(getContext(), PostDetailsActivity.class);
            Post post = mPosts.get(position);
            intent.putExtra(Post.class.getSimpleName(), post);
            startActivity(intent);
        });

        mBinding.swipeContainer.setOnRefreshListener(() -> populateHomeTimeline(true));

        mBinding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mBinding.postsView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.postsView.setAdapter(mPostsAdapter);
        populateHomeTimeline(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentTimelineBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }
}