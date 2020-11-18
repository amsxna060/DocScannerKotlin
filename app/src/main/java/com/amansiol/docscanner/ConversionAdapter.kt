package com.amansiol.docscanner

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class ConversionAdapter(var context: Context): RecyclerView.Adapter<ConversionAdapter.ConversionViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ConversionAdapter.ConversionViewHolder {
        val view: View=LayoutInflater.from(context).inflate(R.layout.conmenuitem,parent,false)
        return ConversionViewHolder(view)
    }
    override fun getItemCount(): Int {
        return ConversionMenu.list.size
    }
    override fun onBindViewHolder(holder: ConversionAdapter.ConversionViewHolder, position: Int) {
        val text: String=ConversionMenu.list[position]
        var type : String=".pdf"
        var ConversionType : String=".pdf"
        when(text){
            "Doc To Pdf"->{
                holder.tpyeText.text=text
                type=".docx"
                ConversionType=".pdf"
                holder.typeImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.doctopdf))
            }
            "Excel to Pdf"->{
                holder.tpyeText.text=text
                type=".xlsx"
                ConversionType=".pdf"
                holder.typeImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.extopdf))
            }
            "PPT to Pdf"->{
                holder.tpyeText.text=text
                type=".ppt"
                ConversionType=".pdf"
                holder.typeImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ppttopdf))
            }
            "Pdf to Doc"->{
                holder.tpyeText.text=text
                type=".pdf"
                ConversionType=".doc"
                holder.typeImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.pdftodoc))
            }
            "Pdf to Excel"->{
                holder.tpyeText.text=text
                type=".pdf"
                ConversionType=".xlsx"
                holder.typeImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.pdftoex))
            }
            "Pdf to Image"->{
                holder.tpyeText.text=text
                type=".pdf"
                ConversionType=".jpg"
                holder.typeImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.pdftoimage))
            }
            "Pdf to PPT"->{
                holder.tpyeText.text=text
                type=".pdf"
                ConversionType=".ppt"
                holder.typeImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.pdftoppt))
            }
        }
        holder.itemView.setOnClickListener(View.OnClickListener {
            val intent: Intent= Intent(context,DeviceFile::class.java)
            intent.putExtra("Type",type)
            intent.putExtra("ConversionType",ConversionType)
            context.startActivity(intent)
        })
    }
    class ConversionViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val typeImage: ImageView=itemView.findViewById(R.id.imageitem)
        val tpyeText: TextView=itemView.findViewById(R.id.textItem)
    }
}