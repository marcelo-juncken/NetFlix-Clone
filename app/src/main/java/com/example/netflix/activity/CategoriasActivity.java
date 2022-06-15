package com.example.netflix.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.netflix.R;
import com.example.netflix.adapter.AdapterCategoriasLista;
import com.example.netflix.helper.FirebaseHelper;
import com.example.netflix.model.Categoria;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CategoriasActivity extends AppCompatActivity implements AdapterCategoriasLista.OnClick {

    private AdapterCategoriasLista adapterCategoriasLista;
    private final List<Categoria> categoriaList = new ArrayList<>();
    private List<String> categoriasEscolhidas = new ArrayList<>();
    private RecyclerView rvCategoriasLista;

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            categoriasEscolhidas = bundle.getStringArrayList("categoriasEscolhidas");
        }
        iniciaComponentes();
        configCliques();

        configRV();

        recuperaCategorias();
    }


    private void salvaDados() {
        if (categoriasEscolhidas.size() > 0) {
            Intent intent = new Intent();
            intent.putExtra("categoriasEscolhidas", (Serializable) categoriasEscolhidas);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            showDialog();
        }
    }

    private void recuperaCategorias() {
        DatabaseReference categoriasRef = FirebaseHelper.getDatabaseReference()
                .child("categorias");
        categoriasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoriaList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Categoria categoria = ds.getValue(Categoria.class);
                        if (categoria != null) {
                            categoriaList.add(categoria);
                        }
                    }
                    adapterCategoriasLista.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                this, R.style.CustomAlertDialog
        );

        View view = getLayoutInflater().inflate(R.layout.dialog_info, null);
        builder.setView(view);

        TextView textTitulo = view.findViewById(R.id.textTitulo);
        textTitulo.setText("Atenção!");

        TextView textMensagem = view.findViewById(R.id.textMensagem);
        textMensagem.setText("É necessário escolher pelo menos 1 categoria.");

        Button btnOK = view.findViewById(R.id.btnOK);
        btnOK.setOnClickListener(v -> dialog.dismiss());

        dialog = builder.create();
        dialog.show();

    }

    private void configRV() {
        rvCategoriasLista.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        rvCategoriasLista.setHasFixedSize(true);
        adapterCategoriasLista = new AdapterCategoriasLista(categoriaList, categoriasEscolhidas,this);
        rvCategoriasLista.setAdapter(adapterCategoriasLista);
    }

    @Override
    public void onclick(Categoria categoria, Boolean status) {
        if (status) {
            if (!categoriasEscolhidas.contains(categoria.getNome())) {
                categoriasEscolhidas.add(categoria.getNome());
            }
        } else {
            categoriasEscolhidas.remove(categoria.getNome());
        }


    }

    private void configCliques() {
        findViewById(R.id.ibVoltar).setOnClickListener(v -> finish());
        findViewById(R.id.ibOK).setOnClickListener(v -> salvaDados());
    }

    private void iniciaComponentes() {
        rvCategoriasLista = findViewById(R.id.rvCategoriasLista);
    }


}