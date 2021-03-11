package pams.ai.demo.notificationsPage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import models.Notification
import pams.ai.demo.databinding.NotificationListItemBinding

class NotificationListAdapter : RecyclerView.Adapter<NotificationViewHolder>() {
    private var notifications = listOf<Notification>()

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

    fun setNotifications(notifications: List<Notification>) {
        this.notifications = notifications
        notifyDataSetChanged()
    }
}

class NotificationViewHolder(val binding: NotificationListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun setNotification(notification: Notification) {
        binding.notification = notification
        Picasso.get().load(notification.Image).into(binding.notificationImage);
    }
}
