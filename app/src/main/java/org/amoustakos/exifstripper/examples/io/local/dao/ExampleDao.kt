package org.amoustakos.exifstripper.examples.io.local.dao

import io.reactivex.Observable
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import org.amoustakos.exifstripper.examples.io.local.models.ExampleModel
import org.amoustakos.exifstripper.examples.io.local.models.ExampleModel.Column.ID
import org.amoustakos.exifstripper.examples.io.local.models.ExampleModel.Column.NAME
import org.amoustakos.exifstripper.injection.annotations.realm.DefaultRealmConfig
import org.amoustakos.exifstripper.io.dao.base.BaseDao
import org.amoustakos.utils.android.RxUtil


class ExampleDao(realm: Realm):
		BaseDao<ExampleModel>(realm, ExampleModel::class.java) {


    fun getByID(id: String): ExampleModel? = getOneByColumn(ID, id)

    fun getByName(name: String): RealmResults<ExampleModel> =
            getByColumn(NAME, name)



    companion object {

        fun getInstance(@DefaultRealmConfig config: RealmConfiguration) =
                ExampleDao(Realm.getInstance(config))

        fun getObservableInstance(
                @DefaultRealmConfig config: RealmConfiguration): Observable<ExampleDao> =
		        Observable.just {}
                        .compose(RxUtil.applyRealmObservableScheduler())
				        .map { ExampleDao(Realm.getInstance(config)) }


    }
}
