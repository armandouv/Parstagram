package com.example.parstagram.posts;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.parstagram.databinding.ActivityTimelineBinding;
import com.example.parstagram.users.LoginActivity;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class TimelineActivity extends AppCompatActivity {
    private static final String TAG = TimelineActivity.class.getSimpleName();
    private final List<Post> mPosts = new ArrayList<>();
    private ActivityTimelineBinding mBinding;
    private PostsAdapter mPostsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityTimelineBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.logoutButton.setOnClickListener(v ->
                ParseUser.logOutInBackground(e -> {
                    if (e != null) {
                        Toast.makeText(this, "Couldn't log out", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
                    goLoginActivity();
                    finish();
                }));

        mBinding.composeButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ComposeActivity.class);
            startActivity(intent);
        });

        mPostsAdapter = new PostsAdapter(this, mPosts, (view, position) -> {
            Intent intent = new Intent(TimelineActivity.this, PostDetailsActivity.class);
            Post post = mPosts.get(position);
            intent.putExtra(Post.class.getSimpleName(), post);
            startActivity(intent);
        });

        mBinding.swipeContainer.setOnRefreshListener(() -> populateHomeTimeline(true));

        mBinding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mBinding.postsView.setLayoutManager(new LinearLayoutManager(this));
        mBinding.postsView.setAdapter(mPostsAdapter);
        populateHomeTimeline(false);
    }

    private void populateHomeTimeline(boolean isRefreshing) {
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
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}