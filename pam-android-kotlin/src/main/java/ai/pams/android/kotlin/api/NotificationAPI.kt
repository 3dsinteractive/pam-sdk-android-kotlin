package ai.pams.android.kotlin.api

import ai.pams.android.kotlin.NullableTypAdapterFactory
import ai.pams.android.kotlin.Pam
import ai.pams.android.kotlin.consent.UserConsentPermissions
import ai.pams.android.kotlin.http.Http
import ai.pams.android.kotlin.models.notification.NotificationItem
import ai.pams.android.kotlin.models.notification.NotificationList
import android.util.Log
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class NotificationAPI {
    companion object {

        private fun createGson() = GsonBuilder()
            .registerTypeAdapterFactory(NullableTypAdapterFactory())
            .create()

        fun loadPushNotificationsFromCustomerID(
            customerID: String,
            callBack: ((List<NotificationItem>) -> Unit)?
        ) {
            val db = Pam.getDatabaseAlias()
            val contactID = Pam.getContactID() ?: "-"
            val pamServerURL = Pam.shared.options?.pamServer ?: "-"

            val endpoint =
                "${pamServerURL}/api/app-notifications/?_database=${db}&_contact_id=${contactID}&customer=${customerID}"

            Http.getInstance()
                .get(endpoint) { result, _ ->
                    val model = createGson().fromJson(result, NotificationList::class.java)
                    CoroutineScope(Dispatchers.Main).launch {
                        callBack?.invoke(model.items ?: listOf())
                    }
                }
        }

        fun loadPushNotificationsMobile(
            mobile: String,
            callBack: ((List<NotificationItem>) -> Unit)?
        ) {
            val db = Pam.getDatabaseAlias()
            val contactID = Pam.getContactID() ?: "-"
            val pamServerURL = Pam.shared.options?.pamServer ?: "-"

            val endpoint =
                "${pamServerURL}/api/app-notifications/?_database=${db}&_contact_id=${contactID}&sms=${mobile}"

            Http.getInstance()
                .get(endpoint) { result, _ ->
                    val model = createGson().fromJson(result, NotificationList::class.java)
                    CoroutineScope(Dispatchers.Main).launch {
                        callBack?.invoke(model.items ?: listOf())
                    }
                }
        }

        fun loadPushNotificationsEmail(
            email: String,
            callBack: ((List<NotificationItem>) -> Unit)?
        ) {
            val db = Pam.getDatabaseAlias()
            val contactID = Pam.getContactID() ?: "-"
            val pamServerURL = Pam.shared.options?.pamServer ?: "-"

            val endpoint =
                "${pamServerURL}/api/app-notifications/?_database=${db}&_contact_id=${contactID}&email=${email}"

            Http.getInstance()
                .get(endpoint) { result, _ ->
                    val model = createGson().fromJson(result, NotificationList::class.java)
                    CoroutineScope(Dispatchers.Main).launch {
                        callBack?.invoke(model.items ?: listOf())
                    }
                }
        }
    }


}