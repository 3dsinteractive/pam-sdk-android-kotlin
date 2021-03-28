package ai.pams.android.kotlin

import ai.pams.android.kotlin.http.Http
import ai.pams.android.kotlin.models.consent.tracking.allow.ConsentModel
import ai.pams.android.kotlin.models.consent.tracking.message.TrackingConsentModel
import ai.pams.android.kotlin.views.ConsentRequestDialog
import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.gson.Gson

class TrackingConsentManager(val fragmentManager: FragmentManager, lifeCycle: Lifecycle) :
    LifecycleObserver {

    var consentMessageID: String? = null
    var pamServerURL: String? = null
    var consentMessage: TrackingConsentModel? = null
    var consentAllowModel: ConsentModel? = null

    init {
        lifeCycle.addObserver(this)
    }

    private fun loadConsentMessage() {
        consentMessageID = Pam.shared.options?.trackingConsentMessageID
        pamServerURL = Pam.shared.options?.pamServer
        Http.getInstance()
            .get("${pamServerURL ?: ""}/consent-message/$consentMessageID") { result, error ->
                if (error == null) {
                    consentMessage = Gson().fromJson(result, TrackingConsentModel::class.java)
                    checkConsentPermission()
                }
            }
    }

    private fun checkConsentPermission() {
        val contact = Pam.shared.getContactID()

        if (contact == null) {
            showConsentRequestPopup()
        } else {
            Http.getInstance()
                .get("${pamServerURL ?: ""}/contacts/$contact/consents/$consentMessageID") { result, error ->
                    if (error == null) {
                        consentAllowModel =
                            Gson().fromJson(result, ConsentModel::class.java)

                        //Allow Tracking if preferencesCookies is allowed
                        Pam.shared.allowTracking = consentAllowModel?.trackingPermission?.preferencesCookies == true

                        if (consentAllowModel?.code == "NOT_FOUND" || consentAllowModel?.needConsentReview == true) {
                            showConsentRequestPopup()
                        }
                    }
                }
        }
    }

    private fun saveConsent(consentAllow: Map<String, Boolean>) {
        val payload = mutableMapOf<String, Any>(
            "_consent_message_id" to (consentMessageID ?: ""),
        )

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
            Pam.shared.allowTracking = true
        }
    }

    private fun showConsentRequestPopup() {
        val dialog = ConsentRequestDialog(consentMessage, consentAllowModel)
        dialog.isCancelable = false
        dialog.show(fragmentManager, "consent_request")
        dialog.onAccept = {
            saveConsent(it)
        }
        consentMessage
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun resume() {
        loadConsentMessage()
    }

}