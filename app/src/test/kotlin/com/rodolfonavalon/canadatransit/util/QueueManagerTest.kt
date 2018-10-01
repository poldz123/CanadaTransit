package com.rodolfonavalon.canadatransit.util

import com.rodolfonavalon.canadatransit.controller.util.queue.AbstractQueueManager
import com.rodolfonavalon.canadatransit.controller.util.queue.QueueManagerListener
import com.rodolfonavalon.canadatransit.controller.util.queue.task.Task
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.*

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class QueueManagerTest {

    @Test
    fun testAdd_singleTask() {
        val queueTask = TestQueueManager()

        val testTask = createSingleTask(queueTask, true)
        assertEquals(1, queueTask.numTasks())

        // If task is added again the number of tasks should
        // not change or incremented.
        for (i in 1..20) {
            queueTask.add(testTask.trackingId, testTask)
        }
        assertEquals(1, queueTask.numTasks())
    }

    @Test
    fun testAdd_multipleTask() {
        val queueTask = TestQueueManager()

        val testTasks = createMultipleTasks(queueTask, true)
        assertEquals(testTasks.count(), queueTask.numTasks())

        // If tasks are added again the number of tasks should
        // not change or incremented.
        for (testTask in testTasks) {
            queueTask.add(testTask.first, testTask.second)
        }
        assertEquals(testTasks.count(), queueTask.numTasks())
    }

    @Test
    fun testGet_singleTask() {
        val queueTask = TestQueueManager()

        val testTask = createSingleTask(queueTask, true)
        val retrievedTask = queueTask.get(testTask.trackingId)
        assertNotNull(retrievedTask)
        assertEquals(testTask.trackingId, retrievedTask!!.trackingId)
    }

    @Test
    fun testGet_nullTask() {
        val queueTask = TestQueueManager()

        // Retrieved a task when the queue is empty
        val retrievedTaskWhenQueueIsEmpty = queueTask.get("NULL")
        assertNull(retrievedTaskWhenQueueIsEmpty)

        createSingleTask(queueTask, true)

        // Retrieved a task when there are task being queued
        val retrievedTaskWhenQueueIsNotEmpty = queueTask.get("NULL")
        assertNull(retrievedTaskWhenQueueIsNotEmpty)
    }

    @Test
    fun testGet_multipleTask() {
        val queueTask = TestQueueManager()

        val testTasks = createMultipleTasks(queueTask, true)
        for (testTask in testTasks) {
            val retrievedTask = queueTask.get(testTask.first)
            assertNotNull(retrievedTask)
            assertEquals(testTask.first, retrievedTask!!.trackingId)
        }
    }

    @Test
    fun testRemove_singleTask() {
        val queueTask = TestQueueManager()

        val testTask = createSingleTask(queueTask, true)
        val isRemoved = queueTask.remove(testTask.trackingId)
        assertTrue(isRemoved)
        assertTrue(queueTask.isEmpty())
        assertFalse(queueTask.isBusy())
        assertEquals(0, queueTask.numTasks())
    }

    @Test
    fun testRemove_currentTask() {
        val queueTask = TestQueueManager()

        val testTask = createSingleTask(queueTask, true)
        testTask.preventAutoComplete = true
        queueTask.start()
        val isRemoved = queueTask.remove(testTask.trackingId)
        assertTrue(isRemoved)
        assertTrue(queueTask.isEmpty())
        assertFalse(queueTask.isBusy())
        assertEquals(0, queueTask.numTasks())
    }

    @Test
    fun testRemove_unknownTask() {
        val queueTask = TestQueueManager()

        createSingleTask(queueTask, true)

        val isRemoved = queueTask.remove("UNKNOWN")
        assertFalse(isRemoved)
        assertFalse(queueTask.isEmpty())
        assertEquals(1, queueTask.numTasks())
    }

    @Test
    fun testRemove_multipleTask() {
        val queueTask = TestQueueManager()

        val testTasks = createMultipleTasks(queueTask, true)
        var numCurrentRemoved = 0
        val numTasks = testTasks.count()
        for (testTask in testTasks) {
            val isRemoved = queueTask.remove(testTask.first)
            assertTrue(isRemoved)
            numCurrentRemoved += 1
            assertEquals(numTasks - numCurrentRemoved, queueTask.numTasks())
        }
        assertTrue(queueTask.isEmpty())
        assertFalse(queueTask.isBusy())
        assertEquals(0, queueTask.numTasks())
    }

    @Test
    fun testClear_singleTask() {
        val queueTask = TestQueueManager()

        createSingleTask(queueTask, true)
        queueTask.clear()
        assertTrue(queueTask.isEmpty())
        assertFalse(queueTask.isBusy())
        assertEquals(0, queueTask.numTasks())

        val testTasks = createSingleTask(queueTask, true)
        testTasks.preventAutoComplete = true
        queueTask.start()
        queueTask.clear()
        assertTrue(queueTask.isEmpty())
        assertFalse(queueTask.isBusy())
        assertEquals(0, queueTask.numTasks())
    }

    @Test
    fun testClear_multipleTask() {
        val queueTask = TestQueueManager()

        createMultipleTasks(queueTask, true)
        queueTask.clear()
        assertTrue(queueTask.isEmpty())
        assertFalse(queueTask.isBusy())
        assertEquals(0, queueTask.numTasks())

        val testTasks = createMultipleTasks(queueTask, true)
        testTasks.first().second.preventAutoComplete = true
        queueTask.start()
        queueTask.clear()
        assertTrue(queueTask.isEmpty())
        assertFalse(queueTask.isBusy())
        assertEquals(0, queueTask.numTasks())
    }

    @Test
    fun testStart_task() {
        val queueTask = TestQueueManager()

        // Test starting the manager while there are no tasks added
        queueTask.start()
        assertFalse(queueTask.isBusy())

        // Now start the manager with task on it
        val testTask = createSingleTask(queueTask, true)
        testTask.preventAutoComplete = true
        queueTask.start()
        assertTrue(queueTask.isBusy())

        // If the manager has already started, it cannot start a new
        // task all over again.
        for (i in 1..20) {
            queueTask.start()
        }
        assertEquals(1, queueTask.numTasks())
    }

    @Test
    fun testNext_task() {
        val queueTask = TestQueueManager()

        val testTasks = createMultipleTasks(queueTask, true)
        val totalTasks = testTasks.count()
        for (testTask in testTasks) {
            // Prevent all of the tasks from being automatically finished
            testTask.second.preventAutoComplete = true
        }
        queueTask.start()
        assertEquals(totalTasks, queueTask.numTasks())

        // Trigger the manager for next task to be processed, this will test
        // the tasks being complete one by one until all are finished
        for (i in 1..totalTasks) {
            queueTask.next()
            assertEquals(totalTasks - i, queueTask.numTasks())
        }
        assertTrue(queueTask.isEmpty())
        assertFalse(queueTask.isBusy())

        // Calling managers's next for an empty tasks will just ignore start tasks
        for (i in 1..20) {
            queueTask.next()
        }
        assertTrue(queueTask.isEmpty())
        assertFalse(queueTask.isBusy())
    }

    @Test
    fun testSuccess_singleTask() {
        val queueTask = TestQueueManager()

        val testTask = createSingleTask(queueTask, true)
        testTask.preventAutoComplete = true
        queueTask.start()

        // Test the manager for a successful task
        queueTask.success()
        assertEquals(queueTask.lastSuccessfulTrackingId, testTask.trackingId)
        assertTrue(queueTask.isEmpty())
        assertFalse(queueTask.isBusy())
    }

    @Test
    fun testSuccess_multipleTask() {
        val queueTask = TestQueueManager()

        val testTasks = createMultipleTasks(queueTask, true)
        for (testTask in testTasks) {
            // Prevent all of the tasks from being automatically finished
            testTask.second.preventAutoComplete = true
        }
        queueTask.start()

        // Test the manager for a successful tasks
        for (testTask in testTasks) {
            queueTask.success()
            assertEquals(queueTask.lastSuccessfulTrackingId, testTask.first)
        }
        assertTrue(queueTask.isEmpty())
        assertFalse(queueTask.isBusy())
    }

    @Test
    fun testFailure_singleTask() {
        val queueTask = TestQueueManager()

        val testTask = createSingleTask(queueTask, true)
        testTask.preventAutoComplete = true
        queueTask.start()

        // Test the manager for a successful task
        queueTask.failure()
        assertEquals(queueTask.lastFailedTrackingId, testTask.trackingId)
        assertTrue(queueTask.isEmpty())
        assertFalse(queueTask.isBusy())
    }

    @Test
    fun testFailure_multipleTask() {
        val queueTask = TestQueueManager()

        val testTasks = createMultipleTasks(queueTask, true)
        for (testTask in testTasks) {
            // Prevent all of the tasks from being automatically finished
            testTask.second.preventAutoComplete = true
        }
        queueTask.start()

        // Test the manager for a successful tasks
        for (testTask in testTasks) {
            queueTask.failure()
            assertEquals(queueTask.lastFailedTrackingId, testTask.first)
        }
        assertTrue(queueTask.isEmpty())
        assertFalse(queueTask.isBusy())
    }

    @Test
    fun testListener_successTask() {
        val queueTaskWithListener = TestQueueManager()
        val queueTaskWithoutListener = TestQueueManager(enableListener = false)
        queueTaskWithoutListener.setStartService(true)

        createSingleTask(queueTaskWithListener, true).preventAutoComplete = true
        createSingleTask(queueTaskWithoutListener, true).preventAutoComplete = true
        queueTaskWithListener.start()
        queueTaskWithoutListener.start()

        // Test the manager for a successful task
        queueTaskWithListener.success()
        queueTaskWithoutListener.success()

        assertNotNull(queueTaskWithListener.lastSuccessfulTrackingId)
        assertTrue(queueTaskWithListener.isStarted)
        assertTrue(queueTaskWithListener.isFinished)

        assertNull(queueTaskWithoutListener.lastSuccessfulTrackingId)
        assertTrue(queueTaskWithoutListener.isServiceStarted)
        assertFalse(queueTaskWithoutListener.isStarted)
        assertFalse(queueTaskWithoutListener.isFinished)
    }

    @Test
    fun testListener_failedTask() {
        val queueTaskWithListener = TestQueueManager()
        val queueTaskWithoutListener = TestQueueManager(enableListener = false)
        queueTaskWithoutListener.setStartService(true)

        createSingleTask(queueTaskWithListener, true).preventAutoComplete = true
        createSingleTask(queueTaskWithoutListener, true).preventAutoComplete = true
        queueTaskWithListener.start()
        queueTaskWithoutListener.start()

        // Test the manager for a successful task
        queueTaskWithListener.failure()
        queueTaskWithoutListener.failure()

        assertNotNull(queueTaskWithListener.lastFailedTrackingId)
        assertTrue(queueTaskWithListener.isStarted)
        assertTrue(queueTaskWithListener.isFinished)

        assertNull(queueTaskWithoutListener.lastFailedTrackingId)
        assertTrue(queueTaskWithoutListener.isServiceStarted)
        assertFalse(queueTaskWithoutListener.isStarted)
        assertFalse(queueTaskWithoutListener.isFinished)
    }

    private fun createSingleTask(queueTask: TestQueueManager, addToQueue: Boolean = false): TestTask {
        val testTaskTrackingId = "1"
        val testTask = TestTask(testTaskTrackingId, queueTask)
        if (addToQueue) {
            queueTask.add(testTaskTrackingId, testTask)
        }
        return testTask
    }

    private fun createMultipleTasks(queueTask: TestQueueManager, addToQueue: Boolean = false): ArrayList<Pair<String, TestTask>> {
        val testTasks = ArrayList<Pair<String, TestTask>>()
        for (i in 1..20) {
            val testTaskTrackingId = "$i"
            val testTask = TestTask(testTaskTrackingId, queueTask)
            testTasks.add(Pair(testTaskTrackingId, testTask))
            if (addToQueue) {
                queueTask.add(testTaskTrackingId, testTask)
            }
        }
        return testTasks
    }
}

class TestQueueManager: AbstractQueueManager<TestTask>, QueueManagerListener {

    var lastSuccessfulTrackingId: String? = null
    var lastFailedTrackingId: String? = null
    var isServiceStarted: Boolean = false
    var isStarted: Boolean = false
    var isFinished: Boolean = false

    constructor(): this(true)

    constructor(enableListener: Boolean) {
        if (enableListener) {
            this.listener = this
        }
        this.setStartService(false)
    }

    override fun onStartService() {
        isServiceStarted = true
    }

    override fun onSuccess(trackingId: String) {
        lastSuccessfulTrackingId = trackingId
    }

    override fun onFailure(trackingId: String) {
        lastFailedTrackingId = trackingId
    }

    override fun onStart() {
        isStarted = true
    }

    override fun onFinish() {
        isFinished = true
    }
}

class TestTask(val trackingId: String, val queueTask: TestQueueManager): Task {

    var isStarting = false
    var isCancelled = false
    var isFailed = false
    var isSuccess = false

    var preventAutoComplete = false

    fun triggerSuccess() {
        isSuccess = true
        queueTask.success()
    }

    fun triggerFailure() {
        isFailed = true
        queueTask.failure()
    }

    override fun onStart(trackingId: String) {
        isStarting = true
        if (!preventAutoComplete) {
            triggerSuccess()
        }
    }

    override fun onCancel() {
        isCancelled = true
    }

    override fun onError(error: Throwable) {
        onCancel()
        triggerFailure()
    }
}