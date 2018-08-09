package org.amoustakos.utils.io.realm

import io.reactivex.Flowable
import io.reactivex.Observable
import io.realm.*
import io.realm.kotlin.deleteFromRealm

/**
 * Utility class to help with Realm IO.
 *
 * Most of these methods can be implemented with [RealmObject] instead of [RealmModel]
 * but this would require you to extend this at model level instead of implement.
 */
@Suppress("UNCHECKED_CAST")
object RealmUtil : RealmTraits() {

    // =========================================================================================
    // Helpers
    // =========================================================================================

    @JvmStatic fun has(realm: Realm, model: Class<out RealmModel>): Boolean =
            realm.where(model).count() > 0

    @JvmStatic fun getCount(realm: Realm, model: Class<out RealmModel>): Long =
            realm.where(model).count()

    fun unsubscribe(realm: Realm) = realm.removeAllChangeListeners()

    fun unsubscribe(realm: Realm, listener: RealmChangeListener<Realm>) =
            realm.removeChangeListener(listener)

    fun refresh(realm: Realm) = realm.refresh()

    fun subscribeToChanges(realm: Realm, listener: RealmChangeListener<Realm>) =
            realm.addChangeListener(listener)


    // =========================================================================================
    // CRUD - Synced
    // =========================================================================================

    @JvmStatic fun <T : RealmModel> copyToOrUpdate(realm: Realm, model: T): T? =
            realm transWithResp {
                realm.copyToRealmOrUpdate(model)
            }

    @JvmStatic fun <T : RealmModel> copyToOrUpdate(realm: Realm, models: Iterable<T>): List<RealmModel?> =
            realm transWithResp {
                realm.copyToRealmOrUpdate(models)
            }

    @JvmStatic fun <T : RealmModel> copyAllFrom(
            realm: Realm, models: Iterable<T>, depth: Int): List<T?> = realm transWithResp {
        realm.copyFromRealm(models, depth)
    }

    @JvmStatic fun <T : RealmModel> copyFrom(
            realm: Realm, model: T, depth: Int): T? = realm transWithResp {
        realm.copyFromRealm(model, depth)
    }

    @JvmStatic fun <T : RealmModel> add(realm: Realm, model: T, copy: Boolean): T {
        return realm transWithResp {
            var obj: T = model
            if (copy)
                obj = realm.copyToRealm(model)
            else
                realm.insert(model)
            obj
        }
    }


    @JvmStatic fun addOrUpdate(realm: Realm, model: RealmModel) = realm trans {
        realm.insertOrUpdate(model)
    }

    @JvmStatic fun addOrUpdate(realm: Realm, models: Collection<RealmModel>) = realm trans {
        realm.insertOrUpdate(models)
    }

    @JvmStatic fun addOrUpdateBatch(realm: Realm, models: Collection<RealmModel>) {
        if (models.size <= 100) {
            addOrUpdate(realm, models)
            return
        }

        val size = models.size
        for (i in 0..size step 100) {
            val newSize = models.size - i
            val get = if (newSize <= 100) newSize else 100
            addOrUpdate(realm, models.drop(i).take(get))
        }
    }

    @JvmStatic fun remove(realm: Realm, model: RealmModel) = realm trans {
        deleteNoTrans(model)
    }

    @JvmStatic fun remove(realm: Realm, models: List<RealmModel>) = realm trans {
        models.forEach { deleteNoTrans(it) }
    }

    @JvmStatic fun deleteNoTrans(model: RealmModel) = model.deleteFromRealm()

    @JvmStatic fun clearAll(realm: Realm, model: Class<out RealmModel>) = realm trans {
        realm.delete(model)
    }


    @JvmStatic fun <T : RealmModel> getByModel(realm: Realm, model: Class<T>): RealmResults<T> =
            realm.where(model).findAll()

    @JvmStatic fun <T : RealmModel> getOne(realm: Realm, model: Class<T>): T? =
            realm.where(model).findFirst()

    @JvmStatic fun <T : RealmModel> getOneByColumn(
            realm: Realm, model: Class<T>, column: String, value: String): T? =
            realm.where(model).equalTo(column, value).findFirst()

    @JvmStatic fun <T : RealmModel> getOneByColumn(
            realm: Realm, model: Class<T>, column: String, value: Int): T? =
            realm.where(model).equalTo(column, value).findFirst()

    @JvmStatic fun <T : RealmModel> getOneByColumn(
            realm: Realm, model: Class<T>, column: String, value: Double): T? =
            realm.where(model).equalTo(column, value).findFirst()

    @JvmStatic fun <T : RealmModel> getOneByColumn(
            realm: Realm, model: Class<T>, column: String, value: Float): T? =
            realm.where(model).equalTo(column, value).findFirst()

    @JvmStatic fun <T : RealmModel> getOneByColumn(
            realm: Realm, model: Class<T>, column: String, value: Boolean): T? =
            realm.where(model).equalTo(column, value).findFirst()


    @JvmStatic fun <T : RealmModel> getByColumn(
            realm: Realm, model: Class<T>, column: String, value: String): RealmResults<T> =
            realm.where(model).equalTo(column, value).findAll()

    @JvmStatic fun <T : RealmModel> getByColumn(
            realm: Realm, model: Class<T>, column: String, value: Int): RealmResults<T> =
            realm.where(model).equalTo(column, value).findAll()

    @JvmStatic fun <T : RealmModel> getByColumn(
            realm: Realm, model: Class<T>, column: String, value: Double): RealmResults<T> =
            realm.where(model).equalTo(column, value).findAll()

    @JvmStatic fun <T : RealmModel> getByColumn(
            realm: Realm, model: Class<T>, column: String, value: Float): RealmResults<T> =
            realm.where(model).equalTo(column, value).findAll()

    @JvmStatic fun <T : RealmModel> getByColumn(
            realm: Realm, model: Class<T>, column: String, value: Boolean): RealmResults<T> =
            realm.where(model).equalTo(column, value).findAll()


    // =========================================================================================
    // CRUD - Async
    // =========================================================================================

    @JvmStatic fun <T : RealmModel> copyToOrUpdateAsync(
            realm: Realm, model: T): Observable<RealmResponse<T?>> =
            Observable.fromCallable {
                RealmResponse(
                    RealmUtil.copyToOrUpdate(realm, model)
                )
            }

    @JvmStatic fun <T : RealmModel> copyToOrUpdateAsync(realm: Realm, models: Iterable<T>) =
            Observable.fromCallable {
                    RealmUtil.copyToOrUpdate(realm, models)
            }!!

    @JvmStatic fun <T : RealmModel> copyFromAsync(
            realm: Realm, model: T, depth: Int): Observable<RealmResponse<T?>> =
            Observable.fromCallable {
                RealmResponse(
                    copyFrom(realm, model, depth)
                )
            }

    @JvmStatic fun <T : RealmModel> copyAllFromAsync(realm: Realm, models: Iterable<T>, depth: Int) =
            Observable.fromCallable {
                RealmListResponse(
                    copyAllFrom(realm, models, depth)
                )
            }


    @JvmStatic fun <T : RealmModel> addAsync(
            realm: Realm, model: T, copy: Boolean): Observable<RealmResponse<T>> =
            Observable.fromCallable {
                var obj: T = model
                if (copy)
                    obj = realm.copyToRealm(model)
                else
                    realm.insert(model)
                RealmResponse (obj)
            }

    @JvmStatic fun addOrUpdateAsync(realm: Realm, model: RealmModel): Observable<Unit> =
            Observable.fromCallable {
                addOrUpdate(realm, model)
            }

    @JvmStatic fun addOrUpdateBatchAsync(realm: Realm, models: Collection<RealmModel>): Observable<Unit> =
            Observable.fromCallable {
                addOrUpdateBatch(realm, models)
            }

    @JvmStatic fun removeAsync(realm: Realm, model: RealmModel): Observable<Boolean> =
            Observable.fromCallable {
                realm trans { deleteNoTrans(model) }
                return@fromCallable true
            }


    @JvmStatic fun removeAsync(realm: Realm, models: List<RealmModel>): Observable<Boolean> =
            Observable.fromCallable {
                realm trans {
                    models.forEach { deleteNoTrans(it) }
                }
                return@fromCallable true
            }


    @JvmStatic fun clearAllAsync(realm: Realm, model: Class<out RealmModel>): Observable<Unit> =
            Observable.fromCallable {
                clearAll(realm, model)
            }


    @JvmStatic fun <T : RealmModel> getByModelAsync(
            realm: Realm, model: Class<T>): Flowable<RealmResults<T>> =
            realm.where(model).findAllAsync().asFlowable()


    @JvmStatic fun <T : RealmModel> getOneAsync(realm: Realm, model: Class<T>): Observable<RealmResponse<T?>> =
            Observable.fromCallable{ RealmResponse(getOne(realm, model)) }

    @JvmStatic fun <T : RealmModel> getOneByColumnAsync(
            realm: Realm, model: Class<T>, column: String, value: String): Observable<RealmResponse<T?>> =
            Observable.fromCallable{ RealmResponse(getOneByColumn(realm, model, column, value)) }

    @JvmStatic fun <T : RealmModel> getOneByColumnAsync(
            realm: Realm, model: Class<T>, column: String, value: Int): Observable<RealmResponse<T?>> =
            Observable.fromCallable{ RealmResponse(getOneByColumn(realm, model, column, value)) }

    @JvmStatic fun <T : RealmModel> getOneByColumnAsync(
            realm: Realm, model: Class<T>, column: String, value: Double): Observable<RealmResponse<T?>> =
            Observable.fromCallable{ RealmResponse(getOneByColumn(realm, model, column, value)) }

    @JvmStatic fun <T : RealmModel> getOneByColumnAsync(
            realm: Realm, model: Class<T>, column: String, value: Float): Observable<RealmResponse<T?>> =
            Observable.fromCallable{ RealmResponse(getOneByColumn(realm, model, column, value)) }

    @JvmStatic fun <T : RealmModel> getOneByColumnAsync(
            realm: Realm, model: Class<T>, column: String, value: Boolean): Observable<RealmResponse<T?>> =
            Observable.fromCallable{ RealmResponse(getOneByColumn(realm, model, column, value)) }


    @JvmStatic fun <T : RealmModel> getByColumnAsync(
            realm: Realm, model: Class<T>, column: String, value: String): Flowable<RealmResults<T>> =
            realm.where(model).equalTo(column, value).findAllAsync().asFlowable()

    @JvmStatic fun <T : RealmModel> getByColumnAsync(
            realm: Realm, model: Class<T>, column: String, value: Int): Flowable<RealmResults<T>> =
            realm.where(model).equalTo(column, value).findAllAsync().asFlowable()

    @JvmStatic fun <T : RealmModel> getByColumnAsync(
            realm: Realm, model: Class<T>, column: String, value: Double): Flowable<RealmResults<T>> =
            realm.where(model).equalTo(column, value).findAllAsync().asFlowable()

    @JvmStatic fun <T : RealmModel> getByColumnAsync(
            realm: Realm, model: Class<T>, column: String, value: Float): Flowable<RealmResults<T>> =
            realm.where(model).equalTo(column, value).findAllAsync().asFlowable()

    @JvmStatic fun <T : RealmModel> getByColumnAsync(
            realm: Realm, model: Class<T>, column: String, value: Boolean): Flowable<RealmResults<T>> =
            realm.where(model).equalTo(column, value).findAllAsync().asFlowable()

}