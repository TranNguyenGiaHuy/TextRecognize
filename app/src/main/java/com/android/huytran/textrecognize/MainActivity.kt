package com.android.huytran.textrecognize

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.android.huytran.textrecognize.fragment.CameraFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val cameraFragment = CameraFragment()
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.main_view, cameraFragment)
                .commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}
