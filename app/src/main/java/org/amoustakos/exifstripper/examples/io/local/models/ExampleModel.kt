package org.amoustakos.exifstripper.examples.io.local.models

import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class ExampleModel : RealmModel {

    @PrimaryKey var id: String? = null
    var name: String? = null


    companion object Column {
        const val ID = "id"
        const val NAME = "name"
    }


}