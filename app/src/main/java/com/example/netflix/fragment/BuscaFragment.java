package com.example.netflix.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.netflix.R;
import com.example.netflix.adapter.AdapterBusca;
import com.example.netflix.adapter.AdapterCategoria;
import com.example.netflix.helper.FirebaseHelper;
import com.example.netflix.model.Categoria;
import com.example.netflix.model.Post;
import com.google.android.material.slider.Slider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BuscaFragment extends Fragment {

    private EditText searchView;
    private TextView textInfo;
    private ImageButton ibClose;

    private RecyclerView rvBusca;
    private AdapterBusca adapterBusca;
    private List<Post> postListAll = new ArrayList<>();
    private List<Post> postList = new ArrayList<>();


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
                adapterBusca.notifyDataSetChanged();
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

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                recuperaPostsPesquisa(s.toString());

                if(s.toString().isEmpty()){
                    ibClose.setVisibility(View.GONE);
                } else {
                    ibClose.setVisibility(View.VISIBLE);
                }
            }
        });

        searchView.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                ocultarTeclado();
            }
            return false;
        });

        ibClose.setOnClickListener(v -> {
            limparPesquisa();
        });
    }

    private void limparPesquisa() {
        searchView.getText().clear();
        ocultarTeclado();
        searchView.clearFocus();
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
        textInfo = view.findViewById(R.id.textInfo);
        ibClose = view.findViewById(R.id.ibClose);
        rvBusca = view.findViewById(R.id.rvBusca);

    }
}