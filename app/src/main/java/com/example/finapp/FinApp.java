package com.example.finapp;

import android.app.Application;

import com.google.firebase.FirebaseApp;

public class FinApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}
