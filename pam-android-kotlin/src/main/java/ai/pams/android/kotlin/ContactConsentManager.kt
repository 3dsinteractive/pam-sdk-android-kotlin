package ai.pams.android.kotlin

import ai.pams.android.kotlin.dialogs.ContactConsentRequestDialog
import ai.pams.android.kotlin.http.Http
import ai.pams.android.kotlin.models.consent.contact.ContactConsentModel
import androidx.fragment.app.FragmentManager
import com.google.gson.Gson

typealias OnStatusChanged = (Map<String, Boolean>)->Unit

class ContactConsentManager(private val consentMessageID:String, private val fragmentManager: FragmentManager) {

    private var onReady: (()->Unit)? = null
    var ready = false
    var consentMessage: ContactConsentModel? = null

    private var onStatusChanged: OnStatusChanged? = null

    fun setOnReadyListener(onReady: ()->Unit ){
        this.onReady = onReady
        if(ready){
            onStatusChanged?.invoke(createAllowMap())
            onReady.invoke()
        }
    }

    fun setOnStatusChangedListener(onStatusChanged: OnStatusChanged ){
        this.onStatusChanged = onStatusChanged
    }

    init{
        loadConsentMessage()
    }

    private fun loadConsentMessage() {
        val pamServerURL = Pam.shared.options?.pamServer
        Http.getInstance()
            .get("${pamServerURL ?: ""}/consent-message/$consentMessageID"
            ) { result, error ->
                if (error == null) {
                    consentMessage = Gson().fromJson(result, ContactConsentModel::class.java)
                    ready = true
                    onReady?.invoke()
                    //onStatusChanged?.invoke(createAllowMap())
                }
            }
    }

    fun openConsentRequestDialog(){
        consentMessage?.let{
            it.setting?.privacyOverview?.is_allow = true
            it.setting?.termsAndConditions?.is_allow = true

            val dialog = ContactConsentRequestDialog(it)
            dialog.isCancelable = false
            dialog.onAccept = {
                onStatusChanged?.invoke(createAllowMap())
            }
            dialog.show(fragmentManager, "contact_consent_request")
        }
    }

    private fun createAllowMap(): Map<String, Boolean>{
        val allowMap = mutableMapOf<String, Boolean>()

        consentMessage?.setting?.termsAndConditions?.let{
            if(it.is_enabled == true){
                allowMap["terms_and_conditions"] = it.is_allow
            }
        }

        consentMessage?.setting?.privacyOverview?.let{
            if(it.is_enabled == true){
                allowMap["privacy_overview"] = it.is_allow
            }
        }

        consentMessage?.setting?.email?.let{
            if(it.is_enabled == true){
                allowMap["email"] = it.is_allow
            }
        }

        consentMessage?.setting?.sms?.let{
            if(it.is_enabled == true){
                allowMap["sms"] = it.is_allow
            }
        }

        consentMessage?.setting?.line?.let{
            if(it.is_enabled == true){
                allowMap["line"] = it.is_allow
            }
        }


        consentMessage?.setting?.facebookMessenger?.let{
            if(it.is_enabled == true){
                allowMap["facebook_messenger"] = it.is_allow
            }
        }

        consentMessage?.setting?.pushNotification?.let{
            if(it.is_enabled == true){
                allowMap["push_notification"] = it.is_allow
            }
        }

        return allowMap
    }

    fun setAcceptAllPermissions(isAccept: Boolean){
        if(!ready){return}

        consentMessage?.setting?.termsAndConditions?.is_allow = isAccept
        consentMessage?.setting?.privacyOverview?.is_allow = isAccept

        consentMessage?.setting?.email?.let{
            it.is_allow = isAccept
        }

        consentMessage?.setting?.sms?.let{
            it.is_allow = isAccept
        }

        consentMessage?.setting?.line?.let{
            it.is_allow = isAccept
        }

        consentMessage?.setting?.facebookMessenger?.let{
            it.is_allow = isAccept
        }

        consentMessage?.setting?.pushNotification?.let{
            it.is_allow = isAccept
        }

        onStatusChanged?.invoke(createAllowMap())
    }

    fun applyConsent(callBack:(PamResponse)->Unit){
        val payload = mutableMapOf<String, Any>(
            "_consent_message_id" to (consentMessage?.consentMessageId ?: ""),
        )

        consentMessage?.setting?.version?.let{
            payload["_version"] = it
        }

        Pam.shared.getDatabaseAlias()?.let{
            payload["_database"] = it
        }

        consentMessage?.setting?.termsAndConditions?.let{
            payload["_allow_terms_and_conditions"] = it.is_allow
        }

        consentMessage?.setting?.privacyOverview?.let{
            payload["_allow_privacy_overview"] = it.is_allow
        }

        consentMessage?.setting?.email?.let{
            payload["_allow_email"] = it.is_allow
        }

        consentMessage?.setting?.sms?.let{
            payload["_allow_sms"] = it.is_allow
        }

        consentMessage?.setting?.line?.let{
            payload["_allow_line"] = it.is_allow
        }

        consentMessage?.setting?.facebookMessenger?.let{
            payload["_allow_facebook_messenger"] = it.is_allow
        }

        consentMessage?.setting?.pushNotification?.let{
            payload["_allow_push_notification"] = it.is_allow
        }

        Pam.track("allow_consent", payload){
            callBack.invoke(it)
        }

    }

}