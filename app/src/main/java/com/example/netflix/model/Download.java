package com.example.netflix.model;

import com.example.netflix.helper.FirebaseHelper;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class Download {

    public static void salvar(List<String> downloadList){
        DatabaseReference downloadRef = FirebaseHelper.getDatabaseReference()
                .child("downloads");
        downloadRef.setValue(downloadList);
    }

}
