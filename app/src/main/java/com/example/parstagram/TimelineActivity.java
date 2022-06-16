package com.example.parstagram;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.parstagram.databinding.ActivityTimelineBinding;
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

        mPostsAdapter = new PostsAdapter(this, mPosts, (view, position) -> {
            Intent intent = new Intent(TimelineActivity.this, PostDetailsActivity.class);
            Post post = mPosts.get(position);
            intent.putExtra(Post.class.getSimpleName(), post);
            startActivity(intent);
        });

        mBinding.postsView.setLayoutManager(new LinearLayoutManager(this));
        mBinding.postsView.setAdapter(mPostsAdapter);
        populateHomeTimeline();
    }

    private void populateHomeTimeline() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(20);
        query.addDescendingOrder("createdAt");

        query.findInBackground((posts, e) -> {
            if (e != null) {
                Log.e(TAG, "Could not query post", e);
                return;
            }

            mPosts.addAll(posts);
            mPostsAdapter.notifyDataSetChanged();
        });
    }

    private void goLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}