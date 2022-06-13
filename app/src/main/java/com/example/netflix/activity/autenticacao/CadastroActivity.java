package com.example.netflix.activity.autenticacao;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.netflix.R;
import com.example.netflix.activity.MainActivity;
import com.example.netflix.helper.FirebaseHelper;
import com.google.firebase.database.DatabaseReference;

public class CadastroActivity extends AppCompatActivity {

    private EditText editLogin, editSenha;
    private Button btnContinuar;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        configCliques();
        iniciaComponentes();
    }

    private void validaDados() {
        String login = editLogin.getText().toString().trim();
        String senha = editSenha.getText().toString().trim();

        if (!login.isEmpty()) {
            if (!senha.isEmpty()) {
                ocultarTeclado();
                progressBar.setVisibility(View.VISIBLE);
                cadastraConta(login, senha);
            } else {
                editSenha.requestFocus();
                editSenha.setError("Esse campo não pode estar vazio.");
            }
        } else {
            editLogin.requestFocus();
            editLogin.setError("Esse campo não pode estar vazio.");
        }

    }

    private void cadastraConta(String login, String senha) {
        FirebaseHelper.getAuth().createUserWithEmailAndPassword(
                login, senha
        ).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                finish();
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }else{
                progressBar.setVisibility(View.GONE);
                ocultarTeclado();
                Toast.makeText(this, FirebaseHelper.validaErros(task.getException().getMessage()), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void ocultarTeclado() {
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                editLogin.getWindowToken(), 0
        );
    }

    private void configCliques() {
        findViewById(R.id.btnLogin).setOnClickListener(v -> finish());
        findViewById(R.id.btnContinuar).setOnClickListener(v -> validaDados());
    }

    private void iniciaComponentes() {
        editLogin = findViewById(R.id.editLogin);
        editSenha = findViewById(R.id.editSenha);
        btnContinuar = findViewById(R.id.btnContinuar);
        progressBar = findViewById(R.id.progressBar);
    }

}