package com.example.netflix.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflix.R;
import com.example.netflix.model.Categoria;
import com.example.netflix.model.Post;

import java.util.List;

public class AdapterCategoria extends RecyclerView.Adapter<AdapterCategoria.MyViewHolder> implements AdapterPost.OnClickListener {

    private List<Categoria> categoriaList;
    private Context context;

    public AdapterCategoria(List<Categoria> categoriaList, Context context) {
        this.categoriaList = categoriaList;
        this.context = context;
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
        holder.rvListagem.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.rvListagem.setHasFixedSize(true);
        holder.adapterPost = new AdapterPost(categoria.getPostList(),this);
        holder.rvListagem.setAdapter(holder.adapterPost);
        holder.adapterPost.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return categoriaList.size();
    }

    @Override
    public void onClick(Post post) {
        Toast.makeText(context, post.getTitulo(), Toast.LENGTH_SHORT).show();
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
