package com.amansiol.docscanner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView

class BitmapAdapter(var context: Context) : RecyclerView.Adapter<BitmapAdapter.BitmapViewHolder>() {

    override fun onBindViewHolder(holder: BitmapViewHolder, position: Int) {
        val positionText = "${position + 1}"
        holder.photoNumber.text = positionText
        holder.clickedImage.setImageBitmap(CreatingPdf.bitmapArray[position])
        holder.removeImageView.setOnClickListener(View.OnClickListener {
            CreatingPdf.bitmapArray.removeAt(position)
            CreatingPdf.bitmapFileArray.removeAt(position)
            CreatingPdf.adapter.notifyDataSetChanged()
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BitmapViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.rowimage,parent,false)
        return BitmapViewHolder(view)
    }

    override fun getItemCount(): Int {
        return CreatingPdf.bitmapArray.size
    }

    class BitmapViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clickedImage: AppCompatImageView = itemView.findViewById<AppCompatImageView>(R.id.imageitem)
        val photoNumber: TextView = itemView.findViewById(R.id.photo_number)
        val removeImageView: ImageView = itemView.findViewById(R.id.remove_page_from_pdf)
    }
}