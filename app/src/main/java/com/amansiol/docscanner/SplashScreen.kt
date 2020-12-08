package com.amansiol.docscanner

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setStatusBarColor(Color.parseColor("#3D5AFE"))
        setContentView(R.layout.activity_splash_screen)
        Handler().postDelayed({ startActivity(Intent(this@SplashScreen, MainActivity::class.java)) }, 1000)
    }
}