package ai.pams.android.kotlin.utils

import ai.pams.android.kotlin.models.notification.NotificationItem
import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject
import org.threeten.bp.LocalDateTime

class PAMUtils {
    companion object{
        fun isPamNotification(message: RemoteMessage): Boolean{
            return message.data.containsKey("pam")
        }

        fun convertRemoteMessageToPam(remoteMessage: RemoteMessage, context: Context): NotificationItem?{
            //remoteMessage.notification
            return when(remoteMessage.data.containsKey("pam")){
                true-> {
                    val pam = remoteMessage.data["pam"] ?: "{}"
                    val pamObj = JSONObject(pam)

                    val payload = mutableMapOf<String, Any>()
                    pamObj.keys().forEach { key->
                        if(key != "popupType" && key != "flex" && key != "pixel" && key != "url" && key != "created_date") {
                            payload[key] = pamObj.get(key)
                        }
                    }

                    val createdDate = when(pamObj.getString("created_date")){
                        null-> LocalDateTime.now()
                        else-> DateUtils.localDateTimeFromString(pamObj.getString("created_date"))
                    }

                    NotificationItem(
                        createdDate,
                        null,
                        remoteMessage.notification?.body,
                        pamObj.getString("flex"),
                        false,
                        payload,
                        pamObj.getString("pixel"),
                        null,
                        remoteMessage.notification?.title,
                        pamObj.getString("url"),
                        pamObj.optString("popupType"),
                    ).also {
                        it.parseFlex(context)
                    }
                }
                else-> null
            }
        }

        fun readPamNotificationFromIntent(intent: Intent, context: Context): NotificationItem? {

            return when(intent.extras?.containsKey("pam")){
                true-> {
                    val title = intent.extras?.getString("title")
                    val message = intent.extras?.getString("message")

                    val pam = intent.extras?.getString("pam")
                    val pamObj = JSONObject(pam)

                    val payload = mutableMapOf<String, Any>()
                    pamObj.keys().forEach { key->
                        if(key != "popupType" && key != "flex" && key != "pixel" && key != "url" && key != "created_date") {
                            payload[key] = pamObj.get(key)
                        }
                    }

                    val createdDate = when(pamObj.getString("created_date")){
                        null-> LocalDateTime.now()
                        else-> DateUtils.localDateTimeFromString(pamObj.getString("created_date"))
                    }

                    NotificationItem(
                        createdDate,
                        null,
                        message,
                        pamObj.getString("flex"),
                        false,
                        payload,
                        pamObj.getString("pixel"),
                        null,
                        title,
                        pamObj.getString("url"),
                        pamObj.getString("popupType"),
                    ).also {
                        it.parseFlex(context)
                    }

                }
                else-> null
            }
        }
    }
}