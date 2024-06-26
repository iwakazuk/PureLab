package com.purelab.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Favorite(
    @PrimaryKey
    var favoriteId: String = "",
    var itemId: String = ""
) : RealmObject()
