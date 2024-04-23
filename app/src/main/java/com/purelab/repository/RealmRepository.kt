package com.purelab.repository

import com.purelab.models.Favorite
import com.purelab.models.Register
import com.purelab.models.User
import io.realm.Realm

interface RealmRepositoryImp {
    fun getUser(): User?
    fun saveUser(user: User)
    fun deleteUser(userId: String)
    fun getFavorites(): List<Favorite>?
    fun saveFavorite(favorite: Favorite)
    fun deleteFavorite(favoriteId: String)
    fun getRegisters(): List<Register>?
    fun saveRegister(register: Register)
    fun deleteRegister(registerId: String)
}

class RealmRepository : RealmRepositoryImp {

    private val realm: Realm = Realm.getDefaultInstance()

    override fun getUser(): User? {
        return realm.where(User::class.java)
            .equalTo("userId", "user")
            .findFirst()
    }

    override fun saveUser(user: User) {
        realm.executeTransactionAsync { bgRealm ->
            bgRealm.where(User::class.java).findAll().deleteAllFromRealm()
            bgRealm.copyToRealmOrUpdate(user)
        }
    }

    override fun deleteUser(userId: String) {
        realm.executeTransactionAsync { bgRealm ->
            bgRealm.where(User::class.java).equalTo("userId", userId).findFirst()?.deleteFromRealm()
        }
    }

    override fun getFavorites(): List<Favorite>? {
        return realm.where(Favorite::class.java)
            .findAll()
    }

    override fun saveFavorite(favorite: Favorite) {
        realm.executeTransactionAsync { bgRealm ->
            bgRealm.copyToRealmOrUpdate(favorite)
        }
    }

    override fun deleteFavorite(favoriteId: String) {
        realm.executeTransactionAsync { bgRealm ->
            bgRealm.where(Favorite::class.java).equalTo("favoriteId", favoriteId).findFirst()
                ?.deleteFromRealm()
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

    override fun deleteRegister(registerId: String) {
        realm.executeTransactionAsync { bgRealm ->
            bgRealm.where(Register::class.java).equalTo("registerId", registerId).findFirst()
                ?.deleteFromRealm()
        }
    }

    fun close() {
        realm.close()
    }
}