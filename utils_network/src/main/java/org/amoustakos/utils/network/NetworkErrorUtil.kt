package org.amoustakos.utils.network


object NetworkErrorUtil {


	/**
	 * Checks if the [Throwable] is a connection error.
	 */
	@JvmStatic
	fun isConnectionError(t: Throwable): Boolean {
		return t is java.net.ConnectException
				|| t is java.net.NoRouteToHostException
				|| t is java.net.PortUnreachableException
				|| t is java.net.ProtocolException
				|| t is java.net.SocketException
				|| t is java.net.SocketTimeoutException
				|| t is java.net.UnknownHostException
				|| t is java.net.UnknownServiceException
	}


}
