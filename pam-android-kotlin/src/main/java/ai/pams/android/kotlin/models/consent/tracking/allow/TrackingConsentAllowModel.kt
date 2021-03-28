package ai.pams.android.kotlin.models.consent.tracking.allow

data class TrackingConsentAllowModel(
    val code: String? = null,
    val message: String,
    val allow_analytics_cookies: Boolean? = null,
    val allow_marketing_cookies: Boolean? = null,
    val allow_necessary_cookies: Boolean? = null,
    val allow_preferences_cookies: Boolean? = null,
    val allow_privacy_overview: Boolean? = null,
    val allow_social_media_cookies: Boolean? = null,
    val allow_terms_and_conditions: Boolean? = null,
    val consent_message_id: String? = null,
    val consent_type: String? = null,
    val contact_id: String? = null,
    val show_consent_bar: Boolean? = null
)