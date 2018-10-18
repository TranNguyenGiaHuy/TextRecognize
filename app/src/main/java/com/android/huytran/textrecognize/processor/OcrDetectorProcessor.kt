package com.android.huytran.textrecognize.processor

import android.util.SparseArray
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.text.TextBlock

class OcrDetectorProcessor(val graphicOverlay: GraphicOverlay<OcrGraphic>) : Detector.Processor<TextBlock> {

    override fun release() {
        graphicOverlay.clear()
    }

    override fun receiveDetections(p0: Detector.Detections<TextBlock>?) {
        graphicOverlay.clear()
        val items = p0?.detectedItems ?: SparseArray()
        for (i in 0 until items.size()) {
            val item = items.valueAt(i)
            if (item != null && item.value != null) {
                graphicOverlay.add(
                        OcrGraphic(
                                graphicOverlay,
                                item
                        )
                )
            }
        }
    }
}