package ai.pams.android.kotlin.models.consent.tracking.message

data class TrackingCollection(
    val google_tag_id: List<String>? = null,
    val marketing_script: List<String>? = null,
    val facebook_pixel_id: List<String>? = null
)