package ai.pams.android.kotlin.api

import ai.pams.android.kotlin.NullableTypeAdapterFactory
import ai.pams.android.kotlin.Pam
import ai.pams.android.kotlin.http.Http
import ai.pams.android.kotlin.models.notification.NotificationItem
import ai.pams.android.kotlin.models.notification.NotificationList
import android.content.Context
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationAPI {
    companion object {

        private fun createGson() = GsonBuilder()
            .registerTypeAdapterFactory(NullableTypeAdapterFactory())
            .create()

        fun loadPushNotificationsFromCustomerID(context: Context,
            customerID: String,
            callBack: ((List<NotificationItem>) -> Unit)?
        ) {
            val db = Pam.getDatabaseAlias() ?: ""
            val contactID = Pam.getContactID() ?: "-"
            val pamServerURL = Pam.shared.options?.pamServer ?: "-"

            val endpoint =
                "${pamServerURL}/api/app-notifications/"

            val queryString = mapOf(
                "_database" to db,
                "_contact_id" to contactID,
                "customer" to customerID
            )

            Http.getInstance()
                .get(
                    url=endpoint,
                    queryString = queryString
                ) { result, _ ->
                    val model = createGson().fromJson(result, NotificationList::class.java)
                    model.items?.forEach {
                        it.parseFlex(context)
                    }
                    CoroutineScope(Dispatchers.Main).launch {
                        callBack?.invoke(model.items ?: listOf())
                    }
                }
        }

        fun loadPushNotificationsMobile(context: Context,
            mobile: String,
            callBack: ((List<NotificationItem>) -> Unit)?
        ) {
            val db = Pam.getDatabaseAlias() ?: ""
            val contactID = Pam.getContactID() ?: "-"
            val pamServerURL = Pam.shared.options?.pamServer ?: "-"

            val endpoint =
                "${pamServerURL}/api/app-notifications/"

            val queryString = mapOf(
                "_database" to db,
                "_contact_id" to contactID,
                "sms" to mobile
            )

            Http.getInstance()
                .get(url=endpoint,
                    queryString = queryString
                ) { result, _ ->
                    val model = createGson().fromJson(result, NotificationList::class.java)
                    model.items?.forEach {
                        it.parseFlex(context)
                    }
                    CoroutineScope(Dispatchers.Main).launch {
                        callBack?.invoke(model.items ?: listOf())
                    }
                }
        }

        fun loadPushNotificationsEmail(context: Context,
            email: String,
            callBack: ((List<NotificationItem>) -> Unit)?
        ) {
            val db = Pam.getDatabaseAlias() ?: ""
            val contactID = Pam.getContactID() ?: "-"
            val pamServerURL = Pam.shared.options?.pamServer ?: "-"

            val endpoint =
                "${pamServerURL}/api/app-notifications/"

            val queryString = mapOf(
                "_database" to db,
                "_contact_id" to contactID,
                "email" to email
            )

            Http.getInstance()
                .get(
                    url=endpoint,
                    queryString = queryString
                ) { result, _ ->
                    val model = createGson().fromJson(result, NotificationList::class.java)
                    model.items?.forEach {
                        it.parseFlex(context)
                    }
                    CoroutineScope(Dispatchers.Main).launch {
                        callBack?.invoke(model.items ?: listOf())
                    }
                }
        }
    }

}