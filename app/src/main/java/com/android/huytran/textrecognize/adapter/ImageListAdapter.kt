package com.android.huytran.textrecognize.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.android.huytran.textrecognize.R
import com.android.huytran.textrecognize.model.FileImage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ImageListAdapter(val imageList: List<FileImage>, val context: Context, val onClickInterface: OnClickInterface?) : RecyclerView.Adapter<ImageListAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.image_list_row, parent, false)
        val viewHolder = ViewHolder(view)
        view.setOnClickListener {
            onClickInterface?.onViewHolderClick(viewHolder)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = imageList[position]
        item.let {
            val file = File(it.path)
            if (!file.exists()) return
            holder.imageView.setImageBitmap(
                    BitmapFactory.decodeFile(file.absolutePath)
            )

            holder.txtCreatedTimestamp.text = timestampToDate(it.createdTimestamp)
            holder.txtPath.text = it.path
            holder.fileImage = it
        }

    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val imageView = view.findViewById<ImageView>(R.id.img_view)
        val txtCreatedTimestamp = view.findViewById<TextView>(R.id.txt_created_timestamp)
        val txtPath = view.findViewById<TextView>(R.id.txt_path)
        lateinit var fileImage: FileImage

    }

    private fun timestampToDate(timestamp: Long) :String {
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return simpleDateFormat.format(
                Date(timestamp)
        )
    }

    interface OnClickInterface {
        fun onViewHolderClick(viewHolder: ViewHolder)
    }
}