package ai.pams.android.kotlin.models.consent.tracking.message

data class ConsentOption(
    val brief_description: BriefDescription? = null,
    val full_description: FullDescription? = null,
    val is_enabled: Boolean? = null,
    val is_full_description_enabled: Boolean? = null,
    val tracking_collection: TrackingCollection? = null,
    var is_expanded: Boolean = false,
    var is_allow: Boolean = true,
    var require:Boolean = false,
    var title:String = ""
)