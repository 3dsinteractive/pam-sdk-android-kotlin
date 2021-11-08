package ai.pams.android.kotlin

import ai.pams.android.kotlin.dialogs.TrackingConsentRequestDialog
import ai.pams.android.kotlin.http.Http
import ai.pams.android.kotlin.models.consent.tracking.allow.TrackingConsentUserPermissions
import ai.pams.android.kotlin.models.consent.tracking.message.TrackingConsentMessageConfigurations
import androidx.fragment.app.FragmentManager
import com.google.gson.Gson

typealias OnUserConsentChangedListener = (consentID: String?, allow: Map<String,Boolean>?)->Unit
typealias OnConsentLoadedListener = (trackingConsentMessageConfigurations:TrackingConsentMessageConfigurations, trackingConsent: TrackingConsent, requireUserReview: Boolean)->Unit

data class TrackingConsent(
    val consentID: String? = null
    ){

    companion object{
        fun fromMap(consentID: String, map: Map<String, Boolean>): TrackingConsent{
            return TrackingConsent(consentID).apply {
                analyticsCookie = map["_allow_analytics_cookies"] ?: false
                marketingCookies = map["_allow_marketing_cookies"] ?: false
                necessaryCookies = map["_allow_necessary_cookies"] ?: false
                preferencesCookies = map["_allow_preferences_cookies"] ?: false
                privacyOverview = map["_allow_privacy_overview"] ?: false
                socialMediaCookies = map["_allow_social_media_cookies"] ?: false
                termsAndConditions = map["_allow_terms_and_conditions"] ?: false
            }
        }
    }

    var analyticsCookie = false
    var marketingCookies = false
    var necessaryCookies = false
    var preferencesCookies = false
    var privacyOverview = false
    var socialMediaCookies = false
    var termsAndConditions = false

    fun toMap(): Map<String, Boolean>{
        return mapOf(
            "_allow_analytics_cookies" to analyticsCookie,
            "_allow_marketing_cookies" to marketingCookies,
            "_allow_necessary_cookies" to necessaryCookies,
            "_allow_preferences_cookies" to preferencesCookies,
            "_allow_privacy_overview" to privacyOverview,
            "_allow_social_media_cookies" to socialMediaCookies,
            "_allow_terms_and_conditions" to termsAndConditions
        )
    }
}

class TrackingConsentManager{

    private var consentMessageID: String? = null
    private var pamServerURL: String? = null
    private var trackingConsentMessageConfigurations: TrackingConsentMessageConfigurations? = null
    private var trackingConsentUserPermissions: TrackingConsentUserPermissions? = null
    private var _onAcceptConsent: OnUserConsentChangedListener? = null
    private var _onLoad: OnConsentLoadedListener? = null

    fun setOnUserConsentChangedListener(listener: OnUserConsentChangedListener){
        _onAcceptConsent = listener
    }

    fun setOnConsentLoadedListener(listener: OnConsentLoadedListener){
        _onLoad = listener
    }

    fun load() {
        consentMessageID = Pam.shared.options?.trackingConsentMessageID
        pamServerURL = Pam.shared.options?.pamServer
        Http.getInstance()
            .get("${pamServerURL ?: ""}/consent-message/$consentMessageID") { result, error ->
                if (error == null) {
                    trackingConsentMessageConfigurations = Gson().fromJson(result, TrackingConsentMessageConfigurations::class.java)
                    checkConsentPermission()
                }
            }
    }

    private fun checkConsentPermission() {
        val contact = Pam.shared.getContactID()

        if (contact == null) {
            trackingConsentMessageConfigurations?.let{
               // _onLoad?.invoke(it, true)
            }
        } else {
            Http.getInstance()
                .get("${pamServerURL ?: ""}/contacts/$contact/consents/$consentMessageID") { result, error ->
                    if (error == null) {
                        trackingConsentUserPermissions =
                            Gson().fromJson(result, TrackingConsentUserPermissions::class.java)

                        //Allow Tracking if preferencesCookies is allowed
                        Pam.shared.allowTracking = trackingConsentUserPermissions?.trackingPermission?.preferencesCookies == true

                        if (trackingConsentUserPermissions?.code == "NOT_FOUND" || trackingConsentUserPermissions?.needConsentReview == true) {
                            trackingConsentMessageConfigurations?.let{
                                //_onLoad?.invoke(it, true)
                            }
                        }else{
                            val trackingConsent = TrackingConsent(trackingConsentUserPermissions?.consentId)
                            trackingConsent.analyticsCookie = trackingConsentUserPermissions?.trackingPermission?.analyticsCookies ?: false
                            trackingConsent.marketingCookies = trackingConsentUserPermissions?.trackingPermission?.marketingCookies ?: false
                            trackingConsent.necessaryCookies = trackingConsentUserPermissions?.trackingPermission?.necessaryCookies ?: false
                            trackingConsent.preferencesCookies = trackingConsentUserPermissions?.trackingPermission?.preferencesCookies ?: false
                            trackingConsent.privacyOverview = trackingConsentUserPermissions?.trackingPermission?.privacyOverview ?: false
                            trackingConsent.socialMediaCookies = trackingConsentUserPermissions?.trackingPermission?.socialMediaCookies ?: false
                            trackingConsent.termsAndConditions = trackingConsentUserPermissions?.trackingPermission?.termsAndConditions ?: false

                            trackingConsentMessageConfigurations?.let{
                               // _onLoad?.invoke(it, false)
                            }
                        }
                    }
                }
        }
    }

    private fun saveConsent(consentAllow: Map<String, Boolean>) {
        val payload = mutableMapOf<String, Any>(
            "_consent_message_id" to (consentMessageID ?: ""),
        )

        trackingConsentMessageConfigurations?.setting?.version?.let{
            payload["_version"] = it
        }

        Pam.shared.getDatabaseAlias()?.let{
            payload["_database"] = it
        }

        consentAllow["_allow_terms_and_conditions"]?.let {
            payload["_allow_terms_and_conditions"] = it
        }

        consentAllow["_allow_privacy_overview"]?.let {
            payload["_allow_privacy_overview"] = it
        }

        consentAllow["_allow_necessary_cookies"]?.let {
            payload["_allow_necessary_cookies"] = it
        }

        consentAllow["_allow_preferences_cookies"]?.let {
            Pam.shared.allowTracking = it
            payload["_allow_preferences_cookies"] = it
        }

        consentAllow["_allow_analytics_cookies"]?.let {
            payload["_allow_analytics_cookies"] = it
        }

        consentAllow["_allow_marketing_cookies"]?.let {
            payload["_allow_marketing_cookies"] = it
        }

        consentAllow["_allow_social_media_cookies"]?.let {
            payload["_allow_social_media_cookies"] = it
        }

        Pam.track("allow_consent", payload){
            _onAcceptConsent?.invoke(it.consentID, consentAllow)
        }
    }

    private fun showConsentRequestPopup(fragmentManager: FragmentManager) {
        val dialog = TrackingConsentRequestDialog(trackingConsentMessageConfigurations, trackingConsentUserPermissions)
        dialog.isCancelable = false
        dialog.show(fragmentManager, "tracking_consent_request")
        dialog.onAccept = {
            saveConsent(it)
        }
    }

}