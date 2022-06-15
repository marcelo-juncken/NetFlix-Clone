package com.example.netflix.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflix.R;
import com.example.netflix.model.Post;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterPost extends RecyclerView.Adapter<AdapterPost.MyViewHolder> {
    private final List<Post> postList;
    private final OnClickListener onClickListener;

    public AdapterPost(List<Post> postList, OnClickListener onClickListener) {
        this.postList = postList;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_post,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Post post = postList.get(position);

        Picasso.get().load(post.getUrlImagem()).into(holder.imagem);

        holder.itemView.setOnClickListener(v -> onClickListener.onClick(post));
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public interface OnClickListener{
        void onClick(Post post);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imagem;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imagem = itemView.findViewById(R.id.imagem);
        }
    }
}
