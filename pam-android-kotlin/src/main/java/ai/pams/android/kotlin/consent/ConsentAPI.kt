package ai.pams.android.kotlin.consent

import ai.pams.android.kotlin.Pam
import ai.pams.android.kotlin.http.Http
import org.json.JSONObject

typealias OnLoadConsentMessage = (messages: Map<String, BaseConsentMessage>)->Unit
typealias OnSubmitConsent = (messages: Map<String, AllowConsentResult>)->Unit

class ConsentAPI {
    private var consentMessagesIdQueue: List<String>? = null
    private var isLoading = false
    private var index = 0

    private var resultMessages: MutableMap<String, BaseConsentMessage>? =null
    private var _onConsentLoadCallBack: OnLoadConsentMessage? = null

    private var submitConsentQueue: List<BaseConsentMessage>? = null
    private var resultSubmit: MutableMap<String, AllowConsentResult>? = null
    private var _onConsentSubmitCallBack: OnSubmitConsent? = null


    fun setOnConsentLoaded(callBack: OnLoadConsentMessage){
        _onConsentLoadCallBack = callBack
    }

    fun setOnConsentSubmit(callBack: OnSubmitConsent){
        _onConsentSubmitCallBack = callBack
    }

    fun loadConsent(consentMessageID: List<String>) {
        if (!isLoading) {
            consentMessagesIdQueue = consentMessageID
            index = 0
            resultMessages = mutableMapOf()
            startLoad()
        }
    }

    fun submitConsents(consents: List<BaseConsentMessage>){
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
                _onConsentSubmitCallBack?.invoke(it)
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
            payload[it.getSubmitKey()] = it.accept
            Pam.shared.options?.trackingConsentMessageID?.let{ trackingConsentMessageID ->
                if(consent.id == trackingConsentMessageID && it.getSubmitKey() == "_allow_preferences_cookies"){
                    Pam.shared.allowTracking = it.accept
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

    private fun startLoad(){
        if(index == (consentMessagesIdQueue?.size ?: 0) ){
            resultMessages?.toMap()?.let{
                _onConsentLoadCallBack?.invoke(it)
            }
            isLoading = false
            return
        }

        val consentMessageID = consentMessagesIdQueue?.get(index) ?: ""

        index++
        if(consentMessageID == ""){
            startLoad()
            return
        }

        isLoading = true
        consentMessagesIdQueue
        val pamServerURL = Pam.shared.options?.pamServer ?: ""

        Http.getInstance()
            .get("${pamServerURL ?: ""}/consent-message/$consentMessageID") { result, error ->
                if (error == null) {
                    if(result != null) {
                        val json = JSONObject(result)
                        when(json.optString("consent_message_type")){
                            "tracking_type"->parseConsentMessage(json, ConsentType.Tracking)
                            "contacting_type"->parseConsentMessage(json, ConsentType.Contacting)
                            else->parseErrorConsent(consentMessageID, json)
                        }
                    }else{
                        val json = JSONObject("{\"code\": \"SERVER_EMPTY_RESPONSE\",\"message\":\"Empty Response From Server.\"}")
                        parseErrorConsent(consentMessageID, json)
                    }
                }else{
                    val msg = error.localizedMessage
                    val json = JSONObject("{\"code\": \"NETWORK_REQUEST_ERROR\",\"message\":\"${msg}\"}")
                    parseErrorConsent(consentMessageID, json)
                }

                startLoad()
            }
    }

    private fun parseErrorConsent(id:String, json:JSONObject){
        val errorMessage = json.optString("message")
        val errorCode = json.optString("code")
        val msg = ConsentMessageError(
            errorMessage = errorMessage,
            errorCode = errorCode
        )
        resultMessages?.put(id, msg)
    }

    private fun getText(json:JSONObject?): Text?{
        if(json == null ) return null
        return Text(
            en = json.optString("en"),
            th = json.optString("th"),
        )
    }

    private fun parseConsentMessage(json:JSONObject, type: ConsentType){
        val id = json.optString("consent_message_id")
        val name = json.optString("name")
        val description = json.optString("description")
        val style = StyleConfiguration.parse(json.optJSONObject("style_configuration"))
        val setting = json.optJSONObject("setting") ?: JSONObject()
        val version = setting.optInt("version")
        val revision = setting.optInt("version")
        val displayText = getText(setting.optJSONObject("display_text"))
        val acceptButtonText = getText(setting.optJSONObject("accept_button_text"))
        val consentDetailTitle = getText(setting.optJSONObject("consent_detail_title"))

        val availableLanguages = mutableListOf<String>()
        setting.optJSONArray("available_languages")?.let{
            for(i in 0 until it.length()){
                availableLanguages.add(it.getString(i))
            }
        }

        val defaultLanguage = setting.optString("default_language")
        val permissions = ConsentPermission.parse(setting)

        val msg = ConsentMessage(
            id = id,
            type = ConsentType.Tracking,
            name = name,
            description = description,
            style = style,
            version = version,
            revision = revision,
            displayText = displayText,
            acceptButtonText = acceptButtonText,
            consentDetailTitle = consentDetailTitle,
            availableLanguages = availableLanguages.toList(),
            defaultLanguage = defaultLanguage,
            permission = permissions
        )

        resultMessages?.put(id, msg)
    }

}