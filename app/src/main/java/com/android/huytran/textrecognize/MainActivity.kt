package com.android.huytran.textrecognize

import android.app.Activity
import android.os.Bundle
import com.android.huytran.textrecognize.fragment.ImageListFragment
import io.realm.Realm
import io.realm.RealmConfiguration

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
//            val cameraFragment = CameraFragment()
            val imageListFragment = ImageListFragment()
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.main_view, imageListFragment)
                    .commit()
            Realm.init(this)
            val realmConfiguration = RealmConfiguration.Builder()
                    .name("textrecognize.realm")
                    .deleteRealmIfMigrationNeeded()
                    .build()

            Realm.setDefaultConfiguration(realmConfiguration)
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
