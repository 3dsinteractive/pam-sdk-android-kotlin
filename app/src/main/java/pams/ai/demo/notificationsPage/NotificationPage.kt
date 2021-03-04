package pams.ai.demo.notificationsPage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import pams.ai.demo.databinding.ActivityNotificationPageBinding

class NotificationPage : AppCompatActivity() {

    var binding: ActivityNotificationPageBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNotificationPageBinding.inflate(layoutInflater)
        binding?.let {
            setContentView(it.root)
        }
    }
}