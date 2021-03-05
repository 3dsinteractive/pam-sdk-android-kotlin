package pams.ai.demo.notificationsPage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pams.ai.demo.LoginPage
import pams.ai.demo.databinding.ActivityNotificationPageBinding
import pamsdk.PamSDK
import pamsdk.PamSDKName
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
        registerRefreshButton()
        registerUserButton()
        registerLogoutButton()

        fetchNotifications()
    }

    private fun registerNotificationView() {
        adapter = NotificationListAdapter()
        binding?.listView?.adapter = adapter

        val layoutManager =
            LinearLayoutManager(this@NotificationPage, LinearLayoutManager.VERTICAL, false)
        binding?.listView?.layoutManager = layoutManager
    }

    private fun registerRefreshButton() {
        binding?.btnRefresh?.let {
            it.setOnClickListener {
                fetchNotifications()
            }
        }
    }

    private fun registerUserButton() {
        binding?.let {
            it.btnUser.setOnClickListener {
                binding?.let { b ->
                    if (b.btnLogout.visibility == View.INVISIBLE) {
                        b.btnLogout.visibility = View.VISIBLE
                    } else {
                        b.btnLogout.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }

    private fun registerLogoutButton() {
        binding?.let {
            it.btnLogout.setOnClickListener {
                PamSDK.userLogout()

                val intent = Intent(this, LoginPage::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

                startActivity(intent)
                this@NotificationPage.finish()
            }
        }
    }

    private fun fetchNotifications() {
        val notifications = MockAPI.getInstance().getNotifications()
        Log.d(PamSDKName, notifications.toString())
        adapter?.setNotifications(notifications = notifications)
    }
}