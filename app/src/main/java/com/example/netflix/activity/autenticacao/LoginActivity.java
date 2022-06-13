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
import com.example.netflix.helper.FirebaseHelper;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText editLogin, editSenha;
    private Button btnEntrar;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        iniciaComponentes();
        configCliques();
    }

    private void validaDados() {
        String login = editSenha.getText().toString().trim();
        String senha = editSenha.getText().toString().trim();

        if (!login.isEmpty()) {
            if (!senha.isEmpty()) {
                ocultarTeclado();
                progressBar.setVisibility(View.VISIBLE);
                loginConta(login, senha);
            } else {
                editSenha.requestFocus();
                editSenha.setError("Esse campo não pode estar vazio.");
            }
        } else {
            editLogin.requestFocus();
            editLogin.setError("Esse campo não pode estar vazio.");
        }

    }

    private void loginConta(String login, String senha) {
        FirebaseHelper.getAuth().signInWithEmailAndPassword(
                login, senha
        ).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                finish();
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
        findViewById(R.id.btnCadastrar).setOnClickListener(v -> {
            ocultarTeclado();
            startActivity(new Intent(this, CadastroActivity.class));
        });

        findViewById(R.id.imbVoltar).setOnClickListener(v -> finish());

        findViewById(R.id.btnEntrar).setOnClickListener(v -> validaDados());
    }

    private void iniciaComponentes() {
        editLogin = findViewById(R.id.editLogin);
        editSenha = findViewById(R.id.editSenha);
        btnEntrar = findViewById(R.id.btnEntrar);
        progressBar = findViewById(R.id.progressBar);
    }
}