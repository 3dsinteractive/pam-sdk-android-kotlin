package ai.pams.android.kotlin.models.notification


import com.google.gson.annotations.SerializedName

data class Payload(
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("pam")
    val pam: PamPayload? = null,
    @SerializedName("title")
    val title: String? = null
)