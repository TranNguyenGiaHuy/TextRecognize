package com.android.huytran.textrecognize.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.util.*

@RealmClass
open class ImageContent : RealmObject() {
    @PrimaryKey
    var id : String = UUID.randomUUID().toString()
    var text : String = ""
    var fileImage: FileImage? = null
}