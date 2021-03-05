package pamsdk

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import webservices.MockAPI


open class PAMFirebaseMessagingService : FirebaseMessagingService() {
//    private fun createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            // Create the NotificationChannel
//            val name = getString(R.string.noti_default_channel_id)
//            val descriptionText = getString(R.string.noti_default_channel_id)
//            val importance = NotificationManager.IMPORTANCE_HIGH
//            val mChannel = NotificationChannel("default", name, importance)
//            mChannel.description = descriptionText
//            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(mChannel)
//        }
//    }

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

            PamSDK.dispatch(
                "onMessage", mutableMapOf(
                    "image_url" to imageURL,
                    "payload" to payload,
                    "title" to title,
                    "message" to message
                )
            )

//            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//            val bundle = Bundle()
//            bundle.putString("product_id", payload["product_id"])
//
//            val notification: Notification = NotificationCompat.Builder(this)
//                .setContentTitle(title)
//                .setContentText(message)
//                .setSmallIcon(R.mipmap.ic_logo)
//                .setChannelId("default")
//                .build()
//            val manager = NotificationManagerCompat.from(applicationContext)
//            manager.notify(123, notification)
        }
    }
}