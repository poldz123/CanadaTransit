package com.rodolfonavalon.canadatransit.controller.util.queue

import com.rodolfonavalon.canadatransit.controller.manager.transfer.TransferManager
import com.rodolfonavalon.canadatransit.controller.manager.update.UpdateManager

interface Action {
    /**
     * This is a unique id that tracks the actions within the [QueueTask], the ID corresponds to
     * the [Task] within the queue. It can be used in multiple areas such as:
     *
     * - The tracking id can be used as the file name of the transferred file. This serves as
     *   the tracking id for the [TransferManager] which tracks the currently being transferred
     *   within the manager.
     *
     * - The tracking id can also be used as a way to track the current object being update within
     *   the [UpdateManager]
     *
     * @return The id of the entity
     */
    fun trackingId(): String
}
