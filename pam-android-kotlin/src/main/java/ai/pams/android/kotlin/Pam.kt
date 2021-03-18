package ai.pams.android.kotlin

import ai.pams.android.kotlin.http.Http
import ai.pams.android.kotlin.queue.QueueTrackerManager
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
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

public class Pam {
    companion object {
        var shared = Pam()

        fun initialize(application: Application, enableLog: Boolean = false) {
            shared.isLogEnable = enableLog
            shared.app = application

            val config = application.packageManager.getApplicationInfo(
                application.packageName,
                PackageManager.GET_META_DATA
            )

            var pamServer = config.metaData.get("pam-server").toString()
            if( pamServer.endsWith("/") ){
                pamServer = pamServer.replaceFirst(".$", "")
            }

            shared.options = PamOption(
                pamServer = pamServer,
                publicDbAlias = config.metaData.get("public-db-alias").toString(),
                loginDbAlias = config.metaData.get("login-db-alias").toString()
            )
        }

        fun track(eventName: String, payload: Map<String, Any>? = null) = shared.track(
            eventName,
            payload
        )
        fun appReady() = shared.appReady()
        fun listen(eventName: String, callback: ListenerFunction) =
            shared.listen(eventName, callback)
        fun userLogout() = shared.userLogout()
        fun userLogin(customerID: String) = shared.userLogin(customerID)
        fun askNotificationPermission() = shared.askNotificationPermission()
    }

    enum class SaveKey(val keyName:String) {
        CustomerID("@pam_customer_id"),
        ContactID("@_pam_contact_id"),
        LoginContactID("@_pam_login_contact_id"),
        PushKey("@_pam_push_key")
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

    private var platformVersionCache:String? = null
    private var osVersionCache:String? = null
    private var appVersionCache:String? = null
    private var publicContactID:String? = null
    private var loginContactID:String? = null
    private var custID:String? = null

    private var sharedPreferences: SharedPreferences? = null

    init {
        queueTrackerManager.callback = { eventName, payload, deleteLoginContactAfterPost ->
            postTracker(eventName, payload, deleteLoginContactAfterPost)
        }
    }

    fun listen(eventName: String, callback: ListenerFunction) {
        when (eventName.toLowerCase(Locale.ENGLISH)) {
            "ontoken" -> onTokenListener.add(callback)
            "onmessage" -> onMessageListener.add(callback)
        }
    }

    private fun dispatch(eventName: String, args: Map<String, Any>) {
        val listenerList = when (eventName.toLowerCase(Locale.ENGLISH)) {
            "ontoken" -> onTokenListener
            "onmessage" -> onMessageListener
            else -> null
        }

        listenerList?.forEach { callback ->
            callback(args)
        }
    }

    fun askNotificationPermission() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            saveValue(SaveKey.PushKey, task.result.toString())
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
            sharedPreferences = app?.getSharedPreferences(
                "pams-ai-local-preference",
                Context.MODE_PRIVATE
            )
        }
        return sharedPreferences
    }

    private fun saveValue(key: SaveKey, value: Any) {
        val sharedPref = getSharedPreference()
        val editor = sharedPref?.edit()
        editor?.putString(key.keyName, value.toString())
        editor?.apply()
    }

    private fun removeValue(key: SaveKey) {
        val sharedPref = getSharedPreference()
        val editor = sharedPref?.edit()
        editor?.remove(key.keyName)
        editor?.apply()
    }

    private fun readValue(key: SaveKey): String? {
        val sharedPref = getSharedPreference()
        return sharedPref?.getString(key.keyName, null)
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

    fun userLogin(customerID: String) {
        custID = customerID
        saveValue(SaveKey.CustomerID, customerID)
        track("login")
    }

    fun userLogout() {
        track(
            "logout", mapOf(
                "_delete_media" to mapOf(
                    "android_notification" to ""
                )
            ), true
        )
    }

    fun setDeviceToken(deviceToken: String) {
        track(
            "save_push", mapOf(
                "android_notification" to deviceToken
            )
        )
        dispatch("onToken", mapOf("token" to deviceToken))
    }

    fun track(eventName: String, payload: Map<String, Any>? = null, deleteLoginContactAfterPost: Boolean = false) {
        this.queueTrackerManager.enqueue(eventName, payload, deleteLoginContactAfterPost)
    }

    private fun isUserLogin(): Boolean {
        if(custID == null) {
            custID = readValue(SaveKey.CustomerID)
        }
        return custID != null
    }

    private fun postTracker(eventName: String, payload: Map<String, Any>?, deleteLoginContactAfterPost: Boolean) {
        val url = "${options?.pamServer!!}/trackers/events"

        if(platformVersionCache == null){
            val pInfo = app?.packageManager?.getPackageInfo(app?.packageName ?: "", 0)
            val packageName = app?.packageName ?: ""
            appVersionCache = "$packageName (${pInfo?.versionName ?: ""})"
            osVersionCache = "Android: ${Build.VERSION.SDK_INT}"

            platformVersionCache = "Android: ${Build.VERSION.SDK_INT} (${Build.VERSION.RELEASE}), $packageName: $appVersionCache"
        }

        val body = mutableMapOf<String, Any>(
            "event" to eventName,
            "platform" to (platformVersionCache ?: ""),
        )

        val formField = mutableMapOf<String, Any>(
            "os_version" to (osVersionCache ?: ""),
            "app_version" to (appVersionCache ?: ""),
            "_session_id" to getSessionID()
        )

        val publicContact = publicContactID ?: readValue(SaveKey.ContactID)
        val loginContact = loginContactID ?: readValue(SaveKey.LoginContactID)

        if (loginContact != null ){
            formField["_contact_id"] = loginContact
        }else if(publicContact != null){
            formField["_contact_id"] = publicContact
        }

        payload?.forEach {
            if (it.key != "page_url" || it.key != "page_title") {
                formField[it.key] = it.value
            }else{
                body[it.key] = it.value
            }
        }


        if(isUserLogin()){
            formField["_database"] = options?.loginDbAlias ?: ""
            formField["customer"] = custID ?: (readValue(SaveKey.CustomerID) ?: "")
        }else{
            formField["_database"] = options?.publicDbAlias ?: ""
        }

        body["form_fields"] = formField

        if(isLogEnable){
            Log.d("PAM", "🦄 : POST Event = 🍀$eventName🍀")
            Log.d("PAM","🦄 : Payload")
            Log.d("PAM","🍉🍉🍉🍉🍉🍉🍉🍉🍉🍉🍉🍉🍉\n$body\n🍉🍉🍉🍉🍉🍉🍉🍉🍉🍉🍉🍉🍉\n\n")
        }

        Http.getInstance().post(
            url = url,
            headers = mapOf(),
            queryString = mapOf(),
            data = body
        ) { text, err ->
            if (enableLog) {
                Log.d("Pam", "track response is $text\n")
            }

            try{
                val response = Gson().fromJson(text, PamResponse::class.java)
                if (response.contactID != null) {

                    if (isUserLogin()) {
                        saveValue(SaveKey.LoginContactID, response.contactID)
                        this.loginContactID = response.contactID
                    } else {
                        saveValue(SaveKey.ContactID, response.contactID)
                        this.publicContactID = response.contactID
                    }

                } else if (response.code != null && response.message != null) {
                    if (enableLog) {
                        Log.d("Pam", "track error $err\n")
                    }
                }
            }catch (e: JsonSyntaxException){

            }

            val task = CoroutineScope(Dispatchers.Main)
            task.launch {
                if( deleteLoginContactAfterPost ){
                    this@Pam.custID = null
                    this@Pam.loginContactID = null
                    this@Pam.removeValue(SaveKey.CustomerID)
                    this@Pam.removeValue(SaveKey.LoginContactID)
                }
                queueTrackerManager.next()
            }
        }
    }
}