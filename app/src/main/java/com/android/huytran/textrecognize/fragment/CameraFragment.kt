package com.android.huytran.textrecognize.fragment

import android.Manifest
import android.app.Fragment
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.android.huytran.textrecognize.R
import com.android.huytran.textrecognize.model.FileImage
import com.android.huytran.textrecognize.model.ImageContent
import com.android.huytran.textrecognize.processor.*
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.text.TextRecognizer
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import io.realm.Realm
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception


class CameraFragment : Fragment() {

    private val cameraRequestCode = 100
    private val storageRequestCode = 200

    private var cameraSource: CameraSource? = null
    private lateinit var cameraSourcePreview: CameraSourcePreview
    private lateinit var graphicOverlay: GraphicOverlay<OcrGraphic>
    private lateinit var captureButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.camera_fragment, container, false)
        cameraSourcePreview = view.findViewById(R.id.cameraPreview)
        graphicOverlay = view.findViewById(R.id.cameraGraphicOverlay)
        captureButton = view.findViewById(R.id.captureBtn)
        captureButton.setOnClickListener { captureImage() }

        checkCameraPermissionAndStartIfNeeded()

        return view
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode != cameraRequestCode) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }
        val grantResult = grantResults.firstOrNull()
        if (grantResult != null && grantResult == PackageManager.PERMISSION_GRANTED) {
            if (context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            createCameraSource()
        }
    }

    private fun createCameraSource() {
        val textRecognizer = TextRecognizer.Builder(context!!).build()
        textRecognizer.setProcessor(
                OcrDetectorProcessor(graphicOverlay)
        )
        if (textRecognizer.isOperational) {
            cameraSource = CameraSource.Builder(context!!, textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setAutoFocusEnabled(true)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(60F)
                    .build()
        }
    }

    @Throws(SecurityException::class)
    private fun startCameraSource() {
        cameraSource?.let {
            try {
                cameraSourcePreview.start(
                        cameraSource,
                        graphicOverlay
                )
            } catch (e: IOException) {
                e.printStackTrace()
                it.release()
                cameraSource = null
            }
        }
    }

    private fun captureImage() {
        cameraSource?.takePicture(null, { bytes ->

            val textList = arrayListOf<String>()
            var bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            when (Exif.getOrientation(bytes)) {
                0 -> bitmap = rotateImage(bitmap, 0f)
                90 -> bitmap = rotateImage(bitmap, 90f)
                180 -> bitmap = rotateImage(bitmap, 180f)
                270 -> bitmap = rotateImage(bitmap, 270f)
            }

            val image = FirebaseVisionImage.fromBitmap(
                    bitmap
            )
//            val options = FirebaseVisionCloudDetectorOptions.Builder()
//                    .setModelType(FirebaseVisionCloudDetectorOptions.LATEST_MODEL)
//                    .setMaxResults(15)
//                    .build()
            FirebaseVision.getInstance()
                    .onDeviceTextRecognizer
                    .processImage(image)
                    .addOnSuccessListener { firebaseVisionText ->
                        println(firebaseVisionText.text)
                        textList.addAll(
                                firebaseVisionText.textBlocks.map { textBlock -> textBlock.text }
                        )

                        // save file to storage
                        val folder =  File("${Environment.getExternalStorageDirectory()}${File.separator}TextRecognize${File.separator}")
                        if (!folder.exists()) {
                            folder.mkdirs()
                        }

                        val file = File(folder.path, "${System.currentTimeMillis()}.jpg")
                        if (file.exists()) {
                            file.delete()
                        }
                        val fileOutputStream = FileOutputStream(file)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
                        fileOutputStream.close()

                        // save data to db
                        try {
                            val realm = Realm.getDefaultInstance()
                            realm.beginTransaction()
                            val fileImage = FileImage()
                            fileImage.createdTimestamp = System.currentTimeMillis()
                            fileImage.path = file.path
                            realm.insertOrUpdate(fileImage)
                            realm.commitTransaction()

                            val imageContentList = textList.map {
                                val imageContent = ImageContent()
                                imageContent.text = it
                                imageContent.fileImage = fileImage
                                fileImage.imageContentList.add(imageContent)
                                imageContent
                            }
                            realm.beginTransaction()
                            realm.insertOrUpdate(imageContentList)
                            realm.insertOrUpdate(fileImage)
                            realm.commitTransaction()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Realm.getDefaultInstance().close()
                        }

                        fragmentManager.popBackStack()
                    }
                    .addOnFailureListener {
                        it.printStackTrace()
                    }
        })
    }

    override fun onResume() {
        super.onResume()
        startCameraSource()
    }

    override fun onPause() {
        super.onPause()
        cameraSourcePreview.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraSourcePreview.release()
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height,
                matrix, true)
    }

    private fun checkCameraPermissionAndStartIfNeeded() {
        if (context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                    cameraRequestCode
            )
            return
        }
        createCameraSource()
    }

}