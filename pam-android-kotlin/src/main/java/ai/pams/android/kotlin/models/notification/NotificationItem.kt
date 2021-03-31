package ai.pams.android.kotlin.models.notification


import ai.pams.android.kotlin.http.Http
import com.google.gson.annotations.SerializedName

data class NotificationItem(
    @SerializedName("created_date")
    val createdDate: String? = null,
    @SerializedName("deliver_id")
    val deliverId: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("flex")
    val flex: String? = null,
    @SerializedName("is_open")
    val isOpen: Boolean? = null,
    @SerializedName("json_data")
    val payload: Payload? = null,
    @SerializedName("pixel")
    val pixel: String? = null,
    @SerializedName("thumbnail_url")
    val thumbnailUrl: String? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("url")
    val url: String? = null
){
    fun trackOpen(){
        pixel?.let{
            Http.getInstance().get(it)
        }
    }
}