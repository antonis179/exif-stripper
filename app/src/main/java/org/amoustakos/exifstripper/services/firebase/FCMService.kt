package org.amoustakos.exifstripper.services.firebase

import com.google.firebase.messaging.FirebaseMessagingService

class FCMService : FirebaseMessagingService() {


	/**
	 * Called if InstanceID token is updated. This may occur if the security of
	 * the previous token had been compromised. Note that this is called when the InstanceID token
	 * is initially generated so this is where you would retrieve the token.
	 */
	override fun onNewToken(token: String?) {
		super.onNewToken(token)

		/* If you want to send messages to this application instance or
		*  manage this apps subscriptions on the server side, send the
		*  Instance ID token to your app server.
		*/
	}


}
