package ai.pams.android.kotlin.consent

import ai.pams.android.kotlin.Pam
import ai.pams.android.kotlin.http.Http
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

typealias OnLoadConsentMessage = (messages: Map<String, BaseConsentMessage>)->Unit
typealias OnSubmitConsent = (messages: Map<String, AllowConsentResult>)->Unit
typealias OnLoadPermission = (messages: Map<String, UserConsentPermissions>)->Unit

class ConsentAPI {
    private var consentMessagesIdQueue: List<String>? = null
    private var isLoading = false
    private var index = 0

    private var resultMessages: MutableMap<String, BaseConsentMessage>? =null
    private var _onConsentLoadCallBack: OnLoadConsentMessage? = null

    private var submitConsentQueue: List<BaseConsentMessage?>? = null
    private var resultSubmit: MutableMap<String, AllowConsentResult>? = null
    private var _onConsentSubmitCallBack: OnSubmitConsent? = null

    private var resultUserConsentLoad: MutableMap<String, UserConsentPermissions>? = null
    private var _onPermissionLoadCallBack: OnLoadPermission? = null

    fun setOnPermissionLoadCallBack(callBack: OnLoadPermission){
        _onPermissionLoadCallBack = callBack
    }

    fun setOnConsentLoaded(callBack: OnLoadConsentMessage){
        _onConsentLoadCallBack = callBack
    }

    fun setOnConsentSubmit(callBack: OnSubmitConsent){
        _onConsentSubmitCallBack = callBack
    }

    fun loadConsent(consentMessageIDs: List<String>) {
        if (!isLoading) {
            consentMessagesIdQueue = consentMessageIDs
            index = 0
            resultMessages = mutableMapOf()
            startLoadConsentMessage()
        }
    }

    fun loadConsentPermissions(consentMessageIDs: List<String>){
        if (!isLoading) {
            consentMessagesIdQueue = consentMessageIDs
            index = 0
            resultUserConsentLoad = mutableMapOf()
            startLoadPermissions()
        }
    }

    fun submitConsents(consents: List<ConsentMessage?>){
        if (!isLoading) {
            submitConsentQueue = consents
            index = 0
            resultSubmit = mutableMapOf()
            startSubmit()
        }
    }

    private fun startSubmit(){
        if(index == (submitConsentQueue?.size ?: 0) ){
            resultSubmit?.toMap()?.let{
                CoroutineScope(Dispatchers.Main).launch {
                    _onConsentSubmitCallBack?.invoke(it)
                }
            }
            isLoading = false
            return
        }

        val submitConsent = submitConsentQueue?.get(index)
        index++
        if(submitConsent == null || submitConsent is ConsentMessageError){
            startSubmit()
            return
        }
        isLoading = true

        val consent = submitConsent as ConsentMessage
        val payload = mutableMapOf<String, Any>(
            "_consent_message_id" to consent.id,
        )
        payload["_version"] = consent.version

        consent.permission.forEach{
            payload[it.getSubmitKey()] = it.allow
            Pam.shared.options?.trackingConsentMessageID?.let{ trackingConsentMessageID ->
                if(consent.id == trackingConsentMessageID && it.getSubmitKey() == "_allow_preferences_cookies"){
                    Pam.shared.allowTracking = it.allow
                }
            }
        }

        Pam.track("allow_consent", payload){
            //_onAcceptConsent?.invoke(it.consentID, consentAllow)
            val result = AllowConsentResult(
                consentID = it.consentID,
                contactID = it.contactID,
                database = it.database
            )
            resultSubmit?.put(key = consent.id, value=result)
            startSubmit()
        }
    }

    private fun startLoadPermissions(){
        if(index == (consentMessagesIdQueue?.size ?: 0) ){
            resultUserConsentLoad?.toMap()?.let{
                CoroutineScope(Dispatchers.Main).launch {
                    _onPermissionLoadCallBack?.invoke(it)
                }
            }
            isLoading = false
            return
        }

        val consentMessageID = consentMessagesIdQueue?.get(index) ?: ""
        Log.d("PAMHTTP","consentMessageID=$consentMessageID" )

        index++
        if(consentMessageID == ""){
            startLoadPermissions()
            return
        }

        isLoading = true
        val pamServerURL = Pam.shared.options?.pamServer ?: ""
        val contactID = Pam.shared.getContactID() ?: ""

        Log.d("PAMHTTP","${pamServerURL}/contacts/$contactID/consents/$consentMessageID" )

        Http.getInstance()
            .get("${pamServerURL}/contacts/$contactID/consents/$consentMessageID") { result, error ->
                Log.d("PAMHTTP","$result" )
                if (error == null) {
                    if(result != null) {
                        val json = JSONObject(result)
                        val userConsent = UserConsentPermissions.parse(json)
                        resultUserConsentLoad?.put(consentMessageID, userConsent)
                    }
                }
                startLoadPermissions()
            }
    }

    private fun startLoadConsentMessage(){
        if(index == (consentMessagesIdQueue?.size ?: 0) ){
            resultMessages?.toMap()?.let{
                CoroutineScope(Dispatchers.Main).launch {
                    _onConsentLoadCallBack?.invoke(it)
                }
            }
            isLoading = false
            return
        }

        val consentMessageID = consentMessagesIdQueue?.get(index) ?: ""

        index++
        if(consentMessageID == ""){
            startLoadConsentMessage()
            return
        }

        isLoading = true
        val pamServerURL = Pam.shared.options?.pamServer ?: ""

        Http.getInstance()
            .get("${pamServerURL}/consent-message/$consentMessageID") { result, error ->
                if (error == null) {
                    if(result != null) {
                        val json = JSONObject(result)
                        val consentMessage = ConsentMessage.parse(json)
                        resultMessages?.put(consentMessageID, consentMessage)
                    }else{
                        val msg = ConsentMessageError(
                            errorMessage = "Empty Response From Server.",
                            errorCode = "SERVER_EMPTY_RESPONSE"
                        )
                        resultMessages?.put(consentMessageID, msg)
                    }
                }else{
                    val msg = ConsentMessageError(
                        errorMessage = error.localizedMessage ?: "",
                        errorCode = "NETWORK_REQUEST_ERROR"
                    )
                    resultMessages?.put(consentMessageID, msg)
                }

                startLoadConsentMessage()
            }
    }

}