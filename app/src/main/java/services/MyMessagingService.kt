package services

import ai.pams.android.kotlin.PamMessagePayload
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.RemoteMessage
import pams.ai.demo.ProductDetailPage
import pams.ai.demo.R
import ai.pams.android.kotlin.services.PAMMessagingService

class MyMessagingService : PAMMessagingService() {

    override fun onCreate() {
        super.onCreate()
        notiColor = ContextCompat.getColor(this.applicationContext, R.color.white)
        notiSmallIcon = R.mipmap.ic_launcher
    }

    override fun onCreateCustomNotificationView(remoteMessage: RemoteMessage): RemoteViews? {
        val remoteView = RemoteViews(packageName, R.layout.custom_notification_banner)
        remoteView.setTextViewText(R.id.titleText, remoteMessage.notification?.title)
        remoteView.setTextViewText(R.id.bodyText, remoteMessage.notification?.body)

        val bitmap = loadImageSync(remoteMessage.notification?.imageUrl)
        if (bitmap != null) {
            remoteView.setImageViewBitmap(R.id.notiImage, bitmap)
        } else {
            remoteView.setViewVisibility(R.id.notiImage, View.GONE)
        }

        return remoteView
    }

    override fun onCreateReaderIntent(payload: PamMessagePayload): Intent {
        return if (payload.url?.startsWith("digits3://product") == true) {
            val uri = Uri.parse(payload.url)
            val productID = uri.getQueryParameter("id") ?: ""
            ProductDetailPage.createIntentWithProductID(this, productID)
        } else {
            Intent(this, NotiReaderActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }
    }

    override fun onMessageReceivedButNotFromPAM(remoteMessage: RemoteMessage) {
        //Handle your notification here
        notifyAsPam(remoteMessage)
    }

}

class NotiReaderActivity : AppCompatActivity() {

}
