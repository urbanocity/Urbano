package com.urbano.urbano;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class Hive {
    private static String TAG = "Urbano_v1";

    public static boolean isAuthenticated() {
        return null != FirebaseAuth.getInstance().getCurrentUser();
    }

    public static DatabaseReference getDb_ref() {
        return FirebaseDatabase.getInstance().getReference();
    }

    public static FirebaseAuth getAuth() {
        return FirebaseAuth.getInstance();
    }

    public static String getTAG() {
        return TAG;
    }

    public static String getUID() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
