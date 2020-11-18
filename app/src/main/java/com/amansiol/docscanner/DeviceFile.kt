package com.amansiol.docscanner

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import kotlin.reflect.typeOf

class DeviceFile : AppCompatActivity() {
    var type : String= ""
    companion object{
        val FileArray : ArrayList<File> = ArrayList()
        lateinit var conversionListAdapter : ConversionListAdapter
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == Constants.RC_READ_EXTERNAL_STORAGE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val task: ConversionListTask= ConversionListTask(this,type)
            task.execute()
        } else if(requestCode == Constants.RC_WRITE_EXTERNAL_STORAGE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,"Permission granted to write storage try your action again",Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_file)
        type =intent.getStringExtra("Type")
        val ConversionType :String=intent.getStringExtra("ConversionType")
        Toast.makeText(this,type,Toast.LENGTH_SHORT).show()
        FileArray.clear()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            val task: ConversionListTask= ConversionListTask(this,type)
            task.execute()
        } else {
            //We don't have the permission to read the files in device hence we ask for it
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                Constants.RC_READ_EXTERNAL_STORAGE)
        }
        val recyclerView : RecyclerView=findViewById(R.id.RecyclerViewSelect)
        conversionListAdapter= ConversionListAdapter(this)
        recyclerView.adapter= conversionListAdapter
    }
}