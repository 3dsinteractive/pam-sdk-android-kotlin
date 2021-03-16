package pamsdk

typealias QueueTrackerCallback = (String, Map<String, Any>?, Boolean) -> Unit

class TrackingQueue(val eventName:String, val payload: Map<String, Any>? = null, val deleteLoginContactAfterPost: Boolean)

class QueueTrackerManager() {
    private var isProcessing: Boolean = false
    private val queue = mutableListOf<TrackingQueue>()

    var callback: QueueTrackerCallback? = null

    fun enqueue(eventName: String, payload: Map<String, Any>? = null, deleteLoginContactAfterPost:Boolean) {
        val tracking = TrackingQueue(eventName, payload, deleteLoginContactAfterPost)
        this.queue.add(tracking)

        if (!this.isProcessing) {
            this.next()
        }
    }

    fun next() {
        if (queue.size > 0) {
            this.isProcessing = true
            val task = queue.removeFirst()
            callback?.invoke(task.eventName, task.payload, task.deleteLoginContactAfterPost)

        } else {
            this.isProcessing = false
        }
    }
}