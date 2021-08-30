package ai.pams.android.kotlin.models.consent.tracking.allow

import com.google.gson.annotations.SerializedName


data class TrackingPermission(
    @SerializedName("analytics_cookies") val analyticsCookies: Boolean? = null,
    @SerializedName("marketing_cookies") val marketingCookies: Boolean? = null,
    @SerializedName("necessary_cookies") val necessaryCookies: Boolean? = null,
    @SerializedName("preferences_cookies") val preferencesCookies: Boolean? = null,
    @SerializedName("privacy_overview") val privacyOverview: Boolean? = null,
    @SerializedName("social_media_cookies") val socialMediaCookies: Boolean? = null,
    @SerializedName("terms_and_conditions") val termsAndConditions: Boolean? = null,
)

data class ContactPermissions(val allow_something: Boolean? = null)

data class TrackingConsentUserPermissions(
    @SerializedName("consent_id") val consentId: String? = null,
    @SerializedName("consent_message_id") val consentMessageId: String? = null,
    @SerializedName("version") val version: Int? = null,
    @SerializedName("last_consent_version") val lastConsentVersion: Int? = null,
    @SerializedName("latest_version") val latestVersion: Int? = null,
    @SerializedName("last_consent_at") val lastConsentAt: String? = null,
    @SerializedName("need_consent_review") val needConsentReview: Boolean = false,
    @SerializedName("code") val code: String? = null,
    @SerializedName("message") val message: String,
    @SerializedName("tracking_permission") val trackingPermission: TrackingPermission? = null,
    @SerializedName("contacting_permission") val contactingPermission: ContactPermissions? = null,
    @SerializedName("consent_type") val consentType: String? = null,
    @SerializedName("contact_id") val contactId: String? = null,
    @SerializedName("show_consent_bar") val showConsentBar: Boolean? = null
)
