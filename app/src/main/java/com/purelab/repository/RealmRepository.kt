package com.purelab.repository

import com.purelab.models.Favorite
import com.purelab.models.Register
import com.purelab.models.User
import io.realm.Realm

interface RealmRepositoryImp {
    fun getUser(): User?
    fun saveUser(user: User)
    fun getFavorites(): List<Favorite>?
    fun saveFavorite(favorite: Favorite)
    fun getRegisters(): List<Register>?
    fun saveRegister(register: Register)
}

class RealmRepository : RealmRepositoryImp {

    private val realm: Realm = Realm.getDefaultInstance()

    override fun getUser(): User? {
        return realm.where(User::class.java)
            .equalTo("userId", "userSetting")
            .findFirst()
    }

    override fun saveUser(user: User) {
        realm.executeTransactionAsync { bgRealm ->
            bgRealm.where(User::class.java).findAll().deleteAllFromRealm()
            bgRealm.copyToRealmOrUpdate(user)
        }
    }

    override fun getFavorites(): List<Favorite>? {
        return realm.where(Favorite::class.java)
            .equalTo("favoriteId", "favorite")
            .findAll()
    }

    override fun saveFavorite(favorite: Favorite) {
        realm.executeTransactionAsync { bgRealm ->
            bgRealm.where(Favorite::class.java).findAll().deleteAllFromRealm()
            bgRealm.copyToRealmOrUpdate(favorite)
        }
    }

    override fun getRegisters(): List<Register>? {
        return realm.where(Register::class.java)
            .equalTo("registerId", "register")
            .findAll()
    }

    override fun saveRegister(register: Register) {
        realm.executeTransactionAsync { bgRealm ->
            bgRealm.where(Register::class.java).findAll().deleteAllFromRealm()
            bgRealm.copyToRealmOrUpdate(register)
        }
    }

    fun close() {
        realm.close()
    }
}