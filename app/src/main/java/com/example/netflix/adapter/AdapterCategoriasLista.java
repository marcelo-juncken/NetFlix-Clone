package com.example.netflix.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflix.R;
import com.example.netflix.model.Categoria;

import java.util.List;

public class AdapterCategoriasLista extends RecyclerView.Adapter<AdapterCategoriasLista.MyViewHolder> {

    private final List<Categoria> categoriaList;
    private final List<String> categoriaString;
    private final OnClick onClick;

    public AdapterCategoriasLista(List<Categoria> categoriaList, List<String> categoriaString, OnClick onClick) {
        this.categoriaList = categoriaList;
        this.categoriaString = categoriaString;
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_itens_categorias, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Categoria categoria = categoriaList.get(position);

        holder.textCategoria.setText(categoria.getNome());

        holder.cbCategoria.setOnCheckedChangeListener((buttonView, isChecked) -> {
            onClick.onclick(categoria, holder.cbCategoria.isChecked());
        });

        holder.cbCategoria.setChecked(categoriaString.contains(categoria.getNome()));

        holder.itemView.setOnClickListener(v -> {
            holder.cbCategoria.setChecked(!holder.cbCategoria.isChecked());

            onClick.onclick(categoria, holder.cbCategoria.isChecked());

        });

    }

    @Override
    public int getItemCount() {
        return categoriaList.size();
    }

    public interface OnClick {
        void onclick(Categoria categoria, Boolean status);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbCategoria;
        TextView textCategoria;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cbCategoria = itemView.findViewById(R.id.cbCategoria);
            textCategoria = itemView.findViewById(R.id.textCategoria);
        }
    }
}
