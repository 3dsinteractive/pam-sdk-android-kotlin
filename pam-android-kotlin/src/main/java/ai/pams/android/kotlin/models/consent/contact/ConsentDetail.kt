package ai.pams.android.kotlin.models.consent.contact


import com.google.gson.annotations.SerializedName

data class ConsentDetail(
    @SerializedName("button_text_color")
    val buttonTextColor: String? = null,
    @SerializedName("popup_main_icon")
    val popupMainIcon: String? = null,
    @SerializedName("primary_color")
    val primaryColor: String? = null,
    @SerializedName("secondary_color")
    val secondaryColor: String? = null,
    @SerializedName("text_color")
    val textColor: String? = null
)