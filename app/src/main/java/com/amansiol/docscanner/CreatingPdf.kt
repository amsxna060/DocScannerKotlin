package com.amansiol.docscanner

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_creating_pdf.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class CreatingPdf : AppCompatActivity() {

    companion object {
        val bitmapArray: ArrayList<Bitmap> = ArrayList()
        var bitmapFileArray: ArrayList<File> = ArrayList()
        lateinit var adapter: BitmapAdapter
    }

    override fun onBackPressed() {
        val intent: Intent = Intent(this, FilterActivity::class.java)
        startActivity(intent)
        finish()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creating_pdf)

        adapter = BitmapAdapter(this)

        captured_images_recyclerview.layoutManager = GridLayoutManager(this,2)
        captured_images_recyclerview.adapter = adapter

        startCapture.setOnClickListener(View.OnClickListener {
            val intent: Intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        })

        val itemTouchHelper = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
                0
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder
                ): Boolean {
                    val fromPos = viewHolder.adapterPosition
                    val toPos = target.adapterPosition
                    // move item in `fromPos` to `toPos` in adapter.

                    Collections.swap(bitmapArray,fromPos,toPos)
                    Collections.swap(bitmapFileArray,fromPos,toPos)
                    recyclerView.adapter?.notifyItemMoved(fromPos,toPos)
                    recyclerView.adapter?.notifyDataSetChanged()
                    return true // true if moved, false otherwise
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    // remove from adapter
                }
            })

        itemTouchHelper.attachToRecyclerView(captured_images_recyclerview)
    }

    fun savePdf(view: View) {
        var filename: String
        val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.MyTheme)
        val v: View = LayoutInflater.from(this).inflate(R.layout.save_pdf_alert_dialog,null, false)
        val editText: EditText = v.findViewById(R.id.entered_file_name_for_pdf)
        builder.setView(v)
            .setPositiveButton("Save") { _, _ ->
                if (editText.text.isEmpty()) {
                    Toast.makeText(this@CreatingPdf, "Please enter a name",Toast.LENGTH_SHORT).show()
                } else {
                    filename = editText.text.toString()

                    val task: MakePDFAsyncTask = MakePDFAsyncTask(this, bitmapFileArray,filename)
                    task.execute()

                }
            }
            .setNegativeButton("Cancel", null)

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    fun appendClicking(view: View) {
        val intent: Intent = Intent(this, CameraActivity::class.java)
        finish()
        startActivity(intent)
    }
}