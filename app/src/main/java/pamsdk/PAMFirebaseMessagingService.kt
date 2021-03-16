package pamsdk

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.media.RingtoneManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


open class PAMFirebaseMessagingService : FirebaseMessagingService() {

    var notificationHaldlerClass: Class<out AppCompatActivity>? = null

    var smallIcon: Int? = null
    var largeIcon: Int? = null
    var color: Int = Color.BLACK

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Pam.shared.setDeviceToken(token)
    }

    open fun onMessageReceivedButNotFromPAM(remoteMessage: RemoteMessage) {

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data["pam"] == null) {
            onMessageReceivedButNotFromPAM(remoteMessage)
            return
        }

        if(notificationHaldlerClass == null){
            super.onMessageReceived(remoteMessage)
            return
        }

        val intent = Intent(this.applicationContext, notificationHaldlerClass)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this.applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        );

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        val notificationBuilder = NotificationCompat.Builder(this.applicationContext, "pam")
            .setContentTitle(remoteMessage.data["title"])
            .setContentText(remoteMessage.data["content"])
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setDefaults(Notification.DEFAULT_VIBRATE)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)

        smallIcon?.let{
            notificationBuilder.setSmallIcon(it)
        }

        largeIcon?.let{
            ContextCompat.getDrawable(this.applicationContext, it)?.also{ drawable ->
                val currentState = drawable.current
                if (currentState is BitmapDrawable) {
                    val bitmap = currentState.bitmap
                    notificationBuilder.setLargeIcon(bitmap)
                }
            }
        }

        notificationBuilder.color = color

        val notificationManager =
            this.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.notify(0, notificationBuilder.build())
    }
}
