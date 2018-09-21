package org.amoustakos.exifstripper.net

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.amoustakos.models.network.NetEvent
import org.amoustakos.utils.network.Headers.MIME_JSON
import org.amoustakos.utils.network.HttpStatusCode
import org.amoustakos.utils.network.retrofit.AuthInterface
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber


abstract class BaseCall {

	companion object {
		const val RETRIES: Long = 3
	}


	/**
	 * Applies the default retries to an observable
	 */
	fun <T> applyRetries(times: Long): ObservableTransformer<T, T> = ObservableTransformer {
		it.retry(times)
	}

	/**
	 * Applies the default error logging to an observable
	 */
	fun <T> applyErrorLogging(): ObservableTransformer<T, T> = ObservableTransformer {
		it.doOnError(Timber::e)
	}


	/**
	 * Adds an authorization call to an observable
	 */
	protected fun <T> auth(authManager: AuthInterface, obs: Observable<T>): Observable<T> {
		return if (authManager.isAuthorized()) {
			obs.onErrorResumeNext { throwable: Throwable ->
				if (HttpStatusCode.isUnauthorized(throwable))
					authManager.authorizeAsync().flatMap { obs }
				else
					Observable.error<T>(throwable)
			}
		} else {
			authManager.authorizeAsync().flatMap { obs }
		}
	}


	/**
	 * Applies a return value in case a mapping operation fails
	 */
	fun <T> applyMapErrorResponse():
			ObservableTransformer<NetEvent<T>, NetEvent<T>> = ObservableTransformer {
		it.onErrorReturn { NetEvent(-1, true, null) }
	}

	/**
	 * Applies the default error return to an observable
	 */
	fun <T> applyErrorReturn(): ObservableTransformer<Response<T>, Response<T>> {
		return ObservableTransformer {
			it.onErrorReturn { t ->
				if (t is HttpException)
					t.response() as Response<T>
				else
					makeErrorJsonResponse()
			}
		}
	}

	/**
	 * Applies the following transformations to an observable:
	 *
	 * 1. Retry policy
	 * 2. Default error logging (@ error level)
	 * 3. Default error return
	 */
	fun <T> applyTransformations(retries: Long):
			ObservableTransformer<Response<T>, Response<T>> = ObservableTransformer {
		it.compose(applyRetries<Response<T>>(retries))
				.compose(applyErrorLogging<Response<T>>())
				.compose(applyErrorReturn())
	}


	/**
	 * Default error JSON response
	 */
	fun <T> makeErrorJsonResponse(): Response<T> = Response.error(500, emptyJSONResponseBody())


	/**
	 * Default error JSON body
	 */
	fun emptyJSONResponseBody(): ResponseBody =
			ResponseBody.create(MediaType.parse(MIME_JSON), "{}")


}
