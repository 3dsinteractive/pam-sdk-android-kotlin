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

    override fun onMessageReceived(rm: RemoteMessage) {
        super.onMessageReceived(rm)

        var title = ""
        var message = ""
        var payload: MutableMap<String, String>

        rm.notification?.let {
            it.title?.let { t ->
                title = t
            }
            it.body?.let { m ->
                message = m
            }
        }

        rm.data.isNotEmpty().let {
            payload = rm.data
        }

        PamSDK.dispatch(
            "onMessage", mutableMapOf(
                "payload" to payload,
                "title" to title,
                "message" to message
            )
        )
    }
}