package pamsdk

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.telephony.SignalStrength
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.*
import pams.ai.demo.LoginPage
import pams.ai.demo.productsPage.ProductPage
import java.util.*

const val PamSDKName: String = "PamSDK"
const val sharedPreferenceKey: String = "PamSDK"

data class IPamResponse(
    @SerializedName("code") val code: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("contact_id") val contactID: String? = null
)

data class IPamOption(val pamServer: String, val publicDbAlias: String, val loginDbAlias: String)

typealias ListenerFunction = (Map<String, Any>) -> Unit

enum class PamCallback {
    onToken,
    onMessage
}

enum class PamStandardEvent(val string:String) {
    pageView("page_view"),
    addToCart("add_to_cart"),
    purchaseSuccess("purchase_success"),
    favourite("favourite"),
    savePush("save_push"),
}

class PamSDK {
    companion object {

        fun createPageViewPayload(title:String, url:String, others:Map<String, Any>? = null ): Map<String, Any>{
            var payload = mutableMapOf<String,Any>()
            payload["title"] = title
            payload["page_url"] = url
            others?.forEach{
                payload[it.key] = it.value
            }
            return payload
        }

        var app: Application? = null
        var options: IPamOption? = null

        var onTokenListener = mutableListOf<ListenerFunction>()
        var onMessageListener = mutableListOf<ListenerFunction>()

        fun listen(eventName: String, callBack: ListenerFunction) {
            if (eventName == PamCallback.onToken.toString()) {
                onTokenListener.add(callBack)
            } else if (eventName == PamCallback.onMessage.toString()) {
                onMessageListener.add(callBack)
            }
        }

        // TODO : lower case
        fun dispatch(eventName: String, args: Map<String, Any>) {
            if (eventName == PamCallback.onToken.toString()) {
                onTokenListener.forEach { callBack ->
                    callBack(args)
                }
            } else if (eventName == PamCallback.onMessage.toString()) {
                onMessageListener.forEach { callBack ->
                    callBack(args)
                }
            }
        }

        fun askNotificationPermission() {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }

                savePushKey(task.result.toString())
                saveToSharedPref("push_key", task.result.toString())
                dispatch(
                    PamCallback.onToken.toString(), mutableMapOf(
                        "token" to task.result.toString()
                    )
                )
            })
        }

        fun init(application: Application) {
            app = application
            val config = application.packageManager.getApplicationInfo(
                application.packageName,
                PackageManager.GET_META_DATA
            )
            options = IPamOption(
                pamServer = config.metaData.get("pam-server").toString(),
                publicDbAlias = config.metaData.get("public-db-alias").toString(),
                loginDbAlias = config.metaData.get("login-db-alias").toString()
            )

            Log.d(PamSDKName, "Pam has initial\n")
        }

        private fun saveToSharedPref(key: String, value: Any){
            val sharedPref =
                app?.getSharedPreferences(sharedPreferenceKey, Context.MODE_PRIVATE)
            val editor = sharedPref?.edit()
            editor?.putString(key, value.toString())
            editor?.apply()
        }

        private fun saveToSharedPref(key: String, value: Long){
            val sharedPref =
                app?.getSharedPreferences(sharedPreferenceKey, Context.MODE_PRIVATE)
            val editor = sharedPref?.edit()
            editor?.putLong(key, value)
            editor?.apply()
        }

        private fun removeFromSharedPref(key: String) {
            val sharedPref =
                app?.getSharedPreferences(sharedPreferenceKey, Context.MODE_PRIVATE)
            val editor = sharedPref?.edit()
            editor?.remove(key)
            editor?.apply()
        }

        private fun getFromSharedPref(key: String): String? {
            val sharedPref = app?.getSharedPreferences(sharedPreferenceKey, Context.MODE_PRIVATE)
            return sharedPref?.getString(key, null)
        }

        private fun getLongFromSharedPref(key: String): Long {
            val sharedPref = app?.getSharedPreferences(sharedPreferenceKey, Context.MODE_PRIVATE)
            return sharedPref?.getLong(key,0) ?: 0
        }

        private fun getSessionID(): String{
            val expire = getLongFromSharedPref("session_expire_in_mils")
            val session = getFromSharedPref("session_id")

            //New Session
            if(System.currentTimeMillis() - expire >  (1000 * 60 * 60) ){
                val newExpire = System.currentTimeMillis() + 1000 * 60 * 60
                val newSession = UUID.randomUUID().toString()

                saveToSharedPref("session_expire_in_mils", newExpire)
                saveToSharedPref("session_id", newSession)
                return newSession
            }

            //Old Session
            val newExpire = System.currentTimeMillis() + 1000 * 60 * 60
            val newSession =  session ?: UUID.randomUUID().toString()
            saveToSharedPref("session_expire_in_mils", newExpire)
            saveToSharedPref("session_id", newSession)

            return newSession
        }

        fun getContactID(): String? {
            val contactID = getFromSharedPref("_contact_id")
            if (contactID == "") {
                return null
            }
            return contactID
        }

        fun userLogin(customerID: String) {
            saveToSharedPref("customer_id", customerID)
            track(
                "login", mutableMapOf(
                    "form_fields" to mutableMapOf(
                        "customer" to customerID
                    )
                )
            )
        }

        fun userLogout() {
            track(
                "logout", mapOf(
                    "_delete_media" to mapOf(
                        "android_notification" to ""
                    )
                )
            )

            removeFromSharedPref("customer_id")
            removeFromSharedPref("login_contact_id")
        }

        fun savePushKey(pushKey: String) {
            track(
               PamStandardEvent.savePush, mutableMapOf(
                    "form_fields" to mutableMapOf(
                        "android_notification" to pushKey
                    )
                )
            )
        }

        fun track(event: PamStandardEvent, payload: Map<String, Any>? = null) {
            track(event.string, payload)
        }

        fun track(eventName: String, payload: Map<String, Any>? = null) {

            val postBody = mutableMapOf<String, Any>()

            postBody["platform"] = "android"
            postBody["event"] = eventName

            val formFields = mutableMapOf<String, Any>()
            formFields["_database"] = when (getFromSharedPref("customer_id")) {
                null -> options?.publicDbAlias!!
                else -> options?.loginDbAlias!!
            }

            val isLogin = getFromSharedPref("customer_id") == null
            formFields["_contact_id"] = when (isLogin) {
                true -> getFromSharedPref("login_contact_id") ?: ""
                else -> getFromSharedPref("public_contact_id") ?: ""
            }

            payload?.get("page_url")?.let {
                postBody["page_url"] = it
            }
            payload?.get("page_title")?.let {
                postBody["page_title"] = it
            }

            payload?.forEach {
                if (it.key != "page_url" || it.key != "page_title") {
                    formFields[it.key] = it.value
                }
            }

            formFields["_session"] = getSessionID()

            postBody["form_fields"] = formFields
            Log.d(PamSDKName, "track payload $postBody\n")

            Http.getInstance().post(
                url = "${options?.pamServer!!}/trackers/events",
                headers = mapOf(),
                queryString = mapOf(),
                data = postBody
            ) { text, err ->
                val response = Gson().fromJson(text, IPamResponse::class.java)
                if (response.contactID != null) {
                    Log.d(PamSDKName, "track response is $text\n")

                    if (isLogin) {
                        saveToSharedPref("login_contact_id", response.contactID)
                    } else {
                        saveToSharedPref("public_contact_id", response.contactID)
                    }
                } else if (response.code != null && response.message != null) {
                    Log.d(PamSDKName, "track error $err\n")
                }
            }

            Log.d(PamSDKName, "track has been send\n")
        }
    }
}