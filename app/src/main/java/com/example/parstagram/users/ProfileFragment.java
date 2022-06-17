package com.example.parstagram.users;

import android.util.Log;

import com.example.parstagram.posts.Post;
import com.example.parstagram.posts.TimelineFragment;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ProfileFragment extends TimelineFragment {
    @Override
    protected void populateHomeTimeline(boolean isRefreshing) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        query.setLimit(20);
        query.addDescendingOrder(Post.KEY_CREATED_AT);

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
}