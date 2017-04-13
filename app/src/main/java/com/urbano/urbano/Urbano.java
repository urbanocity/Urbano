package com.urbano.urbano;

import android.app.Application;

import com.digits.sdk.android.Digits;
import com.google.firebase.FirebaseApp;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import io.fabric.sdk.android.Fabric;



public class Urbano extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "GU4O0sxLeL4uNoGLFjEDcqHWE";
    private static final String TWITTER_SECRET = "gy7vHf99fvI0XsHap1y4rGf1MPe5UsAQF6Wueignk68Vk2BZ7c";

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new Digits.Builder().build());
        FirebaseApp.initializeApp(this);
    }
}
