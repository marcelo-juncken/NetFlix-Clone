package com.example.netflix.fragment;

import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.netflix.R;
import com.example.netflix.activity.DetalhesPostActivity;
import com.example.netflix.adapter.AdapterDownload;
import com.example.netflix.helper.FirebaseHelper;
import com.example.netflix.model.Download;
import com.example.netflix.model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class DownloadFragment extends Fragment implements AdapterDownload.OnClickListener {

    private final List<String> downloadsList = new ArrayList<>();
    private final List<Post> postList = new ArrayList<>();

    private RecyclerView rvPosts;
    private AdapterDownload adapterDownload;
    private ProgressBar progressBar;
    private TextView textInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_download, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        iniciaComponentes(view);
        configRV();
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperaDownloads();
    }

    private void recuperaDownloads() {
        DatabaseReference downloadsRef = FirebaseHelper.getDatabaseReference()
                .child("downloads");
        downloadsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                downloadsList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String download = ds.getValue(String.class);
                        if (download != null) {
                            downloadsList.add(download);
                            recuperaPosts(download);
                        }
                    }
                } else {
                    configText();
                    adapterDownload.notifyDataSetChanged();
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configText() {
        if (postList.isEmpty()) {
            textInfo.setText("Você não fez nenhum download");
        } else {
            textInfo.setText("");
        }
    }

    private void recuperaPosts(String idPost) {
        DatabaseReference postsRef = FirebaseHelper.getDatabaseReference()
                .child("posts")
                .child(idPost);
        postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post != null) {
                        postList.add(post);
                    }
                }
                adapterDownload.notifyDataSetChanged();
                configText();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configRV() {
        rvPosts.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvPosts.setHasFixedSize(true);
        adapterDownload = new AdapterDownload(postList, this);
        rvPosts.setAdapter(adapterDownload);
    }

    @Override
    public void onItemClickListener(Post post) {
        postList.remove(post);
        downloadsList.remove(post.getId());
        Download.salvar(downloadsList);
        adapterDownload.notifyDataSetChanged();
        configText();
    }

    private void iniciaComponentes(View view) {
        rvPosts = view.findViewById(R.id.rvPosts);
        progressBar = view.findViewById(R.id.progressBar);
        textInfo = view.findViewById(R.id.textInfo);
    }

}