package ai.pams.android.kotlin

import ai.pams.android.kotlin.consent.*
import ai.pams.android.kotlin.http.Http
import ai.pams.android.kotlin.models.notification.NotificationItem
import ai.pams.android.kotlin.models.notification.NotificationList
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
    @SerializedName("contact_id") val contactID: String? = null,
    @SerializedName("_database") val database: String? = null,
    @SerializedName("consent_id") val consentID: String? = null
)

data class PamMessagePayload(
    @SerializedName("flex") val flex: String? = null,
    @SerializedName("pixel") val pixel: String? = null,
    @SerializedName("url") val url: String? = null,
    @SerializedName("created_date") val CreatedDate: String? = null
)

data class PamOption(
    val pamServer: String,
    val publicDbAlias: String,
    val loginDbAlias: String,
    val trackingConsentMessageID: String,
    val trackingConsentInterval: Long,
)

typealias ListenerFunction = (Map<String, Any>) -> Unit
typealias TrackerCallback = (PamResponse) -> Unit

class Pam {
    companion object {
        var shared = Pam()

        fun loadConsentPermissions(
            consentMessageIds: List<String>,
            onLoad: (Map<String, UserConsentPermissions>) -> Unit
        ) {
            val api = ConsentAPI()
            api.setOnPermissionLoadCallBack(onLoad)
            api.loadConsentPermissions(consentMessageIds)
        }

        fun loadConsentPermissions(
            consentMessageIds: String,
            onLoad: (UserConsentPermissions) -> Unit
        ) {
            loadConsentPermissions(listOf(consentMessageIds)) {
                it[consentMessageIds]?.let { userPermissions ->
                    CoroutineScope(Dispatchers.Main).launch {
                        onLoad.invoke(userPermissions)
                    }
                }
            }
        }

        fun loadConsentDetails(
            consentMessageIds: List<String>,
            onLoad: (Map<String, BaseConsentMessage>) -> Unit
        ) {
            val api = ConsentAPI()
            api.setOnConsentLoaded(onLoad)
            api.loadConsent(consentMessageIds)
        }

        fun loadConsentDetails(consentMessageIds: String, onLoad: (BaseConsentMessage) -> Unit) {
            loadConsentDetails(listOf(consentMessageIds)) {
                it[consentMessageIds]?.let { msg ->
                    CoroutineScope(Dispatchers.Main).launch {
                        onLoad.invoke(msg)
                    }
                }
            }
        }

        fun submitConsent(
            consents: List<BaseConsentMessage>,
            onSubmit: (result: Map<String, AllowConsentResult>, consentIDs: String) -> Unit
        ) {
            val api = ConsentAPI()
            api.setOnConsentSubmit {
                val ids = mutableListOf<String>()
                it.forEach { (_, v) ->
                    v.consentID?.let { id ->
                        ids.add(id)
                    }
                }
                CoroutineScope(Dispatchers.Main).launch {
                    onSubmit.invoke(it, ids.joinToString(","))
                }
            }
            api.submitConsents(consents)
        }

        fun submitConsent(
            consent: BaseConsentMessage,
            onSubmit: (result: AllowConsentResult, consentID: String) -> Unit
        ) {
            submitConsent(listOf(consent)) { result, consentIDs ->
                val consentMessage = consent as ConsentMessage
                result[consentMessage.id]?.let { result ->
                    CoroutineScope(Dispatchers.Main).launch {
                        onSubmit.invoke(result, consentIDs)
                    }
                }
            }
        }

        fun cleanEverything() {
            shared.removeValue(SaveKey.PushKey)
            shared.removeValue(SaveKey.LoginContactID)
            shared.removeValue(SaveKey.CustomerID)
            shared.removeValue(SaveKey.ContactID)
            shared.removeValue(SaveKey.AllowTracking)
            shared.loginContactID = null
            shared.custID = null
            shared.publicContactID = null
            shared.sessionID = null
            shared.sessionExpire = null
            shared.allowTracking = false
        }

        fun initialize(application: Application, enableLog: Boolean = false) {
            shared.allowTracking = shared.readBoolValue(SaveKey.AllowTracking) ?: false

            shared.isLogEnable = enableLog
            shared.app = application

            val config = application.packageManager.getApplicationInfo(
                application.packageName,
                PackageManager.GET_META_DATA
            )

            var pamServer = config.metaData.get("pam-server").toString()
            if (pamServer.endsWith("/")) {
                pamServer = pamServer.replaceFirst(".$", "")
            }

            config.metaData.get("pam-tracking-consent-message-id")

            val publicDBAlias = config.metaData.get("public-db-alias").toString()
            val loginDBAlias = config.metaData.get("login-db-alias").toString()
            val teckingConsentMessageID =
                config.metaData.get("pam-tracking-consent-message-id").toString()
            val trackingConsentInterval =
                config.metaData.get("pam-tracking-consent-message-interval").toString().toLong()

            shared.options = PamOption(
                pamServer = pamServer,
                publicDbAlias = publicDBAlias,
                loginDbAlias = loginDBAlias,
                trackingConsentMessageID = teckingConsentMessageID,
                trackingConsentInterval = trackingConsentInterval
            )

        }

        fun track(
            eventName: String,
            payload: Map<String, Any>? = null,
            trackerCallBack: TrackerCallback? = null
        ) = shared.track(
            eventName,
            payload,
            trackerCallBack
        )

        fun appReady() = shared.appReady()
        fun listen(eventName: String, callback: ListenerFunction) =
            shared.listen(eventName, callback)

        fun userLogout(callBack: (() -> Unit)? = null) = shared.userLogout(callBack)
        fun userLogin(customerID: String, callBack: (() -> Unit)? = null) =
            shared.userLogin(customerID, callBack)

        fun askNotificationPermission() = shared.askNotificationPermission()
        fun fetchNotificationHistory(callBack: (List<NotificationItem>?) -> Unit) =
            shared.fetchNotificationHistory(callBack)

        fun setPushNotificationToken(token: String) = shared.setPushNotificationToken(token)
    }

    enum class SaveKey(val keyName: String) {
        CustomerID("@pam_customer_id"),
        ContactID("@_pam_contact_id"),
        LoginContactID("@_pam_login_contact_id"),
        PushKey("@_pam_push_key"),
        AllowTracking("@_pam_allow_tracking")
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

    private var platformVersionCache: String? = null
    private var osVersionCache: String? = null
    private var appVersionCache: String? = null
    private var publicContactID: String? = null
    private var loginContactID: String? = null
    private var custID: String? = null

    var allowTracking = false
        set(value) {
            field = value
            saveValue(SaveKey.AllowTracking, value)
        }

    private var sharedPreferences: SharedPreferences? = null

    init {
        queueTrackerManager.onNext = { eventName, payload, trackerCallback ->
            postTracker(eventName, payload, trackerCallback)
        }
    }

    fun getCustomerID(): String? {
        if (custID != null) {
            return custID
        }
        custID = readValue(SaveKey.CustomerID)
        if (custID != null) {
            return custID
        }
        return null
    }

    fun getDatabaseAlias(): String? {
        return when (isUserLogin()) {
            true -> {
                if (isLogEnable) {
                    Log.d("PAM", "🪣🪣 : USE : 🍎DB-LOGIN")
                }
                options?.loginDbAlias
            }
            else -> {
                if (isLogEnable) {
                    Log.d("PAM", "🪣🪣 : USE : 🍊DB-PUBLIC")
                }
                options?.publicDbAlias
            }
        }
    }

    fun getContactID(): String? {
        if (loginContactID != null) loginContactID
        if (publicContactID != null) publicContactID

        readValue(SaveKey.LoginContactID)?.let {
            loginContactID = it
            return@getContactID it
        }

        readValue(SaveKey.ContactID)?.let {
            publicContactID = it
            return@getContactID it
        }

        return null
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
            setPushNotificationToken(task.result.toString())
        })
    }

    fun setPushNotificationToken(token: String){
        saveValue(SaveKey.PushKey, token)
        setDeviceToken(token)
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

    private fun saveValue(key: SaveKey, value: Boolean) {
        val sharedPref = getSharedPreference()
        val editor = sharedPref?.edit()
        editor?.putBoolean(key.keyName, value)
        editor?.apply()
    }

    private fun saveValue(key: SaveKey, value: String) {
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

    private fun readBoolValue(key: SaveKey): Boolean? {
        val sharedPref = getSharedPreference()
        return sharedPref?.getBoolean(key.keyName, false)
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

    fun fetchNotificationHistory(callBack: (List<NotificationItem>?) -> Unit) {
        if (custID == null && getContactID() == null) {
            callBack(null)
        }

        val url = "${options?.pamServer!!}/api/app-notifications"

        val query = mutableMapOf<String, String>(
            "_database" to (getDatabaseAlias() ?: "")
        )

        custID?.let {
            query["customer"] = it
        }

        getContactID()?.let {
            query["_contact_id"] = it
        }

        Http.getInstance().get(
            url = url,
            queryString = query
        ) { result, _ ->
            val model = Gson().fromJson(result, NotificationList::class.java)
            CoroutineScope(Dispatchers.Main).launch {
                callBack.invoke(model.items)
            }
        }
    }

    fun userLogin(customerID: String, callBack: (() -> Unit)? = null) {
        custID = customerID
        saveValue(SaveKey.CustomerID, customerID)
        track("login") {
            callBack?.invoke()
        }
    }

    fun userLogout(callBack: (() -> Unit)? = null) {
        val payload = mapOf(
            "_delete_media" to mapOf(
                "android_notification" to ""
            )
        )

        track("logout", payload) {
            this.custID = null
            this.loginContactID = null
            this.removeValue(SaveKey.CustomerID)
            this.removeValue(SaveKey.LoginContactID)
            callBack?.invoke()
        }

    }

    fun setDeviceToken(deviceToken: String) {
        track(
            "save_push", mapOf(
                "android_notification" to deviceToken
            )
        )
        dispatch("onToken", mapOf("token" to deviceToken))
    }

    fun track(
        eventName: String,
        payload: Map<String, Any>? = null,
        trackerCallback: TrackerCallback?
    ) {
        if (!allowTracking && (eventName != "save_push" && eventName != "allow_consent")) {
            if (isLogEnable) {
                Log.d("PAM", "🤡 NOTRACKING $eventName")
            }
            return
        }

        this.queueTrackerManager.enqueue(eventName, payload, trackerCallback)
    }

    private fun isUserLogin(): Boolean {
        if (custID == null) {
            custID = readValue(SaveKey.CustomerID)
        }
        return custID != null
    }

    private fun buildPayload(
        eventName: String,
        payload: Map<String, Any>? = null,
        trackingConsentMessageID: String? = null
    ): Map<String, Any> {

        if (platformVersionCache == null) {
            val pInfo = app?.packageManager?.getPackageInfo(app?.packageName ?: "", 0)
            val packageName = app?.packageName ?: ""
            appVersionCache = "$packageName (${pInfo?.versionName ?: ""})"
            osVersionCache = "Android: ${Build.VERSION.SDK_INT}"

            platformVersionCache =
                "Android: ${Build.VERSION.SDK_INT} (${Build.VERSION.RELEASE}), $packageName: $appVersionCache"
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

        trackingConsentMessageID?.let {
            formField["_consent_message_id"] = it
        }

        getContactID()?.let {
            formField["_contact_id"] = it
        }

        payload?.forEach {
            if (it.key != "page_url" && it.key != "page_title") {
                formField[it.key] = it.value
            } else {
                body[it.key] = it.value
            }
        }

        getDatabaseAlias()?.let {
            formField["_database"] = it
        }

        if (isUserLogin()) {
            getCustomerID()?.let {
                formField["customer"] = it
            }
        }

        body["form_fields"] = formField

        return body
    }

    private fun postTracker(
        eventName: String,
        payload: Map<String, Any>?,
        trackerCallBack: TrackerCallback? = null
    ) {
        val url = "${options?.pamServer!!}/trackers/events"

        val body = buildPayload(eventName, payload, options?.trackingConsentMessageID)

        if (isLogEnable) {
            Log.d("PAM", "🦄 : POST Event = 🍀$eventName🍀")
            Log.d("PAM", "🦄 : Payload")
            Log.d("PAM", "🍉🍉🍉🍉🍉🍉🍉🍉🍉🍉🍉🍉🍉\n$body\n🍉🍉🍉🍉🍉🍉🍉🍉🍉🍉🍉🍉🍉\n\n")
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

            try {
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

                val task = CoroutineScope(Dispatchers.Main)
                task.launch {
                    trackerCallBack?.invoke(response)
                    queueTrackerManager.next()
                }
            } catch (e: JsonSyntaxException) {

            }
        }
    }
}