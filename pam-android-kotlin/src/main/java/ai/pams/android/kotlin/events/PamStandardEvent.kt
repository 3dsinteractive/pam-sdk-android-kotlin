package ai.pams.android.kotlin.events

import ai.pams.android.kotlin.Pam


typealias Payload = Map<String, Any>

open class PamEvent(private val eventName:String){
    fun track(){
        Pam.track(eventName, buildPayload())
    }
    open fun buildPayload(): Map<String, Any>? {
        return null
    }
}

class PamStandardEvent {
    class PageView(private val pageTitle:String, private val pageURL:String, private val payload:Payload? = null ): PamEvent("page_view"){
        override fun buildPayload(): Map<String, Any> {
            val eventPayload = mutableMapOf<String, Any>(
                "page_title" to pageTitle,
                "page_url" to pageURL,
            )
            for((k,v) in payload ?: mapOf() ){
                eventPayload[k] = v
            }
            return eventPayload
        }
    }
}