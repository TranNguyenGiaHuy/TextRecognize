package com.android.huytran.textrecognize.fragment

import android.annotation.SuppressLint
import android.app.Fragment
import android.content.ClipData
import android.content.ClipboardManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.DisplayMetrics
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import com.android.huytran.textrecognize.R
import com.cocosw.bottomsheet.BottomSheet
import com.ecloud.pulltozoomview.PullToZoomListViewEx
import android.R.attr.label
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.net.Uri


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
                    .sheet(index++, resources.getDrawable(R.drawable.ic_copy, null), "Copy '$text' to clipboard")
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
            bottomDialogBuilder.listener { _, which ->
                if (which == 0){
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip: ClipData = ClipData.newPlainText("text", text)
                    clipboard.primaryClip = clip
                    Toast.makeText(context,"copy success",Toast.LENGTH_SHORT).show()
                }
                if (which == 1 && !phoneList.isEmpty()){
                    val callIntent = Intent(Intent.ACTION_DIAL)
                    callIntent.data = Uri.parse("tel:"+phoneList[0])
                    startActivity(callIntent)
                }
                if (which == 1 && !mailList.isEmpty()){
                    val emailIntent = Intent(Intent.ACTION_SEND)
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(mailList[0]))
                    emailIntent.type = "message/rfc822"
                    startActivity(Intent.createChooser(emailIntent, "Choose an Email client :"))
                }
                if(which == 2){
                    val smsIntent = Intent(Intent.ACTION_SENDTO, null)
                    smsIntent.data = Uri.parse("smsto:"+phoneList[0])
                    startActivity(smsIntent)
                }
                if(which == 3){
                    val topUpIntent = Intent(Intent.ACTION_DIAL)
                    topUpIntent.data = Uri.parse("tel:*100*"+phoneList[0]+"%23")
                    startActivity(topUpIntent)
                }
            }
            bottomDialogBuilder.build().show()

        }

        retainInstance = true
        return view
    }
}