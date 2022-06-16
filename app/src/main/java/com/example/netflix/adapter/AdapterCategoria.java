package com.example.netflix.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflix.R;
import com.example.netflix.activity.DetalhesPostActivity;
import com.example.netflix.model.Categoria;
import com.example.netflix.model.Post;

import java.util.List;

public class AdapterCategoria extends RecyclerView.Adapter<AdapterCategoria.MyViewHolder> {

    private final List<Categoria> categoriaList;
    private final Activity activity;

    public AdapterCategoria(List<Categoria> categoriaList, Activity activity) {
        this.categoriaList = categoriaList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_categoria, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Categoria categoria = categoriaList.get(position);

        holder.textCategoria.setText(categoria.getNome());

        configRV(holder, categoria);

    }

    public void configRV(MyViewHolder holder, Categoria categoria) {
        holder.rvListagem.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        holder.rvListagem.setHasFixedSize(true);
        holder.adapterPost = new AdapterPost(categoria.getPostList(), activity);
        holder.rvListagem.setAdapter(holder.adapterPost);
        holder.adapterPost.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return categoriaList.size();
    }


    static class MyViewHolder extends RecyclerView.ViewHolder {
        AdapterPost adapterPost;
        RecyclerView rvListagem;

        TextView textCategoria;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textCategoria = itemView.findViewById(R.id.textCategoria);
            rvListagem = itemView.findViewById(R.id.rvListagem);
        }
    }
}
