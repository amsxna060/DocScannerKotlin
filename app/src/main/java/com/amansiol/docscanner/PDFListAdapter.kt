package com.amansiol.docscanner

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import java.io.File
import java.util.*

class PDFListAdapter(var context: Context) : RecyclerView.Adapter<PDFListAdapter.PDFListViewHolder>() {

    companion object {
        var fileToBeDeleted: File? = null
    }

    private fun sharePDF(file: File) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        val pdfUri: Uri = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".provider",
            file
        );
        sharingIntent.type = "application/pdf"
        sharingIntent.putExtra(Intent.EXTRA_STREAM, pdfUri)
        context.startActivity(Intent.createChooser(sharingIntent, "Share PDF using"))
    }

    private fun deleteFile(file: File) {
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context)
        val v = LayoutInflater.from(context).inflate(R.layout.delete_ask, null, false)
        alertDialogBuilder.setView(v)

        val deleteBtn: Button = v.findViewById(R.id.sure_delete)
        val cancelBtn: Button = v.findViewById(R.id.dont_delete)

        val dialog: AlertDialog = alertDialogBuilder.create()
        dialog.show()

        deleteBtn.setOnClickListener {
            file.delete()
            ViewAllPdf.pdfArray.remove(file)
            ViewAllPdf.pdfListAdapter.notifyDataSetChanged()
            Toast.makeText(context, "${file.name} deleted", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
    }

    override fun onBindViewHolder(holder: PDFListViewHolder, position: Int) {
        val file: File = ViewAllPdf.pdfArray[position]
        holder.fileNameTextView.text = file.name

        //Get the last modified date of the PDF file (creation date cannot be fetched)
        val date: Date = Date(file.lastModified())
        holder.fileCreationDate.text = date.toString()

        //Allow sharing of PDF when shareImageView is pressed
        holder.shareImageView.setOnClickListener(View.OnClickListener {
            sharePDF(file)
        })

        //delete PDF
        holder.deleteImageView.setOnClickListener(View.OnClickListener {
            fileToBeDeleted = file
            //Check whether we have the permission to delete the files on device by checking write external storage permission
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                deleteFile(file)
            } else {
                //Ask for permission
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    Constants.RC_WRITE_EXTERNAL_STORAGE
                )
            }
        })

        //To edit PDF
        holder.editImageView.setOnClickListener(View.OnClickListener {
            //to be implemented
            fileToBeDeleted = null
            val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context)
            val v = LayoutInflater.from(context).inflate(R.layout.rename, null, false)
            alertDialogBuilder.setView(v)

            val saveBtn: Button = v.findViewById(R.id.rename_my_file)
            val cancelBtn: Button = v.findViewById(R.id.rename_canceling)
            val editText: EditText = v.findViewById(R.id.rename_file_name_for_pdf)
            val dialog: AlertDialog = alertDialogBuilder.create()
            dialog.show()

            saveBtn.setOnClickListener {
                if (editText.text.toString().isEmpty()) {
                    Toast.makeText(context, "Enter a name", Toast.LENGTH_SHORT).show()
                } else {
                    val newName: String = editText.text.toString()
                    renameFile(newName, file)
                    dialog.dismiss()
                    notifyDataSetChanged()
                }
            }

            cancelBtn.setOnClickListener {
                dialog.dismiss()
            }
        })
        holder.popup_menu.setOnClickListener(View.OnClickListener {

            val popupMenu: PopupMenu = PopupMenu(context,holder.popup_menu)
            popupMenu.menuInflater.inflate(R.menu.popup_menu,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when(item.itemId) {
                    R.id.extract_text->
                        Toast.makeText(context, "You Clicked : " + item.title, Toast.LENGTH_SHORT).show()
                }
                true
            })
            popupMenu.show()
        })


        //In general onClick listener on a listItem to show the PDF
        holder.itemView.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, PdfViewActivity::class.java)
            intent.putExtra(Constants.INTENT_POSITION_KEY_NAME, position)
            intent.putExtra(Constants.IS_FROM_DEVICE, true)
            context.startActivity(intent)
            Animatoo.animateSplit(context)
        })
    }

    private fun renameFile(newName: String, file: File) {
        val directory: File = File(
            file.parentFile.absolutePath
        )
        val from = File(directory, file.name)
        val to = File(directory, newName.trim().toString() + ".pdf")
        from.renameTo(to)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PDFListViewHolder {
        val view: View = LayoutInflater.from(context).inflate(
            R.layout.recent_pdf_row,
            parent,
            false
        )
        return PDFListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return ViewAllPdf.pdfArray.size
    }

    class PDFListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fileNameTextView: TextView = itemView.findViewById(R.id.file_name)
        val fileCreationDate: TextView = itemView.findViewById(R.id.file_date)
        val fileThumbnail: ImageView = itemView.findViewById(R.id.thumbnail)
        val deleteImageView: ImageView = itemView.findViewById(R.id.file_del)
        val shareImageView: ImageView = itemView.findViewById(R.id.file_share)
        val editImageView: ImageView = itemView.findViewById(R.id.file_edit_name)
        val popup_menu: ImageView = itemView.findViewById(R.id.popup_menu)
    }
}