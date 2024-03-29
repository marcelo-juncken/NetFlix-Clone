package com.example.netflix.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflix.R;
import com.example.netflix.activity.DetalhesPostActivity;
import com.example.netflix.model.Post;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterPost extends RecyclerView.Adapter<AdapterPost.MyViewHolder> {
    private final List<Post> postList;
    private final Activity activity;

    public AdapterPost(List<Post> postList, Activity activity) {
        this.postList = postList;
        this.activity = activity;
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

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, DetalhesPostActivity.class);
            intent.putExtra("postSelecionado", post);
            activity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imagem;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imagem = itemView.findViewById(R.id.imagem);
        }
    }
}
