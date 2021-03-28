package ai.pams.android.kotlin.models.consent.tracking.message

data class Setting(
    val accept_button_text: AcceptButtonText? = null,
    val analytics_cookies: ConsentOption? = null,
    val available_languages: List<String>? = null,
    val consent_detail_title: ConsentDetailTitle? = null,
    val default_language: String? = null,
    val display_text: DisplayText? = null,
    val email: ConsentOption? = null,
    val facebook_messenger: ConsentOption? = null,
    val line: ConsentOption? = null,
    val marketing_cookies: ConsentOption? = null,
    val more_info: MoreInfo? = null,
    val necessary_cookies: ConsentOption? = null,
    val preferences_cookies: ConsentOption? = null,
    val privacy_overview: ConsentOption? = null,
    val revision: Int? = null,
    val sms: ConsentOption? = null,
    val social_media_cookies: ConsentOption? = null,
    val terms_and_conditions: ConsentOption? = null,
    val version: Int? = null
)