package com.purelab.models

import io.realm.RealmObject

open class User(
    var userName: String? = null,
    var age: String? = null,
    var sex: String? = null,
    var skinType: String? = null,
    var image: ByteArray? = null
) : RealmObject()
