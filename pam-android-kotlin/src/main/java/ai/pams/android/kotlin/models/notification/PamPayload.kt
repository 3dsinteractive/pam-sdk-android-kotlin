package ai.pams.android.kotlin.models.notification


import com.google.gson.annotations.SerializedName

data class PamPayload(
    @SerializedName("created_date")
    val createdDate: String? = null,
    @SerializedName("flex")
    val flex: String? = null,
    @SerializedName("pixel")
    val pixel: String? = null,
    @SerializedName("url")
    val url: String? = null
)