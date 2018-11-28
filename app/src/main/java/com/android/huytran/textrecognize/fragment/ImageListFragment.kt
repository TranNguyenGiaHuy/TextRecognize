package com.android.huytran.textrecognize.fragment

import android.app.Fragment
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.huytran.textrecognize.R
import com.android.huytran.textrecognize.adapter.ImageListAdapter
import com.android.huytran.textrecognize.model.FileImage
import com.android.huytran.textrecognize.model.ImageContent
import io.realm.Realm
import io.realm.kotlin.where
import java.io.File

class ImageListFragment : Fragment(), ImageListAdapter.OnClickInterface {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()

        val view = inflater.inflate(R.layout.image_list_fragment, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = ImageListAdapter(
                realm.copyFromRealm(realm.where<FileImage>().findAll()),
                context,
                this
        )
        realm.commitTransaction()

        fab = view.findViewById(R.id.fab)
        fab.setOnClickListener {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.main_view, CameraFragment())
                    .addToBackStack(this.javaClass.simpleName)
                    .commit()
        }

        return view
    }

    override fun onViewHolderClick(viewHolder: ImageListAdapter.ViewHolder) {
        val imagePreviewFragment = ImagePreviewFragment()
        val file = File(viewHolder.fileImage.path)
        if (!file.exists()) return
        imagePreviewFragment.bitmap = BitmapFactory.decodeFile(file.absolutePath)

        val realm = Realm.getDefaultInstance()
//        realm.beginTransaction()
        imagePreviewFragment.textList = viewHolder.fileImage.imageContentList.map { imageContent -> imageContent.text }
//        realm.commitTransaction()

        fragmentManager
                .beginTransaction()
                .replace(R.id.main_view, imagePreviewFragment)
                .addToBackStack(this.javaClass.simpleName)
                .commit()
    }

}