package pamsdk

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Parcelable
import android.telephony.SignalStrength
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.*
import kotlinx.parcelize.Parcelize
import models.CartProductModel
import pams.ai.demo.LoginPage
import pams.ai.demo.productsPage.ProductPage
import webservices.MockAPI
import java.util.*

const val PamSDKName: String = "PamSDK"
const val sharedPreferenceKey: String = "PamSDK"

data class IPamResponse(
    @SerializedName("code") val code: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("contact_id") val contactID: String? = null
)

data class IPamMessagePayload(
    @SerializedName("flex") val flex: String? = null,
    @SerializedName("pixel") val pixel: String? = null,
    @SerializedName("url") val url: String? = null,
    @SerializedName("created_date") val CreatedDate: String? = null
)

data class IPamOption(val pamServer: String, val publicDbAlias: String, val loginDbAlias: String)

typealias ListenerFunction = (Map<String, Any>) -> Unit

typealias PamIntentCallback = (IPamMessagePayload) -> Unit

enum class PamCallback(val string: String) {
    onToken("onToken"),
    onMessage("onMessage")
}

enum class PamStandardEvent(val string: String) {
    login("login"),
    appLaunch("app_launch"),
    pageView("page_view"),
    addToCart("add_to_cart"),
    purchaseSuccess("purchase_success"),
    favourite("favourite"),
    savePush("save_push"),
    openPush("open_push"),
}

class PamSDK {
    companion object {

        var app: Application? = null
        var options: IPamOption? = null
        var queueTrackerManager: QueueTrackerManager? = null
        var enableLog = false
        var isAppReady = false

        var onTokenListener = mutableListOf<ListenerFunction>()
        var onMessageListener = mutableListOf<ListenerFunction>()
        var pendingMessage = mutableListOf<Map<String, Any>>()

        fun init(application: Application, enableLog: Boolean = false) {
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
            queueTrackerManager = QueueTrackerManager(this.trackCallback())
            this.enableLog = enableLog

            if (enableLog) {
                Log.d(PamSDKName, "Pam has initial\n")
            }
        }

        fun listen(event: PamCallback, callback: ListenerFunction) {
            listen(event.string, callback)
        }

        fun listen(eventName: String, callback: ListenerFunction) {
            if (eventName == PamCallback.onToken.string) {
                onTokenListener.add(callback)
            } else if (eventName == PamCallback.onMessage.string) {
                onMessageListener.add(callback)
            }
        }

        fun dispatch(event: PamCallback, args: Map<String, Any>) {
            dispatch(event.string, args)
        }

        fun dispatch(eventName: String, args: Map<String, Any>) {
            if (eventName == PamCallback.onToken.toString()) {
                onTokenListener.forEach { callback ->
                    callback(args)
                }
            } else if (eventName == PamCallback.onMessage.toString()) {
                onMessageListener.forEach { callback ->
                    callback(args)
                }
            }
        }

        fun receiveMessage(
            imageURL: String,
            title: String,
            message: String,
            payload: Map<String, Any>
        ): Boolean {
            if (payload["pam"] != null) {
                val args = mapOf(
                    "image_url" to imageURL,
                    "title" to title,
                    "message" to message,
                    "payload" to payload,
                )

                if (isAppReady) {
                    dispatch(PamCallback.onMessage, args)
                } else {
                    pendingMessage.add(args)
                }
                return false
            }
            return true
        }

        fun askNotificationPermission() {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }

                saveToSharedPref("push_key", task.result.toString())
                savePushKey(task.result.toString())
                dispatch(
                    PamCallback.onToken, mapOf(
                        "token" to task.result.toString()
                    )
                )
            })
        }

        fun appReady() {
            if (!isAppReady) {
                isAppReady = true

                track(PamStandardEvent.appLaunch)
                pendingMessage.forEach { args ->
                    dispatch(PamCallback.onMessage, args)
                }
                pendingMessage.clear()
            }
        }

        fun saveToSharedPref(key: String, value: Any) {
            val sharedPref =
                app?.getSharedPreferences(sharedPreferenceKey, Context.MODE_PRIVATE)
            val editor = sharedPref?.edit()
            editor?.putString(key, value.toString())
            editor?.apply()
        }

        fun saveToSharedPref(key: String, value: Long) {
            val sharedPref =
                app?.getSharedPreferences(sharedPreferenceKey, Context.MODE_PRIVATE)
            val editor = sharedPref?.edit()
            editor?.putLong(key, value)
            editor?.apply()
        }

        fun removeFromSharedPref(key: String) {
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
            return sharedPref?.getLong(key, 0) ?: 0
        }

        private fun getSessionID(): String {
            val expire = getLongFromSharedPref("session_expire_in_mils")
            val session = getFromSharedPref("session_id")

            if (System.currentTimeMillis() - expire > (1000 * 60 * 60)) {
                val newExpire = System.currentTimeMillis() + 1000 * 60 * 60
                val newSession = UUID.randomUUID().toString()

                saveToSharedPref("session_expire_in_mils", newExpire)
                saveToSharedPref("session_id", newSession)
                return newSession
            }

            val newExpire = System.currentTimeMillis() + 1000 * 60 * 60
            val newSession = session ?: UUID.randomUUID().toString()
            saveToSharedPref("session_expire_in_mils", newExpire)
            saveToSharedPref("session_id", newSession)

            return newSession
        }

        fun getContactID(): String? {
            return getFromSharedPref("_contact_id")
        }

        fun getCustomerID(): String? {
            return getFromSharedPref("customer_id")
        }

        fun userLogin(customerID: String) {
            saveToSharedPref("customer_id", customerID)
            track(
                PamStandardEvent.login, mapOf(
                    "customer" to customerID
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
                PamStandardEvent.savePush, mapOf(
                    "android_notification" to pushKey
                )
            )
        }

        fun isNotPamIntent(intent: Intent? = null, handler: PamIntentCallback? = null): Boolean {
            if (intent == null) {
                return true
            }

            intent.extras?.keySet()?.forEach { key ->
                if (key == "pam" && intent.extras?.get(key) != null) {
                    // Pam handle the intent on it own
                    intent.getStringExtra(key)?.let {
                        val pamPayload = Gson().fromJson(it, IPamMessagePayload::class.java)

                        pamPayload.pixel?.let {
                            if (enableLog) {
                                Log.d(PamSDKName, "Calling pixel")
                            }

                            Http.getInstance().get(
                                url = pamPayload.pixel,
                            ) { text, err ->
                                if (enableLog) {
                                    Log.d(PamSDKName, "Calling pixel response is $text\n")
                                }
                                if (err != null) {
                                    Log.d(PamSDKName, "Calling pixel error is ${err.message}\n")
                                }
                            }
                        }

                        handler?.invoke(pamPayload)
                        return false
                    }
                }
            }

            return true
        }

        fun track(event: PamStandardEvent, payload: Map<String, Any>? = null) {
            track(event.string, payload)
        }

        fun track(eventName: String, payload: Map<String, Any>? = null) {
            this.queueTrackerManager?.enqueue(eventName, payload)
        }

        private fun trackCallback(): ((eventName: String, payload: Map<String, Any>?) -> Unit) {
            return fun(eventName: String, payload: Map<String, Any>?) {
                val postBody = mutableMapOf<String, Any>()

                postBody["platform"] = "android"
                postBody["event"] = eventName

                val formFields = mutableMapOf<String, Any>()
                formFields["_database"] = when (getFromSharedPref("customer_id")) {
                    null -> options?.publicDbAlias!!
                    else -> options?.loginDbAlias!!
                }

                val isLogin = getFromSharedPref("customer_id") != null

                formFields["_contact_id"] = when (isLogin) {
                    true -> getFromSharedPref("login_contact_id") ?: ""
                    else -> getFromSharedPref("public_contact_id") ?: ""
                }

                if (isLogin) {
                    getFromSharedPref("customer_id")?.let {
                        formFields["customer"] = it
                    }
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
                if (enableLog) {
                    Log.d(PamSDKName, "track payload $postBody\n")
                }

                Http.getInstance().post(
                    url = "${options?.pamServer!!}/trackers/events",
                    headers = mapOf(),
                    queryString = mapOf(),
                    data = postBody
                ) { text, err ->
                    if (enableLog) {
                        Log.d(PamSDKName, "track response is $text\n")
                    }

                    val response = Gson().fromJson(text, IPamResponse::class.java)
                    if (response.contactID != null) {

                        if (isLogin) {
                            saveToSharedPref("login_contact_id", response.contactID)
                        } else {
                            saveToSharedPref("public_contact_id", response.contactID)
                        }
                    } else if (response.code != null && response.message != null) {
                        if (enableLog) {
                            Log.d(PamSDKName, "track error $err\n")
                        }
                    }

                    val task = CoroutineScope(Dispatchers.IO)
                    task.launch {
                        queueTrackerManager?.next()
                    }
                }

                if (enableLog) {
                    Log.d(PamSDKName, "track has been send\n")
                }
            }
        }
    }
}