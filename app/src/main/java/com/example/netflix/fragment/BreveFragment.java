package com.example.netflix.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.netflix.R;
import com.example.netflix.adapter.AdapterBreve;
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


public class BreveFragment extends Fragment {

    private RecyclerView rvPosts;
    private AdapterBreve adapterBreve;
    private final List<Post> postList = new ArrayList<>();

    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_breve, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        iniciaComponentes(view);
        configRV();
        recuperaPosts();
    }


    private void recuperaPosts() {
        DatabaseReference postRef = FirebaseHelper.getDatabaseReference()
                .child("posts");
        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Post post = ds.getValue(Post.class);
                    if (post != null) {
                       postList.add(post);
                    }
                }
                progressBar.setVisibility(View.GONE);
                adapterBreve.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configRV() {
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPosts.setHasFixedSize(true);
        adapterBreve = new AdapterBreve(postList);
        rvPosts.setAdapter(adapterBreve);
    }

    private void iniciaComponentes(View view){
        rvPosts = view.findViewById(R.id.rvPosts);
        progressBar = view.findViewById(R.id.progressBar);
    }
}