package com.android.huytran.textrecognize.fragment

import android.annotation.SuppressLint
import android.app.Fragment
import android.graphics.Bitmap
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.android.huytran.textrecognize.R
import com.cocosw.bottomsheet.BottomSheet
import com.ecloud.pulltozoomview.PullToZoomListViewEx

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
        listView.setOnItemClickListener { parent, _, position, _ ->
            val text = parent.getItemAtPosition(position).toString()
            val phoneList = text.split(' ').filter { s -> android.util.Patterns.PHONE.matcher(s).matches() }.toList()
            val mailList = text.split(' ').filter { s -> android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches() }.toList()

            val bottomDialogBuilder = BottomSheet.Builder(activity)
                    .title("Choose Action...")
            var index = 0
            bottomDialogBuilder
                    .sheet(index++, resources.getDrawable(R.drawable.ic_copy, null), "Copy $text to clipboard")
            phoneList.forEach {
                bottomDialogBuilder
                        .sheet(index++, resources.getDrawable(R.drawable.ic_phone, null), "Call $it")
                        .sheet(index++, resources.getDrawable(R.drawable.ic_sms, null), "Text $it")
                        .sheet(index++, resources.getDrawable(R.drawable.ic_add_code, null), "Top Up Your Phone: $it")
            }
            mailList.forEach {
                bottomDialogBuilder
                        .sheet(index++, resources.getDrawable(R.drawable.ic_mail, null), "Email to $it")
            }
            bottomDialogBuilder.build().show()
        }

        retainInstance = true
        return view
    }
}