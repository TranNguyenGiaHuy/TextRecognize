package com.android.huytran.textrecognize

import android.app.Activity
import android.os.Bundle
import com.android.huytran.textrecognize.fragment.CameraFragment

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            val cameraFragment = CameraFragment()
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.main_view, cameraFragment)
                    .commit()
        }
    }

    override fun onBackPressed() {
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}
