package com.amansiol.docscanner

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    lateinit var fab_main: FloatingActionButton
    lateinit var fab1_cam: FloatingActionButton
    lateinit var fab2_view: FloatingActionButton
    lateinit var fab_open: Animation
    lateinit var fab_close: Animation
    lateinit var fab_clock: Animation
    lateinit var fab_anticlock: Animation
    lateinit var fab2_conversion: FloatingActionButton
    lateinit var textview_view: TextView
    lateinit var textview_cam: TextView
    lateinit var textview_convert: TextView
    var isOpen =false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setStatusBarColor(Color.parseColor("#7F3D5AFE"));
        setContentView(R.layout.activity_main)
        fab_main = findViewById(R.id.fab)
        fab1_cam = findViewById(R.id.cam_fab)
        fab2_view = findViewById(R.id.fab_view)
        fab2_conversion = findViewById(R.id.fab_convert)
        fab_open =AnimationUtils.loadAnimation(applicationContext, R.anim.fab_open)
        fab_close = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_close)
        fab_clock = AnimationUtils.loadAnimation(applicationContext, R.anim.rotate_fab_clock)
        fab_anticlock = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_rotate_anticlock)
        textview_view = findViewById(R.id.textview_cam)
        textview_cam = findViewById(R.id.textview_view)
        textview_convert= findViewById(R.id.textView_convert)

        fab_main.setOnClickListener(View.OnClickListener {
            if (isOpen) {
                textview_view.visibility = View.INVISIBLE
                textview_cam.visibility = View.INVISIBLE
                textview_convert.visibility = View.INVISIBLE
                fab2_conversion.startAnimation(fab_close)
                fab2_view.startAnimation(fab_close)
                fab1_cam.startAnimation(fab_close)
                fab_main.startAnimation(fab_anticlock)
                fab2_view.isClickable = false
                fab2_conversion.isClickable = false
                fab1_cam.isClickable = false
                isOpen = false
            } else {
                textview_view.visibility = View.VISIBLE
                textview_cam.visibility = View.VISIBLE
                textview_convert.visibility = View.VISIBLE
                fab2_conversion.startAnimation(fab_open)
                fab2_view.startAnimation(fab_open)
                fab1_cam.startAnimation(fab_open)
                fab_main.startAnimation(fab_clock)
                fab2_view.isClickable = true
                fab2_conversion.isClickable = true
                fab1_cam.isClickable = true
                isOpen = true
            }
        })

        fab2_view.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@MainActivity, ViewAllPdf::class.java)
            startActivity(intent)
        })

        fab1_cam.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@MainActivity, CameraActivity::class.java)
            startActivity(intent)
        })

        fab2_conversion.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@MainActivity, ConversionMenu::class.java)
            startActivity(intent)
        })

    }
}
