package ai.pams.android.kotlin.models.notification

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class NotificationList(
    var items: List<NotificationItem>? = null
){
    companion object{

        private fun stringToDate(dateString: String): Date? {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            try {
                return format.parse(dateString)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return null
        }

        fun parse(jsonString: String?, context: Context): NotificationList{
            if(jsonString == null){
                val noti = NotificationList()
                noti.items = listOf()
                return noti
            }

            val noti = NotificationList();
            val list = mutableListOf<NotificationItem>()

            val json = JSONObject(jsonString)
            val itemsArray = json.optJSONArray("items")
            val count = itemsArray?.length() ?: 0
            for(i in 0 until count){
                itemsArray?.getJSONObject(i)?.let{  jsonItem->

                    val createdDate = jsonItem.optString("created_date")

                    var jsonPayload = jsonItem.optJSONObject("json_data");
                    jsonPayload = jsonPayload?.optJSONObject("pam")
                    val payload = mutableMapOf<String, Any>()

                    val iterator = jsonPayload?.keys()
                    while(iterator?.hasNext() == true){
                        val key = iterator.next()
                        jsonPayload?.get(key)?.let{ data ->
                            payload[key] = data
                        }
                    }

                    val item = NotificationItem(
                        date = stringToDate(createdDate),
                        deliverId = jsonItem.optString("deliver_id"),
                        description = jsonItem.optString("description"),
                        flex = jsonItem.optString("flex"),
                        isOpen = jsonItem.optBoolean("is_open"),
                        payload = payload,
                        pixel = jsonItem.optString("pixel"),
                        thumbnailUrl = jsonItem.optString("thumbnail_url"),
                        title = jsonItem.optString("title"),
                        url = jsonItem.optString("url"),
                        popupType = jsonPayload?.optString("popupType")
                    )
                    item.parseFlex(context)
                    list.add(item)
                }
            }

            noti.items = list
            return noti
        }
    }
}
