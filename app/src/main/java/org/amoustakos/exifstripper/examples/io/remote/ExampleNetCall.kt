package org.amoustakos.exifstripper.examples.io.remote

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.amoustakos.exifstripper.net.BaseCall
import org.amoustakos.models.network.NetEvent
import org.amoustakos.utils.network.Headers.HEADER_CONTENT_TYPE
import org.amoustakos.utils.network.Headers.MIME_JSON
import org.amoustakos.utils.network.HttpStatusCode
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.Headers
import retrofit2.http.POST
import javax.inject.Inject


class ExampleNetCall @Inject constructor(
		private val service: ApiService
) : BaseCall() {


	fun call(): Observable<NetEvent<JSONObject>> {
        return service.makeCall()
                .observeOn(Schedulers.io())
                .compose(applyTransformations(RETRIES))
                .compose(applyErrorLogging())
                .map { resp ->
                    var hasError = false

                    //Check code
                    if (!HttpStatusCode.isSuccess(resp.code()))
                        hasError = true

                    //Check contents
                    if (resp.body() == null)
                        hasError = true

                    NetEvent(
                            hasError = hasError,
                            item = resp.body())
                }
                .compose(applyMapErrorResponse())
    }


    interface ApiService {
        @POST("/endpoint")
        @Headers("$HEADER_CONTENT_TYPE $MIME_JSON")
        fun makeCall(): Observable<Response<JSONObject>>
    }



}
