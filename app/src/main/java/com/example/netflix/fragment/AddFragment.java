package com.example.netflix.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.netflix.R;
import com.example.netflix.activity.CategoriasActivity;
import com.example.netflix.helper.FirebaseHelper;
import com.example.netflix.model.Post;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AddFragment extends Fragment {

    private static final int REQUEST_GALERIA = 100;
    private static final int REQUEST_CATEGORIA = 150;
    private ImageView image, imageFake;
    private EditText editTitulo, editElenco, editAno, editDuracao, editSinopse;
    private Button btnCadastrar, editGenero;
    private ProgressBar progressBar;

    private String caminhoImagem = null;

    private List<String> categoriaEscolhidas = new ArrayList<>();

    private AlertDialog dialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        iniciaComponentes(view);
        configCliques();
    }


    private void validaDados() {
        String titulo = editTitulo.getText().toString().trim();
        String genero = editGenero.getText().toString().trim();
        String elenco = editElenco.getText().toString().trim();
        String sinopse = editSinopse.getText().toString().trim();
        String duracao = editDuracao.getText().toString().trim();

        int ano = 0;
        if (!editAno.getText().toString().isEmpty()) {
            ano = Integer.parseInt(editAno.getText().toString().trim());
        }


        if (!titulo.isEmpty()) {
            if (!genero.isEmpty()) {
                if (!elenco.isEmpty()) {
                    if (ano >= 1800) {
                        if (!duracao.isEmpty()) {
                            if (!sinopse.isEmpty()) {
                                ocultarTeclado();
                                if (caminhoImagem != null) {
                                    progressBar.setVisibility(View.VISIBLE);
                                    btnCadastrar.requestFocus();
                                    btnCadastrar.setEnabled(false);

                                    Post post = new Post();
                                    post.setTitulo(titulo);
                                    post.setGeneroList(categoriaEscolhidas);
                                    post.setElenco(elenco);
                                    post.setAno(ano);
                                    post.setDuracao(duracao);
                                    post.setSinopse(sinopse);


                                    salvarImagemFirebase(post);
                                } else {
                                    editTitulo.requestFocus();
                                    showDialog("Atenção! Foto do poster necessária.", "É obrigatório ter uma foto para a capa.");
                                }
                            } else {
                                editSinopse.requestFocus();
                                editSinopse.setError("Esse campo não pode estar vazio.");
                            }
                        } else {
                            editDuracao.requestFocus();
                            editDuracao.setError("Esse campo não pode estar vazio.");
                        }
                    } else {
                        editAno.requestFocus();
                        editAno.setError("O ano tem que ser de 1800 para cima.");
                    }
                } else {
                    editElenco.requestFocus();
                    editElenco.setError("Esse campo não pode estar vazio.");
                }
            } else {
                editGenero.requestFocus();
                ocultarTeclado();
                showDialog("Atenção!", "É necessário escolher pelo menos 1 categoria");
            }
        } else {
            editTitulo.requestFocus();
            editTitulo.setError("Esse campo não pode estar vazio.");
        }

    }

    private void salvarImagemFirebase(Post post) {
        StorageReference storageReference = FirebaseHelper.getStorageReference()
                .child("imagens")
                .child("posts")
                .child(post.getId() + ".jpeg");

        UploadTask uploadTask = storageReference.putFile(Uri.parse(caminhoImagem));
        uploadTask.addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String urlImagem = task.getResult().toString();
                post.setUrlImagem(urlImagem);
                post.salvar();
                limparCampos();
            } else {
                Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        })).addOnFailureListener(e -> {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        });


    }

    private void showDialog(String titulo, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                requireActivity(), R.style.CustomAlertDialog
        );

        View view = getLayoutInflater().inflate(R.layout.dialog_info, null);
        builder.setView(view);

        TextView textTitulo = view.findViewById(R.id.textTitulo);
        textTitulo.setText(titulo);

        TextView textMensagem = view.findViewById(R.id.textMensagem);
        textMensagem.setText(msg);

        Button btnOK = view.findViewById(R.id.btnOK);
        btnOK.setOnClickListener(v -> dialog.dismiss());

        dialog = builder.create();
        dialog.show();

    }

    private void limparCampos() {
        progressBar.setVisibility(View.GONE);
        image.setImageBitmap(null);
        imageFake.setVisibility(View.VISIBLE);
        caminhoImagem = null;

        categoriaEscolhidas.clear();
        editTitulo.getText().clear();
        editGenero.setText("");
        editElenco.getText().clear();
        editSinopse.getText().clear();
        editAno.getText().clear();
        editDuracao.getText().clear();
        btnCadastrar.setEnabled(true);
    }


    // ---------------------------------------------- Abrir foto
    private void verificaPermissaoGaleria() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                abrirGaleria();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(getContext(), "Permissão negada.", Toast.LENGTH_SHORT).show();
            }
        };

        showDialogPermissaoGaleria(permissionListener);
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALERIA);

    }

    private void showDialogPermissaoGaleria(PermissionListener listener) {
        TedPermission.create()
                .setPermissionListener(listener)
                .setDeniedTitle("Permissões negadas.")
                .setDeniedMessage("Você negou as permissões para acessar a galeria do dispositivo. Deseja permitir?")
                .setDeniedCloseButtonText("Não")
                .setGotoSettingButtonText("Sim")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_GALERIA) {
                Uri localImagemSelecionada = null;
                if (data != null) {
                    localImagemSelecionada = data.getData();
                }
                caminhoImagem = localImagemSelecionada.toString();
                try {
                    Bitmap imagem;
                    if (Build.VERSION.SDK_INT < 31) {
                        imagem = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), localImagemSelecionada);
                    } else {
                        ImageDecoder.Source source = ImageDecoder.createSource(requireContext().getContentResolver(), localImagemSelecionada);
                        imagem = ImageDecoder.decodeBitmap(source);
                    }
                    imageFake.setVisibility(View.GONE);
                    image.setImageBitmap(imagem);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_CATEGORIA){
                if (data != null) {
                    categoriaEscolhidas = data.getStringArrayListExtra("categoriasEscolhidas");
                    editGenero.setText(TextUtils.join(", ",categoriaEscolhidas));
                }

            }

        }
    }

    private void ocultarTeclado() {
        ((InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                btnCadastrar.getWindowToken(), 0
        );
    }


    // ------------------------------------- Configura objetos
    private void configCliques() {
        image.setOnClickListener(v -> verificaPermissaoGaleria());
        btnCadastrar.setOnClickListener(v -> validaDados());
        editGenero.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), CategoriasActivity.class);
            intent.putExtra("categoriasEscolhidas", (Serializable) categoriaEscolhidas);
            startActivityForResult(intent, REQUEST_CATEGORIA);
        });
    }

    private void iniciaComponentes(View view) {
        image = view.findViewById(R.id.image);
        imageFake = view.findViewById(R.id.imageFake);
        editTitulo = view.findViewById(R.id.editTitulo);
        editGenero = view.findViewById(R.id.editGenero);
        editElenco = view.findViewById(R.id.editElenco);
        editAno = view.findViewById(R.id.editAno);
        editDuracao = view.findViewById(R.id.editDuracao);
        editSinopse = view.findViewById(R.id.editSinopse);
        btnCadastrar = view.findViewById(R.id.btnCadastrar);
        progressBar = view.findViewById(R.id.progressBar);
    }
}