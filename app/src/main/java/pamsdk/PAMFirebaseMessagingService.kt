package pamsdk

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import webservices.MockAPI


open class PAMFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        PamSDK.savePushKey(token)
        PamSDK.dispatch(
            PamCallback.onToken, mapOf(
                "token" to token
            )
        )
    }

    override fun onMessageReceived(rm: RemoteMessage) {
        super.onMessageReceived(rm)

        rm.notification?.let {
            var imageURL = ""
            var title = ""
            var message = ""
            var payload: MutableMap<String, String>

            it.imageUrl?.let { im ->
                imageURL = im.toString()
            }
            it.title?.let { t ->
                title = t
            }
            it.body?.let { m ->
                message = m
            }

            rm.data.isNotEmpty().let {
                payload = rm.data
            }

            // Just for mock only
            MockAPI.getInstance().addToNotification(
                image = imageURL,
                title = title,
                message = message,
                date = "2020-03-05 12:49:45"
            )

            PamSDK.receiveMessage(
                imageURL = imageURL,
                title = title,
                message = message,
                payload = payload
            )
        }
    }
}