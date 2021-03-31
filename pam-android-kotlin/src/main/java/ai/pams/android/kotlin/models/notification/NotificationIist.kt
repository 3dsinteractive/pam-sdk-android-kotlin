package ai.pams.android.kotlin.models.notification

import com.google.gson.annotations.SerializedName

class NotificationList(
    @SerializedName("items") val items: List<NotificationItem>? = null
)