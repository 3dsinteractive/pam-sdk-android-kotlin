package ai.pams.android.kotlin.models.notification

import ai.pams.android.kotlin.utils.DateUtils
import ai.pams.android.kotlin.utils.PAMUtils
import android.content.Context
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class NotificationList(
    var items: List<NotificationItem>? = null
){
    companion object{

        fun parse(jsonString: String?, context: Context): NotificationList{
            if(jsonString == null){
                val noti = NotificationList()
                noti.items = listOf()
                return noti
            }

            val noti = NotificationList();
            val list = mutableListOf<NotificationItem>()

            var json:JSONObject?
            try {
                json = JSONObject(jsonString)
            }catch(e: JSONException){
                noti.items = listOf()
                return noti
            }

            val itemsArray = json.optJSONArray("items") ?: JSONArray()
            val count = itemsArray.length()
            for(i in 0 until count){
                itemsArray.getJSONObject(i)?.let{  jsonItem->

                    val createdDate = jsonItem.optString("created_date")

                    var jsonPayload = jsonItem.optJSONObject("json_data");
                    jsonPayload = jsonPayload?.optJSONObject("pam")
                    val payload = mutableMapOf<String, Any>()

                    val iterator = jsonPayload?.keys()
                    while(iterator?.hasNext() == true){
                        val key = iterator.next()
                        jsonPayload?.opt(key)?.let{ data->
                            payload[key] = data
                        }
                    }

                    val date = PAMUtils.convertToJavaDate(DateUtils.localDateTimeFromString(createdDate))

                    val item = NotificationItem(
                        date = date,
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
