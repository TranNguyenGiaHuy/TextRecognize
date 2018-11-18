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
import com.ecloud.pulltozoomview.PullToZoomListViewEx
import android.widget.AbsListView
import android.util.DisplayMetrics

class ImagePreviewFragment : Fragment() {

    private lateinit var imageView: ImageView
    private lateinit var listView: PullToZoomListViewEx
    var bitmap: Bitmap? = null
    var textList: List<String> = arrayListOf()

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.image_preview_fragment, null, false)
        listView = view.findViewById(R.id.listView)

        val header = inflater.inflate(R.layout.list_header, null, false)
        imageView = header.findViewById(R.id.imgView_header)
        listView.headerView = header
        listView.isParallax = true
        listView.setZoomEnabled(true)

        if (bitmap != null) {
            imageView.setImageBitmap(
                    bitmap
            )
        }
        val localDisplayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(localDisplayMetrics)
        val mScreenWidth = localDisplayMetrics.widthPixels
        val localObject = AbsListView.LayoutParams(mScreenWidth, (9.0f * (mScreenWidth / 16.0f)).toInt())
        listView.setHeaderLayoutParams(localObject)

        listView.setAdapter(ArrayAdapter(context, R.layout.list_row, textList))

        retainInstance = true
        return view
    }
}