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

        fun loadPushNotifications(
            context: Context,
            mobile: String? = null,
            email: String? = null,
            customerID: String? = null,
            contactID: String? = null,
            callBack: ((List<NotificationItem>) -> Unit)?
        ) {

            val pamServerURL = Pam.shared.options?.pamServer ?: "-"

            val endpoint =
                "${pamServerURL}/api/app-notifications"

            val queryString = mutableMapOf<String,String>()

            Pam.getDatabaseAlias()?.let{
                queryString["_database"] = it
            }

            val contact = when (contactID) {
                null -> Pam.getContactID()
                else -> contactID
            }
            contact?.let {
                queryString["_contact_id"] = it
            }

            customerID?.let {
                queryString["customer"] = it
            }

            mobile?.let {
                queryString["sms"] = it
            }

            email?.let {
                queryString["email"] = it
            }

            Http.getInstance()
                .get(
                    url = endpoint,
                    queryString = queryString
                ) { result, _ ->
                    val model = NotificationList.parse(result, context);
                    CoroutineScope(Dispatchers.Main).launch {
                        callBack?.invoke(model.items ?: listOf())
                    }
                }
        }
    }

}