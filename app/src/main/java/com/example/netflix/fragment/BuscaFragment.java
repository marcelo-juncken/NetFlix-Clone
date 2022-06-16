package com.example.netflix.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflix.R;
import com.example.netflix.adapter.AdapterBusca;
import com.example.netflix.helper.FirebaseHelper;
import com.example.netflix.model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BuscaFragment extends Fragment {

    private TextView textInfo;
    private SearchView searchView;

    private RecyclerView rvBusca;
    private AdapterBusca adapterBusca;
    private List<Post> postListAll = new ArrayList<>();
    private List<Post> postList = new ArrayList<>();

    private EditText editPesquisa;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_busca, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        iniciaComponentes(view);
        configRV();
        recuperaPosts();
        configSearchView();
    }


    private void recuperaPosts() {
        DatabaseReference postRef = FirebaseHelper.getDatabaseReference()
                .child("posts");
        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postListAll.clear();
                postList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Post post = ds.getValue(Post.class);
                    if (post != null) {
                        postListAll.add(post);
                    }
                }
                postList.addAll(postListAll);
                recuperaPostsPesquisa(editPesquisa.getText().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void recuperaPostsPesquisa(String pesquisa) {
        postList.clear();
        for (Post post : postListAll) {
            if (post.getTitulo().toLowerCase().contains(pesquisa.toLowerCase().trim())) {
                postList.add(post);
            }
        }

        if (postList.isEmpty()) {
            textInfo.setVisibility(View.VISIBLE);
        } else {
            textInfo.setVisibility(View.GONE);
        }

        adapterBusca.notifyDataSetChanged();
    }

    private void configSearchView() {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recuperaPostsPesquisa(newText);
                return false;
            }
        });

        searchView.findViewById(androidx.appcompat.R.id.search_close_btn).setOnClickListener(v -> {
            limparPesquisa();
        });


    }

    private void limparPesquisa() {
        EditText searchText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchText.getText().clear();
        ocultarTeclado();
        searchView.clearFocus();
        textInfo.setVisibility(View.GONE);
    }


    private void configRV() {
        rvBusca.setLayoutManager(new LinearLayoutManager(getContext()));
        rvBusca.setHasFixedSize(true);
        adapterBusca = new AdapterBusca(postList, requireContext());
        rvBusca.setAdapter(adapterBusca);
    }

    private void ocultarTeclado() {
        ((InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                rvBusca.getWindowToken(), 0
        );
    }

    private void iniciaComponentes(View view) {
        searchView = view.findViewById(R.id.searchView);
        editPesquisa = searchView.findViewById(androidx.appcompat.R.id.search_src_text);

        textInfo = view.findViewById(R.id.textInfo);
        rvBusca = view.findViewById(R.id.rvBusca);
    }
}