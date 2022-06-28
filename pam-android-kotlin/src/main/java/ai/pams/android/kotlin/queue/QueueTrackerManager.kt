package ai.pams.android.kotlin.queue

import ai.pams.android.kotlin.Pam
import ai.pams.android.kotlin.TrackerCallback
import android.util.Log

typealias QueueTrackerCallback = (String, Long, Map<String, Any>?, TrackerCallback?) -> Unit

class TrackingQueue(
    val eventName: String,
    val delayAfterPost: Long,
    val payload: Map<String, Any>? = null,
    val trackerCallback: TrackerCallback? = null
)

class QueueTrackerManager() {
    private var isProcessing: Boolean = false
    private val queue = mutableListOf<TrackingQueue>()

    var onNext: QueueTrackerCallback? = null

    fun enqueue(eventName: String, payload: Map<String, Any>? = null, delayAfterPost: Long = 0L, trackerCallback:TrackerCallback? = null) {
        val tracking = TrackingQueue(eventName, delayAfterPost, payload, trackerCallback)
        this.queue.add(tracking)
        if(Pam.shared.isLogEnable){
            Log.d("PAM", "1.Track = $eventName   isProcessing=$isProcessing")
        }
        if (!this.isProcessing) {
            if(Pam.shared.isLogEnable){
                Log.d("PAM", "2.Track = $eventName")
            }
            this.next()
        }

    }

    fun next() {
        if (queue.size > 0) {
            this.isProcessing = true
            val task = queue.removeFirst()
            if(Pam.shared.isLogEnable){
                Log.d("PAM", "Queue = ${queue.size}")
            }
            onNext?.invoke(task.eventName, task.delayAfterPost, task.payload, task.trackerCallback)
        } else {
            this.isProcessing = false
            if(Pam.shared.isLogEnable){
                Log.d("PAM", "Reset isProcessing to false : $this.isProcessing")
            }
        }
    }
}