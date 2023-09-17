package com.purelab.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class User(
    @PrimaryKey
    var userId: String = "",
    var userName: String? = null,
    var age: String? = null,
    var sex: String? = null,
    var skinType: String? = null,
    var image: ByteArray? = null
) : RealmObject()
