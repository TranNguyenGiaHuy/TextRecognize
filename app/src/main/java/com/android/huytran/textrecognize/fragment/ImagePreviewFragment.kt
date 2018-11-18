package com.android.huytran.textrecognize.fragment

import android.annotation.SuppressLint
import android.app.Fragment
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.FrameLayout
import android.widget.ImageView
import co.dift.ui.SwipeToAction
import com.android.huytran.textrecognize.R
import com.android.huytran.textrecognize.adapter.ImagePreviewAdapter
import com.android.huytran.textrecognize.recyclerView.ImagePreviewRecyclerView


class ImagePreviewFragment : Fragment() {

    private lateinit var imageView: ImageView
    private lateinit var recyclerView: ImagePreviewRecyclerView
    var bitmap: Bitmap? = null
    var textList: ArrayList<String> = arrayListOf()

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.image_preview_fragment, null, false)
        recyclerView = view.findViewById(R.id.recyclerView)

        val header = inflater.inflate(R.layout.list_header, null, false) as FrameLayout
        imageView = header.findViewById(R.id.imgView_header)

        val adapter = ImagePreviewAdapter<ImagePreviewAdapter.ImagePreviewViewHolder>(textList)

        val manager = LinearLayoutManager(context)
        manager.orientation = GridLayoutManager.VERTICAL

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap)
            recyclerView.mHeaderContainer = header
        }

        recyclerView.setAdapterAndLayoutManager(adapter, manager)

        val localDisplayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(localDisplayMetrics)
        val mScreenWidth = localDisplayMetrics.widthPixels
        val localObject = AbsListView.LayoutParams(mScreenWidth, (9.0f * (mScreenWidth / 16.0f)).toInt())
        recyclerView.setHeaderLayoutParams(localObject)

        SwipeToAction(recyclerView.pullRootView as RecyclerView, object : SwipeToAction.SwipeListener<String> {
            override fun onClick(itemData: String?) {

            }

            override fun swipeLeft(itemData: String?): Boolean {
                itemData?.toInt()?.let {
                    adapter.removeItem(it)
                }
                return true
            }

            override fun swipeRight(itemData: String?): Boolean {
                return true
            }

            override fun onLongClick(itemData: String?) {

            }
        })

        retainInstance = true
        return view
    }

}