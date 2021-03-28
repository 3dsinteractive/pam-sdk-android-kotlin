package ai.pams.android.kotlin.models.consent.tracking.message

data class StyleConfiguration(
    val bar_background_color: String? = null,
    val bar_background_opacity_percentage: Int? = null,
    val bar_text_color: String? = null,
    val button_background_color: String? = null,
    val button_text_color: String? = null,
    val consent_detail: ConsentDetail? = null
)