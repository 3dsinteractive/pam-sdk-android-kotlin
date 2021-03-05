package pams.ai.demo.notificationsPage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pams.ai.demo.databinding.ActivityNotificationPageBinding
import webservices.MockAPI

class NotificationPage : AppCompatActivity() {

    private var binding: ActivityNotificationPageBinding? = null
    private var adapter: NotificationListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNotificationPageBinding.inflate(layoutInflater)
        binding?.let {
            setContentView(it.root)
        }

        registerNotificationView()
        fetchNotifications()
    }

    private fun registerNotificationView() {
        adapter = NotificationListAdapter()
        binding?.listView?.adapter = adapter

        val layoutManager = LinearLayoutManager(this@NotificationPage, LinearLayoutManager.VERTICAL, false)
        binding?.listView?.layoutManager = layoutManager
    }

    private fun fetchNotifications() {
        val notifications = MockAPI.getInstance().getNotifications()
        adapter?.setNotifications(notifications = notifications)
    }
}