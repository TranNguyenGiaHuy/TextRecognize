package com.android.huytran.textrecognize.fragment

import android.annotation.SuppressLint
import android.app.Fragment
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.huytran.textrecognize.R

class ImagePreviewFragment() : Fragment() {

    private lateinit var imageView: ImageView
    private lateinit var listView: ListView
    var bitmap: Bitmap? = null
    var textList: List<String> = arrayListOf()

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.image_preview_fragment, null, false)
        imageView = view.findViewById(R.id.imgView)
        listView = view.findViewById(R.id.listView)

        val header = inflater.inflate(R.layout.list_header, null, false)
        listView.addHeaderView(header)

        if (bitmap != null) {
            imageView.setImageBitmap(
                    bitmap
            )
        }

//        listView.setOnScrollListener(object : AbsListView.OnScrollListener {
//            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
//                if (listView.firstVisiblePosition == 0) {
//                    val firstChild = listView.getChildAt(0)
//                    var topY = 0
//                    if (firstChild != null) {
//                        topY = firstChild.top
//                    }
//                    imageView.y = topY * 0.5f
//                }
//            }
//
//            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
//            }
//        })

        listView.adapter = ArrayAdapter(context, R.layout.list_row, textList)

        return view
    }
}