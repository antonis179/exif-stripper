package org.amoustakos.exifstripper.io.realm.dao.base

import com.crashlytics.android.Crashlytics
import io.reactivex.Flowable
import io.reactivex.Observable
import io.realm.*
import org.amoustakos.utils.io.realm.RealmListResponse
import org.amoustakos.utils.io.realm.RealmResponse
import org.amoustakos.utils.io.realm.RealmTraits
import org.amoustakos.utils.io.realm.RealmUtil
import timber.log.Timber

abstract class BaseDao<T : RealmModel> (
        @get:Synchronized protected val realm: Realm,
        private val modelType: Class<T>)
    : RealmTraits() {

	private val finalizer = Observable.fromCallable { close() }
			.doOnNext { Timber.v("Successful Realm shutdown") }
            .doOnError {
                Timber.e(it)
	            Crashlytics.logException(it)
            }
			.onErrorReturn {}!!

	fun close() {
		try {
			unsubscribe()
		} catch (e: Exception) {
			Timber.d("Error removing listeners from this realm instance")
		} finally {
			realm.close()
		}
	}

	fun unsubscribe() = RealmUtil.unsubscribe(realm)

	fun unsubscribe(listener: RealmChangeListener<Realm>) = RealmUtil.unsubscribe(realm, listener)

	fun refresh() = RealmUtil.refresh(realm)

	fun subscribeToChanges(listener: RealmChangeListener<Realm>) =
			RealmUtil.subscribeToChanges(realm, listener)


    // =========================================================================================
    // Helpers
    // =========================================================================================

    fun has(): Boolean = RealmUtil.has(realm, modelType)

    fun getCount() = RealmUtil.getCount(realm, modelType)

    fun realmMonitor(): Flowable<Realm> = realm.asFlowable()

    fun isAutoUpdate() = realm.isAutoRefresh

    fun isClosed() = realm.isClosed

    fun isValid(model: T): Boolean = RealmObject.isValid(model)

    fun isEmpty(): Boolean = getCount() == 0L

    fun sort(field: String, sort: Sort): RealmResults<T> =
            realm.where(modelType).sort(field, sort).findAll()

    // =========================================================================================
    // CRUD - Synced
    // =========================================================================================

    fun update(body: () -> Unit) = realm trans { body() }

    fun copy(model: T, depth: Int) = RealmUtil.copyFrom(realm, model, depth)

    fun copyAll(depth: Int) = RealmUtil.copyAllFrom(realm, all(), depth)

    fun copyAll(models: Iterable<T>, depth: Int) =
            RealmUtil.copyAllFrom(realm, models, depth)

    fun copyToOrUpdate(model: T) =
            RealmUtil.copyToOrUpdate(realm, model)

    fun copyToOrUpdate(models: Iterable<T>) =
            RealmUtil.copyToOrUpdate(realm, models)

    fun all(): RealmResults<T> = RealmUtil.getByModel(realm, modelType)

    fun add(model: T, copy: Boolean): T? = RealmUtil.add(realm, model, copy)

    fun addOrUpdate(model: T) = RealmUtil.addOrUpdate(realm, model)

	fun addOrUpdate(models: Collection<T>) = RealmUtil.addOrUpdate(realm, models)

	fun addOrUpdateBatch(models: Collection<T>) = RealmUtil.addOrUpdateBatch(realm, models)

    fun remove(model: T) = RealmUtil.remove(realm, model)

    fun remove(models: List<T>) = RealmUtil.remove(realm, models)

    fun clearAll() = RealmUtil.clearAll(realm, modelType)


	fun getOne(): T? = RealmUtil.getOne(realm, modelType)

    fun getOneByColumn(column: String, value: String): T? =
            RealmUtil.getOneByColumn(realm, modelType, column, value)

    fun getOneByColumn(column: String, value: Int): T? =
            RealmUtil.getOneByColumn(realm, modelType, column, value)

    fun getOneByColumn(column: String, value: Double): T? =
            RealmUtil.getOneByColumn(realm, modelType, column, value)

    fun getOneByColumn(column: String, value: Float): T? =
            RealmUtil.getOneByColumn(realm, modelType, column, value)

    fun getOneByColumn(column: String, value: Boolean): T? =
            RealmUtil.getOneByColumn(realm, modelType, column, value)



    fun getByColumn(column: String, value: String): RealmResults<T> =
            RealmUtil.getByColumn(realm, modelType, column, value)

    fun getByColumn(column: String, value: Int): RealmResults<T> =
            RealmUtil.getByColumn(realm, modelType, column, value)

    fun getByColumn(column: String, value: Double): RealmResults<T> =
            RealmUtil.getByColumn(realm, modelType, column, value)

    fun getByColumn(column: String, value: Float): RealmResults<T> =
            RealmUtil.getByColumn(realm, modelType, column, value)

    fun getByColumn(column: String, value: Boolean): RealmResults<T> =
            RealmUtil.getByColumn(realm, modelType, column, value)

    // =========================================================================================
    // CRUD - Async
    // =========================================================================================

    fun copyAllAsync(depth: Int) =
            RealmUtil.copyAllFromAsync(realm, all(), depth)

    fun copyAllAsync(models: Iterable<T>, depth: Int): Observable<RealmListResponse<T>> =
            RealmUtil.copyAllFromAsync(realm, models, depth)

    fun copyToOrUpdateAsync(model: T) =
            RealmUtil.copyToOrUpdateAsync(realm, model)

    fun copyToOrUpdateAsync(models: Iterable<T>): Observable<List<RealmModel?>> =
            RealmUtil.copyToOrUpdateAsync(realm, models)

    fun addAsync(model: T, copy: Boolean) =
            RealmUtil.addAsync(realm, model, copy)

    fun addOrUpdateAsync(model: T) =
            RealmUtil.addOrUpdateAsync(realm, model)

    fun removeAsync(model: T) =
            RealmUtil.removeAsync(realm, model)

    fun removeAsync(models: List<T>) =
            RealmUtil.removeAsync(realm, models)

    fun clearAllAsync() =
            RealmUtil.clearAllAsync(realm, modelType)




    fun makeObservable(): Observable<BaseDao<T>> =
            Observable.just(this)

    fun allAsync() =
            RealmUtil.getByModelAsync(realm, modelType)

    fun updateAsync(body: () -> Unit): Observable<Unit> =
            Observable.fromCallable({realm trans { body() }})

    fun copyAsync(model: T, depth: Int) =
            RealmUtil.copyFromAsync(realm, model, depth)


	fun getOneAsync(): Observable<RealmResponse<T?>> = RealmUtil.getOneAsync(realm, modelType)

    fun getOneByColumnAsync(column: String, value: String): Observable<RealmResponse<T?>> =
            RealmUtil.getOneByColumnAsync(realm, modelType, column, value)

    fun getOneByColumnAsync(column: String, value: Int) =
            RealmUtil.getOneByColumnAsync(realm, modelType, column, value)

    fun getOneByColumnAsync(column: String, value: Double) =
            RealmUtil.getOneByColumnAsync(realm, modelType, column, value)

    fun getOneByColumnAsync(column: String, value: Float) =
            RealmUtil.getOneByColumnAsync(realm, modelType, column, value)

    fun getOneByColumnAsync(column: String, value: Boolean) =
            RealmUtil.getOneByColumnAsync(realm, modelType, column, value)



    fun getByColumnAsync(column: String, value: String) =
            RealmUtil.getByColumnAsync(realm, modelType, column, value)

    fun getByColumnAsync(column: String, value: Int) =
            RealmUtil.getByColumnAsync(realm, modelType, column, value)

    fun getByColumnAsync(column: String, value: Double) =
            RealmUtil.getByColumnAsync(realm, modelType, column, value)

    fun getByColumnAsync(column: String, value: Float) =
            RealmUtil.getByColumnAsync(realm, modelType, column, value)

    fun getByColumnAsync(column: String, value: Boolean) =
            RealmUtil.getByColumnAsync(realm, modelType, column, value)

    // =========================================================================================
    // Overrides
    // =========================================================================================

    /** This method overrides "finalize" even though it can't be declared in kotlin yet. */
    protected fun finalize() = finalizer.subscribe()!!

}