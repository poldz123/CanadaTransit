package com.rodolfonavalon.canadatransit.model.database

import io.realm.Realm
import io.realm.RealmObject

class BaseRealmObject: RealmObject() {

    fun save(success: (() -> Unit)? = null, failure: ((Throwable) -> Unit)? = null) {
        Realm.getDefaultInstance().use { realm ->
            // Lets do an asynchronous realm action
            realm.executeTransactionAsync({ realmAsync ->
                // Save or Update the RealmObject
                realmAsync.copyToRealmOrUpdate(this)
            }, success, failure)
        }
    }

    fun delete(success: (() -> Unit)? = null, failure: ((Throwable) -> Unit)? = null) {
        Realm.getDefaultInstance().use { realm ->
            // Lets do an asynchronous realm action
            realm.executeTransactionAsync({ _ ->
                // Delete the object from Realm
                this.deleteFromRealm()
            }, success, failure)
        }
    }
}
