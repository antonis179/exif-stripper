package org.amoustakos.utils.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

object NetworkUtil {

    // =========================================================================================
    // Constants
    // =========================================================================================

    private val PING_ADDRESS = "8.8.8.8"
    private val DEFAULT_TIMEOUT = 1000
    private val DEFAULT_RETRIES = 1


    // =========================================================================================
    // Basic Android functions
    // =========================================================================================

    /**
     * Checks if the device is connected to a network
     */
    fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }

    /**
     * Retrieves the network info
     */
    fun getNetworkInfo(context: Context): NetworkInfo? {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo
    }

    /**
     * Checks if there is any connectivity (this does not mean internet).
     */
    fun isConnected(context: Context): Boolean {
        val info = getNetworkInfo(context)
        return info != null && info.isConnected
    }


    // =========================================================================================
    // Internet connectivity checks
    // =========================================================================================

//    /**
//     * Async version of [.hasInternetSynchronous]. <br></br>
//     * Note that the [Observable] returned does not implement error handling.
//     */
//    @Throws(UnknownHostException::class)
//    fun hasInternetAsync(
//            address: String,
//            timeoutMillis: Int,
//            repeatPing: Int
//    ): Observable<Boolean> {
//
//        return Observable.just(hasInternetSynchronous(address, timeoutMillis, repeatPing))
//    }
//
//    /**
//     * Async version of [.hasInternetSynchronous]. <br></br>
//     * Note that the [Observable] returned does not implement error handling.
//     */
//    @Throws(UnknownHostException::class)
//    fun hasInternetAsync(): Observable<Boolean> {
//        return Observable.just(hasInternetSynchronous())
//    }
//
//    /**
//     * Synchronous method that checks for internet availability by executing a ping command.
//     *
//     * @param address       The address to ping.
//     * @param timeoutMillis The timeout to specify for the execution of the command.
//     * @param repeatPing    The times to retry execution.
//     * @return true if there is internet connectivity - false otherwise.
//     * @throws UnknownHostException if the address input was incorrect or if the host was not found.
//     */
//    @Throws(UnknownHostException::class)
//    @JvmOverloads
//    fun hasInternetSynchronous(
//            address: String = PING_ADDRESS,
//            timeoutMillis: Int = DEFAULT_TIMEOUT,
//            repeatPing: Int = DEFAULT_RETRIES): Boolean {
//        val pingResult = Ping.onAddress(address)
//                .setTimeOutMillis(timeoutMillis)
//                .setTimes(repeatPing)
//                .doPing()
//
//        return !pingResult.hasError()
//    }


}