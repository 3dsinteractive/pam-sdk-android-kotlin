package ai.pams.android.kotlin.models.consent.tracking.message

data class MoreInfo(
    val custom_url: CustomUrl? = null,
    val display_text: DisplayText? = null,
    val is_custom_url_enabled: Boolean? = null
)