package ai.pams.android.kotlin

class PamStandardEvent {
    companion object{
        fun pageView(pageName:String,  pageURL:String? = null, contentID: String? = null, payload: Map<String,Any>? = null) {
            val newPayload = mutableMapOf<String,Any>(
                "page_title" to pageName
            )
            pageURL?.let{
                newPayload["page_url"] = it
            }
            contentID?.let {
                newPayload["id"] = it
            }

            payload?.forEach{ item->
                newPayload[item.key] = item.value
            }

            Pam.track("page_view", newPayload)
        }
    }

}