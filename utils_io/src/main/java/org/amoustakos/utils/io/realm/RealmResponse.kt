package org.amoustakos.utils.io.realm

import io.realm.RealmModel



data class RealmResponse<out T: RealmModel?> (val item: T?)

data class RealmListResponse<out T: RealmModel?> (val item: List<T?>)