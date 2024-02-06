package com.purelab.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Register(
    @PrimaryKey
    var registerId: String = "",
    var itemId: String = ""
) : RealmObject()