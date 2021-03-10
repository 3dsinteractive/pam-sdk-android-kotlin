package pamsdk

typealias QueueTrackerCallback = (String, Map<String, Any>) -> Unit

class QueueTrackerManager(val callback: QueueTrackerCallback? = null) {
    private var isProcessing: Boolean = false
    private val queue = mutableListOf<Map<String, Any>>()


    fun enqueue(eventName: String, payload: Map<String, Any>? = null) {
        this.queue.add(
            mapOf(
                "event_name" to eventName,
                "payload" to (payload ?: mapOf())
            )
        )

        if (!this.isProcessing) {
            this.next()
        }
    }

    fun next() {
        if (queue.size > 0) {
            this.isProcessing = true
            val task = queue.removeFirst()
            callback?.invoke(task["event_name"] as String, task["payload"] as Map<String, Any>)
        } else {
            this.isProcessing = false
        }
    }
}