package com.amansiol.docscanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView

class ConversionMenu : AppCompatActivity() {
    companion object{
        val list: ArrayList<String> = ArrayList()
        lateinit var Conversionadapter: ConversionAdapter
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversion_menu)
        list.clear()
        list.add("Doc To Pdf")
        list.add("Excel to Pdf")
        list.add("PPT to Pdf")
        list.add("Pdf to Doc")
        list.add("Pdf to Excel")
        list.add("Pdf to Image")
        list.add("Pdf to PPT")
        val recycler: RecyclerView=findViewById(R.id.ConversionRecyclerView)
        Conversionadapter= ConversionAdapter(this@ConversionMenu)
        recycler.adapter= Conversionadapter
    }
}