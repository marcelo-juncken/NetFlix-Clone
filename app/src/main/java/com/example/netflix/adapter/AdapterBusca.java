package com.example.netflix.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflix.R;
import com.example.netflix.activity.DetalhesPostActivity;
import com.example.netflix.model.Post;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterBusca extends RecyclerView.Adapter<AdapterBusca.MyViewHolder> {

    private final List<Post> postList;
    private final Context context;

    public AdapterBusca(List<Post> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_listagem_busca, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Post filme = postList.get(position);

        holder.textTitulo.setText(filme.getTitulo());
        Picasso.get().load(filme.getUrlImagem()).into(holder.imagem);

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, DetalhesPostActivity.class);
            intent.putExtra("postSelecionado", filme);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textTitulo;
        ImageView imagem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textTitulo = itemView.findViewById(R.id.textTitulo);
            imagem = itemView.findViewById(R.id.imagem);
        }
    }

}
