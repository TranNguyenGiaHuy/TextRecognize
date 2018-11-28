package com.android.huytran.textrecognize.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class FileImage : RealmObject() {

    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var createdTimestamp: Long = 0
    var path: String = ""
    var imageContentList : RealmList<ImageContent> = RealmList()
}