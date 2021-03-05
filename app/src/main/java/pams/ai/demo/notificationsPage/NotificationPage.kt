package pams.ai.demo.notificationsPage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pams.ai.demo.databinding.ActivityNotificationPageBinding

class NotificationPage : AppCompatActivity() {

    private var binding: ActivityNotificationPageBinding? = null
    private var adapter: NotificationListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNotificationPageBinding.inflate(layoutInflater)
        binding?.let {
            setContentView(it.root)
        }

        this.registerNotificationView()
    }

    private fun registerNotificationView() {
        adapter = NotificationListAdapter()
        binding?.listView?.adapter = adapter

        val layoutManager = LinearLayoutManager(this)
        binding?.listView?.layoutManager = layoutManager
    }
}