package com.example.netflix.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.netflix.R;
import com.example.netflix.adapter.AdapterPost;
import com.example.netflix.helper.FirebaseHelper;
import com.example.netflix.model.Download;
import com.example.netflix.model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DetalhesPostActivity extends AppCompatActivity {

    private ImageView image, imgDownload;
    private TextView textNomePoster, textAno, textDuracao, textSinopse, textElenco, textBaixar;
    private ConstraintLayout clAssistir, clBaixar;
    private RecyclerView rvSemelhantes;

    private Post postSelecionado;
    private final List<Post> postList = new ArrayList<>();
    private final List<String> downloadsList = new ArrayList<>();
    private AdapterPost adapterPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_post);

        iniciaComponentes();
        configCliques();
        configDados();
        configRV();
        recuperaPosts();
        recuperaDownloads();
    }

    private void configDownloads() {
        if (downloadsList.contains(postSelecionado.getId())) {
            textBaixar.setText("Remover dos downloads");
            imgDownload.setImageResource(R.drawable.ic_download_done);
        } else {
            textBaixar.setText("Baixar");
            imgDownload.setImageResource(R.drawable.ic_download);
        }
    }

    private void configDownloadList() {
        if (downloadsList.contains(postSelecionado.getId())) {
            downloadsList.remove(postSelecionado.getId());
        } else {
            downloadsList.add(postSelecionado.getId());
        }
        Download.salvar(downloadsList);
        configDownloads();
    }

    private void recuperaDownloads() {
        DatabaseReference downloadsRef = FirebaseHelper.getDatabaseReference()
                .child("downloads");
        downloadsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                downloadsList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String download = ds.getValue(String.class);
                        if (download != null) {
                            downloadsList.add(download);
                        }
                    }
                }
                configDownloads();
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
                postList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Post postTemp = ds.getValue(Post.class);
                    if (postTemp != null && !postTemp.getTitulo().equals(postSelecionado.getTitulo())) {
                        if (!Collections.disjoint(postTemp.getGeneroList(), postSelecionado.getGeneroList())) {
                            postList.add(0, postTemp);
                        }
                    }
                }
                adapterPost.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void configRV() {
        rvSemelhantes.setLayoutManager(new GridLayoutManager(this, 3));
        rvSemelhantes.setHasFixedSize(true);
        adapterPost = new AdapterPost(postList, this);
        rvSemelhantes.setAdapter(adapterPost);
    }

    private void configDados() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            postSelecionado = (Post) bundle.getSerializable("postSelecionado");
        }

        Picasso.get().load(postSelecionado.getUrlImagem()).into(image);
        textNomePoster.setText(postSelecionado.getTitulo());
        textAno.setText(String.valueOf(postSelecionado.getAno()));
        textDuracao.setText(postSelecionado.getDuracao());
        textSinopse.setText(postSelecionado.getSinopse());
        textElenco.setText(postSelecionado.getElenco());
    }

    private void configCliques() {
        findViewById(R.id.ibVoltar).setOnClickListener(v -> finish());

        textSinopse.setOnClickListener(v -> {
            if (textSinopse.getMaxLines() == 3) {
                textSinopse.setMaxLines(Integer.MAX_VALUE);
            } else {
                textSinopse.setMaxLines(3);
            }
        });

        clBaixar.setOnClickListener(v -> configDownloadList());
    }

    private void iniciaComponentes() {
        image = findViewById(R.id.image);
        imgDownload = findViewById(R.id.imgDownload);
        textBaixar = findViewById(R.id.textBaixar);
        textNomePoster = findViewById(R.id.textNomePoster);
        textAno = findViewById(R.id.textAno);
        textDuracao = findViewById(R.id.textDuracao);
        textSinopse = findViewById(R.id.textSinopse);
        textElenco = findViewById(R.id.textElenco);
        clAssistir = findViewById(R.id.clAssistir);
        clBaixar = findViewById(R.id.clBaixar);
        rvSemelhantes = findViewById(R.id.rvSemelhantes);

    }
}