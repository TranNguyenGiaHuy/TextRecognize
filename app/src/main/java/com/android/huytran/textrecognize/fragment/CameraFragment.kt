package com.android.huytran.textrecognize.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.view.*
import com.android.huytran.textrecognize.R
import com.android.huytran.textrecognize.processor.CameraSourcePreview
import com.android.huytran.textrecognize.processor.GraphicOverlay
import com.android.huytran.textrecognize.processor.OcrDetectorProcessor
import com.android.huytran.textrecognize.processor.OcrGraphic
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.text.TextRecognizer
import java.io.IOException

class CameraFragment : Fragment() {

    private val cameraRequestCode = 100

    private var cameraSource: CameraSource? = null
    private lateinit var cameraSourcePreview: CameraSourcePreview
    private lateinit var graphicOverlay: GraphicOverlay<OcrGraphic>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.camera_fragment, container, false)
        cameraSourcePreview = view.findViewById(R.id.cameraPreview)
        graphicOverlay = view.findViewById(R.id.cameraGraphicOverlay)

        createCameraSource()

        return view
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode != cameraRequestCode) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }
        val grantResult = grantResults.firstOrNull()
        if (grantResult != null && grantResult == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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
                    .setRequestedFps(2F)
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

}