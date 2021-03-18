package ai.pams.android.kotlin.queue

import android.util.Log

typealias QueueTrackerCallback = (String, Map<String, Any>?, Boolean) -> Unit

class TrackingQueue(val eventName:String, val payload: Map<String, Any>? = null, val deleteLoginContactAfterPost: Boolean)

class QueueTrackerManager() {
    private var isProcessing: Boolean = false
    private val queue = mutableListOf<TrackingQueue>()

    var callback: QueueTrackerCallback? = null

    fun enqueue(eventName: String, payload: Map<String, Any>? = null, deleteLoginContactAfterPost:Boolean) {
        val tracking = TrackingQueue(eventName, payload, deleteLoginContactAfterPost)
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
            callback?.invoke(task.eventName, task.payload, task.deleteLoginContactAfterPost)
        } else {
            this.isProcessing = false

            Log.d("PAM", "Reset isProcessing to false : $this.isProcessing")
        }
    }
}