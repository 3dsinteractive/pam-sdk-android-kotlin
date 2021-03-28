package ai.pams.android.kotlin.models.consent.contact


import com.google.gson.annotations.SerializedName

data class ContactConsentModel(
    @SerializedName("consent_message_id")
    val consentMessageId: String? = null,
    @SerializedName("consent_message_type")
    val consentMessageType: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("setting")
    val setting: Setting? = null,
    @SerializedName("style_configuration")
    val styleConfiguration: StyleConfiguration? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
)