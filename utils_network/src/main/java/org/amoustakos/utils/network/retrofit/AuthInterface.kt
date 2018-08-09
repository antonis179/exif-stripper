package org.amoustakos.utils.network.retrofit

import io.reactivex.Observable

/**
 * Created by Antonis Moustakos on 1/28/2018.
 */

interface AuthInterface {

    fun isAuthorized(): Boolean

    fun authorizeAsync(): Observable<Boolean>

}
