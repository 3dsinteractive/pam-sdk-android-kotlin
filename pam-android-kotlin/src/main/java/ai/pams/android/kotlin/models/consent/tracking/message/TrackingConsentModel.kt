package ai.pams.android.kotlin.models.consent.tracking.message

data class TrackingConsentModel(
    val code: String? = null,
    val message: String,
    val consent_message_id: String? = null,
    val consent_message_type: String? = null,
    val created_at: String? = null,
    val description: String? = null,
    val name: String? = null,
    val setting: Setting? = null,
    val style_configuration: StyleConfiguration? = null,
    val updated_at: String? = null
)