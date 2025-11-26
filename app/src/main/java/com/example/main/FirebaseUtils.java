package com.example.main;

import com.google.firebase.database.FirebaseDatabase;

public class FirebaseUtils {
    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance("https://main-e041b-default-rtdb.firebaseio.com/");
        }
        return mDatabase;
    }
}
