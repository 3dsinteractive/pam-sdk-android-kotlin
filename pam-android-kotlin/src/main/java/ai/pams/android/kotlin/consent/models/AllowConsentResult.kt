package ai.pams.android.kotlin.consent.models

import org.json.JSONObject

data class AllowConsentResult(val contactID: String?, val database: String?, val consentID: String?){
    companion object{
        fun parse(json: JSONObject): AllowConsentResult {
            val contactID = json.optString("contact_id")
            val database = json.optString("_database")
            val consentID = json.optString("consent_id")
            return AllowConsentResult(
                contactID = contactID,
                database = database,
                consentID = consentID
            )
        }
    }
}