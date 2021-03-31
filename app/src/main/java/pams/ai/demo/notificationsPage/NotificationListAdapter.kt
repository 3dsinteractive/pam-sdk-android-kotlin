package pams.ai.demo.notificationsPage

import ai.pams.android.kotlin.models.notification.NotificationItem
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import pams.ai.demo.ProductDetailPage
import pams.ai.demo.databinding.NotificationListItemBinding
import services.NotiReaderActivity

class NotificationListAdapter : RecyclerView.Adapter<NotificationViewHolder>() {
    private var notifications = listOf<NotificationItem>()

    override fun getItemCount(): Int {
        return notifications.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = NotificationListItemBinding.inflate(inflater)

        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        holder.setNotification(notification)
    }

    fun setNotifications(notifications: List<NotificationItem>) {
        this.notifications = notifications
        notifyDataSetChanged()
    }
}

class NotificationViewHolder(val binding: NotificationListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private fun openNoti(notification: NotificationItem){
        if (notification.url?.startsWith("digits3://product") == true) {
            val uri = Uri.parse(notification.url)
            val productID = uri.getQueryParameter("id") ?: ""
            val intent = ProductDetailPage.createIntentWithProductID(binding.root.context, productID)
            binding.root.context.startActivity(intent)
        } else {
            val intent =Intent(binding.root.context, NotiReaderActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            binding.root.context.startActivity(intent)
        }
    }

    fun setNotification(notification: NotificationItem) {
        binding.title = notification.title
        binding.description = notification.description
        binding.date = notification.createdDate
        binding.isRead = notification.isOpen ?: false

        binding.root.setOnClickListener{
            notification.trackOpen()
            openNoti(notification)
        }

        Glide.with(this.itemView.context).load(notification.thumbnailUrl).into(binding.notificationImage);
    }
}
