package com.example.netflix.model;

import com.example.netflix.helper.FirebaseHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.List;

public class Categoria {

    private String id;
    private String nome;
    private List<Post> postList;

    public Categoria() {
    }

    public Categoria(String nome) {
        this.nome = nome;

        DatabaseReference categoriaRef = FirebaseHelper.getDatabaseReference();
        this.setId(categoriaRef.push().getKey());

        salvar();
    }

    private void salvar() {
        DatabaseReference categoriaRef = FirebaseHelper.getDatabaseReference()
                .child("categorias")
                .child(getId());
        categoriaRef.setValue(this);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Exclude
    public List<Post> getPostList() {
        return postList;
    }


    public void setPostList(List<Post> postList) {
        this.postList = postList;
    }

}
