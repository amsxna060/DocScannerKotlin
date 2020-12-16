package com.amansiol.docscanner

import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView


class ConversionMenu : AppCompatActivity() {
    companion object{
        val list: ArrayList<String> = ArrayList()
        lateinit var Conversionadapter: ConversionAdapter
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w: Window = window
            w.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
        setContentView(R.layout.activity_conversion_menu)
        list.clear()
        list.add("Doc To Pdf")
        list.add("Excel to Pdf")
        list.add("PPT to Pdf")
        list.add("Pdf to Doc")
        list.add("Pdf to Excel")
        list.add("Pdf to Image")
        list.add("Pdf to PPT")
        list.add("Edit a PDF")
        val recycler: RecyclerView=findViewById(R.id.ConversionRecyclerView)
        Conversionadapter= ConversionAdapter(this@ConversionMenu)
        recycler.adapter= Conversionadapter
    }
}