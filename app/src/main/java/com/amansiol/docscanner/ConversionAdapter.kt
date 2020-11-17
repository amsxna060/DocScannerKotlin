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
        when(text){
            "Doc To Pdf"->{
                holder.tpyeText.text=text
                holder.typeImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.doctopdf))
            }
            "Image To Pdf"->{
                holder.tpyeText.text=text
                holder.typeImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.imgtopdf))
            }
            "Excel to Pdf"->{
                holder.tpyeText.text=text
                holder.typeImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.extopdf))
            }
            "PPT to Pdf"->{
                holder.tpyeText.text=text
                holder.typeImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ppttopdf))
            }
            "Pdf to Doc"->{
                holder.tpyeText.text=text
                holder.typeImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.pdftodoc))
            }
            "Pdf to Excel"->{
                holder.tpyeText.text=text
                holder.typeImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.pdftoex))
            }
            "Pdf to Image"->{
                holder.tpyeText.text=text
                holder.typeImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.pdftoimage))
            }
            "Pdf to PPT"->{
                holder.tpyeText.text=text
                holder.typeImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.pdftoppt))
            }
        }
        holder.itemView.setOnClickListener(View.OnClickListener {
            Toast.makeText(context,text,Toast.LENGTH_SHORT).show()
        })
    }
    class ConversionViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val typeImage: ImageView=itemView.findViewById(R.id.imageitem)
        val tpyeText: TextView=itemView.findViewById(R.id.textItem)
    }
}