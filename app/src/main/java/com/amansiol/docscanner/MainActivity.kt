package com.amansiol.docscanner

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private val fab_main=findViewById<FloatingActionButton>(R.id.fab)
    private val fab1_cam=findViewById<FloatingActionButton>(R.id.cam_fab)
    private val fab2_view=findViewById<FloatingActionButton>(R.id.fab_view)
    private val fab2_conversion=findViewById<FloatingActionButton>(R.id.fab_convert)
    private val fab_open=AnimationUtils.loadAnimation(applicationContext, R.anim.fab_open)
    private val fab_close= AnimationUtils.loadAnimation(applicationContext, R.anim.fab_close)
    private val fab_clock=AnimationUtils.loadAnimation(applicationContext, R.anim.rotate_fab_clock)
    private val fab_anticlock= AnimationUtils.loadAnimation(applicationContext, R.anim.fab_rotate_anticlock)
    var textview_view=findViewById<View>(R.id.textview_cam) as TextView
    val textview_cam= findViewById<View>(R.id.textview_view) as TextView
    val textview_convert= findViewById<View>(R.id.textView_convert) as TextView
    var isOpen =false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab_main.setOnClickListener(View.OnClickListener {
            if (isOpen) {
                textview_view.setVisibility(View.INVISIBLE)
                textview_cam.setVisibility(View.INVISIBLE)
                textview_convert.setVisibility(View.INVISIBLE)
                fab2_conversion.startAnimation(fab_close)
                fab2_view.startAnimation(fab_close)
                fab1_cam.startAnimation(fab_close)
                fab_main.startAnimation(fab_anticlock)
                fab2_view.setClickable(false)
                fab2_conversion.setClickable(false)
                fab1_cam.setClickable(false)
                isOpen = false
            } else {
                textview_view.setVisibility(View.VISIBLE)
                textview_cam.setVisibility(View.VISIBLE)
                textview_convert.setVisibility(View.VISIBLE)
                fab2_conversion.startAnimation(fab_open)
                fab2_view.startAnimation(fab_open)
                fab1_cam.startAnimation(fab_open)
                fab_main.startAnimation(fab_clock)
                fab2_view.setClickable(true)
                fab2_conversion.setClickable(true)
                fab1_cam.setClickable(true)
                isOpen = true
            }
        })


        fab2_view.setOnClickListener(View.OnClickListener {
            Toast.makeText(this,"view pdfs",Toast.LENGTH_LONG);
        })

        fab1_cam.setOnClickListener(View.OnClickListener {
            Toast.makeText(this,"camera",Toast.LENGTH_LONG);

        })
        fab2_conversion.setOnClickListener(View.OnClickListener {
            Toast.makeText(this,"Convert",Toast.LENGTH_LONG);
        })

    }
}