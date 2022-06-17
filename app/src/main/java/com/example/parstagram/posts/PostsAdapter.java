package com.example.parstagram.posts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.parstagram.databinding.ItemPostBinding;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    private final Context mContext;
    private final List<Post> mPosts;
    private final OnItemClickListener mClickListener;

    public PostsAdapter(Context context, List<Post> posts,
                        OnItemClickListener clickListener) {
        this.mContext = context;
        this.mPosts = posts;
        this.mClickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPostBinding itemBinding = ItemPostBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = mPosts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemPostBinding mBinding;

        public ViewHolder(@NonNull ItemPostBinding binding) {
            super(binding.getRoot());

            this.mBinding = binding;
            itemView.setOnClickListener(v ->
                    mClickListener.onItemClick(itemView, getAdapterPosition()));
        }

        public void bind(Post post) {
            mBinding.itemUsername.setText(post.getUser().getUsername());
            mBinding.itemDescription.setText(post.getDescription());
            mBinding.itemTimestamp.setText(post.getCreatedAt().toString());

            Glide.with(mContext)
                    .load(post.getImage().getUrl())
                    .into(mBinding.itemImage);
        }
    }
}
