package com.android.huytran.textrecognize.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import co.dift.ui.SwipeToAction
import com.android.huytran.textrecognize.R


class ImagePreviewAdapter<V : RecyclerView.ViewHolder>(private val stringList: ArrayList<String>) : RecyclerView.Adapter<V>() {

    companion object {
        const val EXTRA_ITEM_TYPE = 100
    }
    private var header: ExtraItem<V>? = null

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): V {
        if (header?.viewType == p1) return header?.v as V

        return ImagePreviewViewHolder(
                LayoutInflater.from(p0.context).inflate(R.layout.list_item, p0, false)
        ) as V
    }

    override fun getItemCount(): Int {
        return stringList.size
    }

    override fun onBindViewHolder(holder: V, p1: Int) {
        if (p1 >= 1 && (p1 - 1) < stringList.size) {
            val position = p1 - 1
            val viewHolder = holder as ImagePreviewViewHolder
            val stringItem = stringList[position]
            viewHolder.textView.text = stringItem
            viewHolder.data = position.toString()
        } else {
            val lp = holder.itemView.layoutParams as ViewGroup.LayoutParams
            holder.itemView.layoutParams = lp
        }

    }

    fun getHeader(): ExtraItem<V>? {
        return header
    }

    public fun addHeaderView(type: Int, headerView: V): ExtraItem<V> {
        val item = ExtraItem(headerView, type)
        addHeaderView(item)
        return item
    }

    public fun addHeaderView(headerView: ExtraItem<V>) {
        header = headerView
        notifyItemInserted(0)
    }

    fun removeHeaderView() {
        header = null
        notifyItemRemoved(0)
    }

    fun removeItem(index: Int) {
        stringList.removeAt(index)
        notifyItemRemoved(index)
        notifyItemRangeChanged(index, stringList.size)
//        notifyDataSetChanged()
    }

    open class ImagePreviewViewHolder(v: View) : SwipeToAction.ViewHolder<String>(v) {

        var textView: TextView = v.findViewById(R.id.title)

    }

    class ExtraItem<V : RecyclerView.ViewHolder>(val v: V, val viewType: Int)

}