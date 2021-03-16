package pamsdk

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.*
import java.util.*


data class PamResponse(
    @SerializedName("code") val code: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("contact_id") val contactID: String? = null
)

data class PamMessagePayload(
    @SerializedName("flex") val flex: String? = null,
    @SerializedName("pixel") val pixel: String? = null,
    @SerializedName("url") val url: String? = null,
    @SerializedName("created_date") val CreatedDate: String? = null
)

data class PamOption(val pamServer: String, val publicDbAlias: String, val loginDbAlias: String)
typealias ListenerFunction = (Map<String, Any>) -> Unit

class Pam {
    companion object {
        var shared = Pam()

        fun initialize(application: Application, enableLog: Boolean = false) {
            shared.isLogEnable = enableLog
            shared.app = application

            val config = application.packageManager.getApplicationInfo(
                application.packageName,
                PackageManager.GET_META_DATA
            )
            shared.options = PamOption(
                pamServer = config.metaData.get("pam-server").toString(),
                publicDbAlias = config.metaData.get("public-db-alias").toString(),
                loginDbAlias = config.metaData.get("login-db-alias").toString()
            )
        }

        fun track(eventName: String, payload: Map<String, Any>? = null) = shared.track(eventName, payload)
        fun appReady() = shared.appReady()
        fun listen(eventName: String, callback: ListenerFunction) =
            shared.listen(eventName, callback)
        fun userLogout() = shared.userLogout()
        fun userLogin(customerID: String) = shared.userLogin(customerID)
        fun askNotificationPermission() = shared.askNotificationPermission()
    }

    private var sessionID: String? = null
    private var sessionExpire: Long? = null

    var isLogEnable = false
    var app: Application? = null
    var options: PamOption? = null
    private val queueTrackerManager = QueueTrackerManager()
    var enableLog = false
    var isAppReady = false

    var onTokenListener = mutableListOf<ListenerFunction>()
    var onMessageListener = mutableListOf<ListenerFunction>()
    var pendingMessage = mutableListOf<Map<String, Any>>()

    private var sharedPreferences: SharedPreferences? = null

    init {
        queueTrackerManager.callback = { eventName, payload ->
            postTracker(eventName, payload)
        }
    }

    fun listen(eventName: String, callback: ListenerFunction) {
        val eventName = eventName.toLowerCase()
        if (eventName == "ontoken") {
            onTokenListener.add(callback)
        } else if (eventName == "onmessage") {
            onMessageListener.add(callback)
        }
    }

    private fun dispatch(eventName: String, args: Map<String, Any>) {
        if (eventName == "onToken") {
            onTokenListener.forEach { callback ->
                callback(args)
            }
        } else if (eventName == "onMessage") {
            onMessageListener.forEach { callback ->
                callback(args)
            }
        }
    }

    fun askNotificationPermission() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            saveValue("push_key", task.result.toString())
            setDeviceToken(task.result.toString())
        })
    }

    fun appReady() {
        if (!isAppReady) {
            isAppReady = true
            track("app_launch")
            pendingMessage.forEach { args ->
                dispatch("onMessage", args)
            }
            pendingMessage.clear()
        }
    }

    private fun getSharedPreference(): SharedPreferences? {
        if (sharedPreferences == null) {
            sharedPreferences = app?.getSharedPreferences("pams.ai/localpref", Context.MODE_PRIVATE)
        }
        return sharedPreferences
    }

    private fun saveValue(key: String, value: Any) {
        val sharedPref = getSharedPreference()
        val editor = sharedPref?.edit()
        editor?.putString(key, value.toString())
        editor?.apply()
    }

    private fun removeValue(key: String) {
        val sharedPref = getSharedPreference()
        val editor = sharedPref?.edit()
        editor?.remove(key)
        editor?.apply()
    }

    private fun readValue(key: String): String? {
        val sharedPref = getSharedPreference()
        return sharedPref?.getString(key, null)
    }

    private fun getSessionID(): String {

        val exp = this.sessionExpire ?: -1000000
        val now = System.currentTimeMillis()
        val diff = now - exp

        this.sessionExpire = System.currentTimeMillis() + 1000 * 60 * 60

        if (diff >= 3600) {
            this.sessionID = UUID.randomUUID().toString()
            return this.sessionID ?: ""
        }

        if (this.sessionID != null) {
            return this.sessionID ?: ""
        } else {
            this.sessionID = UUID.randomUUID().toString()
        }

        return this.sessionID ?: ""
    }

    fun getContactID(): String? {
        return readValue("_contact_id")
    }

    fun getCustomerID(): String? {
        return readValue("customer_id")
    }

    fun userLogin(customerID: String) {
        saveValue("customer_id", customerID)
        track(
            "login", mapOf(
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

        removeValue("customer_id")
        removeValue("login_contact_id")
    }

    fun setDeviceToken(deviceToken: String) {
        track(
            "save_push", mapOf(
                "android_notification" to deviceToken
            )
        )
        dispatch("onToken", mapOf("token" to deviceToken))
    }

    fun isNotPamIntent(intent: Intent?): Boolean {
        intent?.extras

//            ?.keySet()?.forEach { key ->
//            if (key == "pam" && intent.extras?.get(key) != null) {
//                // Pam handle the intent on it own
//                intent.getStringExtra(key)?.let {
//                    val pamPayload = Gson().fromJson(it, IPamMessagePayload::class.java)
//
//                    pamPayload.pixel?.let {
//                        if (enableLog) {
//                            Log.d(PamSDKName, "Calling pixel")
//                        }
//
//                        Http.getInstance().get(
//                            url = pamPayload.pixel,
//                        ) { text, err ->
//                            if (enableLog) {
//                                Log.d(PamSDKName, "Calling pixel response is $text\n")
//                            }
//                            if (err != null) {
//                                Log.d(PamSDKName, "Calling pixel error is ${err.message}\n")
//                            }
//                        }
//                    }
//
//                    handler?.invoke(pamPayload)
//                    return false
//                }
//            }
//        }

        return true
    }

    fun track(eventName: String, payload: Map<String, Any>? = null) {
        this.queueTrackerManager.enqueue(eventName, payload)
    }

    private fun postTracker(eventName: String, payload: Map<String, Any>?) {
        val postBody = mutableMapOf<String, Any>()

        postBody["platform"] = "android"
        postBody["event"] = eventName

        val formFields = mutableMapOf<String, Any>()
        formFields["_database"] = when (readValue("customer_id")) {
            null -> options?.publicDbAlias!!
            else -> options?.loginDbAlias!!
        }

        val isLogin = readValue("customer_id") != null

        formFields["_contact_id"] = when (isLogin) {
            true -> readValue("login_contact_id") ?: ""
            else -> readValue("public_contact_id") ?: ""
        }

        if (isLogin) {
            readValue("customer_id")?.let {
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
            Log.d("Pam", "track payload $postBody\n")
        }

        Http.getInstance().post(
            url = "${options?.pamServer!!}/trackers/events",
            headers = mapOf(),
            queryString = mapOf(),
            data = postBody
        ) { text, err ->
            if (enableLog) {
                Log.d("Pam", "track response is $text\n")
            }

            val response = Gson().fromJson(text, PamResponse::class.java)
            if (response.contactID != null) {

                if (isLogin) {
                    saveValue("login_contact_id", response.contactID)
                } else {
                    saveValue("public_contact_id", response.contactID)
                }
            } else if (response.code != null && response.message != null) {
                if (enableLog) {
                    Log.d("Pam", "track error $err\n")
                }
            }

            val task = CoroutineScope(Dispatchers.IO)
            task.launch {
                queueTrackerManager?.next()
            }
        }

        if (enableLog) {
            Log.d("Pam", "track has been send\n")
        }
    }
}