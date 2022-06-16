package com.example.netflix.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.netflix.R;
import com.example.netflix.adapter.AdapterCategoria;
import com.example.netflix.helper.FirebaseHelper;
import com.example.netflix.model.Categoria;
import com.example.netflix.model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class InicioFragment extends Fragment {

    private RecyclerView rvCategorias;
    private AdapterCategoria adapterCategoria;
    private final List<Categoria> categoriaListTemp = new ArrayList<>();
    private final List<Categoria> categoriaList = new ArrayList<>();

    private Button btnSeries, btnFilmes, btnMinhaLista;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inicio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        iniciaComponentes(view);

        configRV();
        recuperaCategorias();
    }

    private void recuperaCategorias() {
        DatabaseReference categoriasRef = FirebaseHelper.getDatabaseReference()
                .child("categorias");
        categoriasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
                categoriaListTemp.clear();
                categoriaList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Categoria categoria = ds.getValue(Categoria.class);
                        if (categoria != null) {
                            categoria.setPostList(new ArrayList<>());
                            categoriaListTemp.add(categoria);
                        }
                    }
                    recuperaPosts();
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void recuperaPosts() {
        DatabaseReference postRef = FirebaseHelper.getDatabaseReference()
                .child("posts");
        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Post post = ds.getValue(Post.class);
                    if (post != null) {
                        for (int i = 0; i < categoriaListTemp.size(); i++) {
                            if (post.getGeneroList().contains(categoriaListTemp.get(i).getNome())) {

                                List<Post> postTemp = new ArrayList<>(categoriaListTemp.get(i).getPostList());

                                postTemp.add(0, post);
                                categoriaListTemp.get(i).setPostList(postTemp);
                            }
                        }

                    }
                }
                for (Categoria categoria : categoriaListTemp) {
                    if (categoria.getPostList().size() != 0) {
                        categoriaList.add(categoria);
                    }
                }
                progressBar.setVisibility(View.GONE);
                adapterCategoria.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configRV() {
        rvCategorias.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCategorias.setHasFixedSize(true);
        adapterCategoria = new AdapterCategoria(categoriaList, requireActivity());
        rvCategorias.setAdapter(adapterCategoria);
    }

    private void iniciaComponentes(View view) {
        rvCategorias = view.findViewById(R.id.rvCategorias);
        btnSeries = view.findViewById(R.id.btnSeries);
        btnFilmes = view.findViewById(R.id.btnFilmes);
        btnMinhaLista = view.findViewById(R.id.btnMinhaLista);
        progressBar = view.findViewById(R.id.progressBar);

    }
}