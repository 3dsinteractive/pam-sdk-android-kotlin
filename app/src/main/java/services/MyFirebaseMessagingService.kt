package services

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.RemoteMessage
import pams.ai.demo.R
import pamsdk.PAMFirebaseMessagingService
import pamsdk.Pam

class MyFirebaseMessagingService: PAMFirebaseMessagingService(){

    init {
        notificationHaldlerClass = NotiReaderActivity::class.java
        largeIcon = R.mipmap.ic_launcher
        smallIcon = R.mipmap.ic_launcher
        color = ContextCompat.getColor(this.applicationContext, R.color.white)
    }

    override fun onMessageReceivedButNotFromPAM(remoteMessage: RemoteMessage) {
        //Handle your notification here
    }

}

class NotiReaderActivity: AppCompatActivity() {

}
