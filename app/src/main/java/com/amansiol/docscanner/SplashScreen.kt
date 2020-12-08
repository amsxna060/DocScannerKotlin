package com.amansiol.docscanner

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setStatusBarColor(Color.parseColor("#3D5AFE"));
        setContentView(R.layout.activity_splash_screen)
    }
}