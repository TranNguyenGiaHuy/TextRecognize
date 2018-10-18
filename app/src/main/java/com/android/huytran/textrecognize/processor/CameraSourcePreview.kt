package com.android.huytran.textrecognize.processor

import android.Manifest
import android.content.Context
import android.content.res.Configuration
import android.support.annotation.RequiresPermission
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup
import com.google.android.gms.common.images.Size
import com.google.android.gms.vision.CameraSource
import java.io.IOException

class CameraSourcePreview(context: Context?, attrs: AttributeSet?) : ViewGroup(context, attrs) {
    private val TAG = "CameraSourcePreview"

    private var surfaceView: SurfaceView = SurfaceView(context)
    private var startRequested: Boolean = false
    var surfaceAvailable: Boolean = false
    private var cameraSource: CameraSource? = null
    private var graphicOverlay: GraphicOverlay<OcrGraphic>? = null

    init {
        surfaceView.holder.addCallback(SurfaceViewCallback())
        addView(surfaceView)
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    @Throws(IOException::class, SecurityException::class)
    fun start(cameraSource: CameraSource?) {
        cameraSource ?: stop()
        this.cameraSource = cameraSource
        if (cameraSource != null) {
            startRequested = true
            startIfReady()
        }
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    @Throws(IOException::class, SecurityException::class)
    fun start(cameraSource: CameraSource?, graphicOverlay: GraphicOverlay<OcrGraphic>) {
        this.graphicOverlay = graphicOverlay
        start(cameraSource)
    }

    fun stop() {
        cameraSource?.stop()
    }

    fun release() {
        cameraSource?.stop()
        cameraSource = null
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    @Throws(IOException::class, SecurityException::class)
    private fun startIfReady() {
        if (startRequested && surfaceAvailable) {
            cameraSource?.start(surfaceView.holder)
            graphicOverlay?.let {graphicOverlay ->
                val cameraSize = cameraSource?.previewSize ?: Size(0, 0)
                val min = Math.min(cameraSize.width, cameraSize.height)
                val max = Math.max(cameraSize.width, cameraSize.height)
                if (isPortraitMode()) {
                    graphicOverlay.setCameraInfo(min, max, cameraSource!!.cameraFacing)
                } else {
                    graphicOverlay.setCameraInfo(max, min, cameraSource!!.cameraFacing)
                }
                graphicOverlay.clear()
            }
            startRequested = false
        }
    }

    inner class SurfaceViewCallback: SurfaceHolder.Callback {
        override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            surfaceAvailable = true
            try {
                startIfReady()
            } catch (se: SecurityException) {
                se.printStackTrace()
            } catch (ioE: IOException) {
                ioE.printStackTrace()
            }
        }

        override fun surfaceDestroyed(holder: SurfaceHolder?) {
            surfaceAvailable = false
        }

        override fun surfaceCreated(holder: SurfaceHolder?) {

        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var previewWidth = cameraSource?.previewSize?.width ?: 320
        var previewHeight = cameraSource?.previewSize?.height ?: 320
        if (isPortraitMode()) previewWidth = previewHeight.also { previewHeight = previewWidth }
        val viewWidth = right - left
        val viewHeight = bottom - top
        val childWidth: Int
        val childHeight: Int
        var childXOffset = 0
        var childYOffset = 0
        val widthRatio = viewWidth.toFloat() / previewWidth.toFloat()
        val heightRatio = viewHeight.toFloat() / previewHeight.toFloat()

        if (widthRatio > heightRatio) {
            childWidth = viewWidth
            childHeight = (previewHeight.toFloat() * heightRatio).toInt()
            childYOffset = (childHeight - viewHeight) / 2
        } else {
            childWidth = (previewWidth.toFloat() * widthRatio).toInt()
            childHeight = viewHeight
            childXOffset = (childWidth - viewWidth) / 2
        }

        for (i in 0..childCount) {
            getChildAt(i)?.layout(
                    -1 * childXOffset,
                    -1 * childYOffset,
                    childWidth - childXOffset,
                    childHeight - childYOffset
            )
        }

        try {
            startIfReady()
        } catch (se: SecurityException) {
            se.printStackTrace()
        } catch (ioE: IOException) {
            ioE.printStackTrace()
        }
    }

    private fun isPortraitMode(): Boolean {
        val orientation = context.resources.configuration.orientation
        return when (orientation) {
            Configuration.ORIENTATION_PORTRAIT -> true
            Configuration.ORIENTATION_LANDSCAPE -> false
            else -> false
        }
    }
}