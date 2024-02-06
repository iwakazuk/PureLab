package com.purelab.app

import android.app.Application
import io.realm.DynamicRealm
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmMigration

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)

        val myMigration = RealmMigration { realm, oldVersion, newVersion ->
            val schema = realm.schema

            // 以下のコードは、マイグレーションが必要な場合のみ実行されます。
            if (oldVersion == 1L) {
                val userSchema = schema.get("User")
                userSchema?.addField("userId", String::class.java)
                userSchema?.addPrimaryKey("userId")
            }
        }

        val config = RealmConfiguration.Builder()
            .schemaVersion(3)
            .migration(myMigration)
            .build()

        Realm.setDefaultConfiguration(config)
    }
}