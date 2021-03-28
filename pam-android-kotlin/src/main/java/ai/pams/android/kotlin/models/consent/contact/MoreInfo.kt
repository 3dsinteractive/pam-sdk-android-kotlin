package ai.pams.android.kotlin.models.consent.contact


import com.google.gson.annotations.SerializedName

data class MoreInfo(
    @SerializedName("custom_url")
    val customUrl: CustomUrl? = null,
    @SerializedName("display_text")
    val displayText: DisplayText? = null,
    @SerializedName("is_custom_url_enabled")
    val isCustomUrlEnabled: Boolean? = null
)