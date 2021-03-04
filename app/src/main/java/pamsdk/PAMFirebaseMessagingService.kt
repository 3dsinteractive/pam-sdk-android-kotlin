package pamsdk

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

open class PAMFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        PamSDK.savePushKey(token)
        PamSDK.dispatch(
            "onToken", mutableMapOf(
                "token" to token
            )
        )
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        PamSDK.dispatch(
            "onMessage", mutableMapOf(
                "message" to message.toString()
            )
        )
    }
}