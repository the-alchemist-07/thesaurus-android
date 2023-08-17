package com.mashood.thesaurus.app.firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Override base class methods to handle any events required by the application.
 * All methods are invoked on a background thread, and may be called when the
 * app is in the background or not open.
 *
 *  The registration token may change when:
 *  - The app deletes Instance ID
 *  - The app is restored on a new device
 *  - The user uninstalls/reinstall the app
 *  - The user clears app data.
 */
class FirebaseNotificationService : FirebaseMessagingService() {

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCMService ", "Refreshed token :: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("FCMService ", "Message :: $message")
    }
}