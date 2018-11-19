package com.android.huytran.textrecognize.fragment

import android.annotation.SuppressLint
import android.app.Fragment
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import android.content.Intent
import android.net.Uri


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
                itemData?.toInt()?.let { index ->
                    val text = textList[index]

                    val phoneList = text.split(' ').filter { s -> android.util.Patterns.PHONE.matcher(s).matches() }.toList()
                    val mailList = text.split(' ').filter { s -> android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches() }.toList()

                    val bottomSheetBuilder = BottomSheet.Builder(context)
                    bottomSheetBuilder.addItem(0, "Copy \"$text\" to clipboard", R.drawable.ic_copy)

                    phoneList.forEachIndexed { index, s ->
                        bottomSheetBuilder.addItem(index + 1, "Call \"$s\"", R.drawable.ic_phone)
                    }

                    mailList.forEachIndexed { index, s ->
                        bottomSheetBuilder.addItem(index + phoneList.size + 1, "Mail \"$s\"", R.drawable.ic_email)
                    }

                    bottomSheetBuilder.setOnItemClickListener { parent, view, position, id ->
                        when (id.toInt()) {
                            0 -> {
                                (context
                                        .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
                                        .primaryClip = ClipData.newPlainText("Text Recognize", text)
                            }
                            in 1..(phoneList.size) -> {
                                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:${phoneList[id.toInt() - 1]}"))
                                startActivity(intent)
                            }
                            in phoneList.size..phoneList.size + mailList.size -> {

                            }
                        }
                    }

                    bottomSheetBuilder.create().show()
                }
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