package ai.pams.android.kotlin.models.consent.contact


import com.google.gson.annotations.SerializedName

data class StyleConfiguration(
    @SerializedName("bar_background_color")
    val barBackgroundColor: String? = null,
    @SerializedName("bar_background_opacity_percentage")
    val barBackgroundOpacityPercentage: Int? = null,
    @SerializedName("bar_text_color")
    val barTextColor: String? = null,
    @SerializedName("button_background_color")
    val buttonBackgroundColor: String? = null,
    @SerializedName("button_text_color")
    val buttonTextColor: String? = null,
    @SerializedName("consent_detail")
    val consentDetail: ConsentDetail? = null
)