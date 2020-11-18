package com.amansiol.docscanner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.util.*

class ConversionListAdapter(var context: Context) : RecyclerView.Adapter<ConversionListAdapter.ConversionViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ConversionListAdapter.ConversionViewHolder {
        val view: View= LayoutInflater.from(context).inflate(R.layout.devicefile,parent,false)
        return ConversionViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConversionListAdapter.ConversionViewHolder, position: Int) {
        val file : File=DeviceFile.FileArray[position]
        holder.fileName.text=file.name
        val date : Date= Date(file.lastModified())
        holder.fileDate.text=date.toString()
        holder.fileThumbnail.text=file.extension
    }

    override fun getItemCount(): Int {
        return DeviceFile.FileArray.size
    }

    class ConversionViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val fileName: TextView = itemView.findViewById(R.id.file_name)
        val fileDate : TextView=itemView.findViewById(R.id.file_date)
        val fileThumbnail : TextView=itemView.findViewById(R.id.thumbnail)
    }

}