package ai.pams.android.kotlin.queue

import ai.pams.android.kotlin.TrackerCallback
import android.util.Log

typealias QueueTrackerCallback = (String, Map<String, Any>?, TrackerCallback?) -> Unit

class TrackingQueue(val eventName:String, val payload: Map<String, Any>? = null, val trackerCallback: TrackerCallback? = null)

class QueueTrackerManager() {
    private var isProcessing: Boolean = false
    private val queue = mutableListOf<TrackingQueue>()

    var onNext: QueueTrackerCallback? = null

    fun enqueue(eventName: String, payload: Map<String, Any>? = null, trackerCallback:TrackerCallback? = null) {
        val tracking = TrackingQueue(eventName, payload, trackerCallback)
        this.queue.add(tracking)

        Log.d("PAM", "1.Track = $eventName   isProcessing=$isProcessing")
        if (!this.isProcessing) {
            Log.d("PAM", "2.Track = $eventName")
            this.next()
        }

    }

    fun next() {
        if (queue.size > 0) {
            this.isProcessing = true
            val task = queue.removeFirst()
            Log.d("PAM", "Queue = ${queue.size}")
            onNext?.invoke(task.eventName, task.payload, task.trackerCallback)
        } else {
            this.isProcessing = false

            Log.d("PAM", "Reset isProcessing to false : $this.isProcessing")
        }
    }
}