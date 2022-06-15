package com.example.netflix.model;

import com.example.netflix.helper.FirebaseHelper;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.List;

public class Post implements Serializable {
    private String id;
    private String titulo;
    private List<String> generoList;
    private String elenco;
    private int ano;
    private String duracao;
    private String sinopse;
    private String urlImagem;

    public Post() {
        DatabaseReference postRef = FirebaseHelper.getDatabaseReference();
        setId(postRef.push().getKey());
    }

    public void salvar(){
        DatabaseReference postRef = FirebaseHelper.getDatabaseReference()
                .child("posts")
                .child(getId());
        postRef.setValue(this);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public List<String> getGeneroList() {
        return generoList;
    }

    public void setGeneroList(List<String> generoList) {
        this.generoList = generoList;
    }

    public String getElenco() {
        return elenco;
    }

    public void setElenco(String elenco) {
        this.elenco = elenco;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public String getDuracao() {
        return duracao;
    }

    public void setDuracao(String duracao) {
        this.duracao = duracao;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }
}