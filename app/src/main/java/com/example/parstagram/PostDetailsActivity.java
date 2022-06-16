package com.example.parstagram;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.parstagram.databinding.ActivityPostDetailsBinding;

public class PostDetailsActivity extends AppCompatActivity {
    private ActivityPostDetailsBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        mBinding = ActivityPostDetailsBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        Post post = getIntent().getParcelableExtra(Post.class.getSimpleName());

        mBinding.itemUsername.setText(post.getUser().getUsername());
        mBinding.itemDescription.setText(post.getDescription());
        mBinding.itemTimestamp.setText(post.getCreatedAt().toString());

        Glide.with(this)
                .load(post.getImage().getUrl())
                .into(mBinding.itemImage);
    }
}