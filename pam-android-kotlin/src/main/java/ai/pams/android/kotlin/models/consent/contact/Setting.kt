package ai.pams.android.kotlin.models.consent.contact


import ai.pams.android.kotlin.models.consent.tracking.message.ConsentOption
import com.google.gson.annotations.SerializedName

data class Setting(
    @SerializedName("accept_button_text")
    val acceptButtonText: AcceptButtonText? = null,
    @SerializedName("available_languages")
    val availableLanguages: List<String>? = null,
    @SerializedName("consent_detail_title")
    val consentDetailTitle: ConsentDetailTitle? = null,
    @SerializedName("default_language")
    val defaultLanguage: String? = null,
    @SerializedName("display_text")
    val displayText: DisplayText? = null,
    @SerializedName("email")
    val email: ConsentOption? = null,
    @SerializedName("facebook_messenger")
    val facebookMessenger: ConsentOption? = null,
    @SerializedName("line")
    val line: ConsentOption? = null,
    @SerializedName("more_info")
    val moreInfo: MoreInfo? = null,
    @SerializedName("privacy_overview")
    val privacyOverview: ConsentOption? = null,
    @SerializedName("revision")
    val revision: Int? = null,
    @SerializedName("sms")
    val sms: ConsentOption? = null,
    @SerializedName("terms_and_conditions")
    val termsAndConditions: ConsentOption? = null,
    @SerializedName("push_notification")
    val pushNotification: ConsentOption? = null,
    @SerializedName("version")
    val version: Int? = null
)