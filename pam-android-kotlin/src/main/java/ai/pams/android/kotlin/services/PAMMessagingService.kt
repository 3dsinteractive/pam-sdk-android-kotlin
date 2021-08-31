package ai.pams.android.kotlin.services


import ai.pams.android.kotlin.Pam
import ai.pams.android.kotlin.PamMessagePayload
import android.R
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException


open class PAMMessagingService : FirebaseMessagingService() {

    var notificationManager: NotificationManagerCompat? = null
    var notificationChannel: NotificationChannel? = null


    private val channelId = "i.apps.notifications"
    private val description = "Test notification"

    var notiSmallIcon = R.drawable.ic_dialog_info
    var notiLargeIcon: Bitmap? = null
    var notiColor: Int? = null
    var notificationActivityClass: Class<Activity>? = null

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Pam.shared.setDeviceToken(token)
    }

    open fun onMessageReceivedButNotFromPAM(remoteMessage: RemoteMessage) {

    }

     fun loadImageSync(url: Uri?): Bitmap?{
         if(url == null) {return null}
         val myBitmap = Glide.with(applicationContext).asBitmap()
             .load(url.toString())
             .submit()
         return myBitmap.get()
     }

    private fun setupChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val adminChannel = NotificationChannel(
                "pam",
                "pam",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            adminChannel.description = "Marketing push notification"
            adminChannel.enableLights(true)
            adminChannel.lightColor = Color.MAGENTA
            adminChannel.enableVibration(true)
            notificationManager.createNotificationChannel(adminChannel)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val pamData = remoteMessage.data["pam"]
        if(pamData != null){
            notifyAsPam(remoteMessage)
        }
    }

    open fun onCreateReaderIntent(payload: PamMessagePayload): Intent {
        return Intent()
    }

    open fun onCreateCustomNotificationView(remoteMessage: RemoteMessage): RemoteViews? {
        return null
    }

    private fun pamRemoteMessageToMap(remoteMessage: RemoteMessage): PamMessagePayload?{
        val payloadJson = remoteMessage.data["pam"] ?: ""
        try{
            val model =  Gson().fromJson(payloadJson, PamMessagePayload::class.java)
            return model
        }catch (e: JsonSyntaxException){
            e.printStackTrace()
        }

        return null
    }

    @Suppress("DEPRECATION")
    fun notifyAsPam(remoteMessage: RemoteMessage) {
        if (notificationManager == null) {
            notificationManager = NotificationManagerCompat.from(this.applicationContext)
        }

        var intent: Intent? = null
        pamRemoteMessageToMap(remoteMessage)?.let{
            intent = onCreateReaderIntent(it)
        }

        val pendingIntent = PendingIntent.getActivity(this.applicationContext, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val contentView = onCreateCustomNotificationView(remoteMessage)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel =
                NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel?.enableLights(true)
            notificationChannel?.enableVibration(false)
            notificationChannel?.let {
                notificationManager?.createNotificationChannel(it)
            }

            val builder = NotificationCompat.Builder(this, channelId)
            builder.setContent(contentView)
            notiLargeIcon?.let{
                builder.setLargeIcon(it)
            }
            builder.setContentIntent(pendingIntent)
            notiColor?.let{
                builder.color = it
            }
            builder.setSmallIcon(notiSmallIcon)
            val notiCode = remoteMessage.notification?.body.hashCode()
            notificationManager?.notify(notiCode, builder.build())

        } else {
            val builder = NotificationCompat.Builder(this)
                .setContent(contentView)
                .setContentIntent(pendingIntent)

            notiLargeIcon?.let{
                builder.setLargeIcon(it)
            }
            notiColor?.let{
                builder.color = it
            }
            builder.setSmallIcon(notiSmallIcon)

            val notiCode = remoteMessage.notification?.body.hashCode()
            notificationManager?.notify(notiCode, builder.build())
        }

    }
}
