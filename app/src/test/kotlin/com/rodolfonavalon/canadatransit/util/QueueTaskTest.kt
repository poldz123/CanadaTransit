package com.rodolfonavalon.canadatransit.util

import com.rodolfonavalon.canadatransit.controller.util.queue.AbstractQueueTask
import com.rodolfonavalon.canadatransit.controller.util.queue.Task
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.*

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class QueueTaskTest {

    @Test
    fun testAddTask() {
        val queueTask = TestQueueTask()

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
    fun testAddMultipleTasks() {
        val queueTask = TestQueueTask()

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
    fun testGetTask() {
        val queueTask = TestQueueTask()

        val testTask = createSingleTask(queueTask, true)
        val retrievedTask = queueTask.get(testTask.trackingId)
        assertNotNull(retrievedTask)
        assertEquals(testTask.trackingId, retrievedTask!!.trackingId)
    }

    @Test
    fun testGetNullTask() {
        val queueTask = TestQueueTask()

        // Retrieved a task when the queue is empty
        val retrievedTaskWhenQueueIsEmpty = queueTask.get("NULL")
        assertNull(retrievedTaskWhenQueueIsEmpty)

        createSingleTask(queueTask, true)

        // Retrieved a task when there are task being queued
        val retrievedTaskWhenQueueIsNotEmpty = queueTask.get("NULL")
        assertNull(retrievedTaskWhenQueueIsNotEmpty)
    }

    @Test
    fun testGetMultipleTasks() {
        val queueTask = TestQueueTask()

        val testTasks = createMultipleTasks(queueTask, true)
        for (testTask in testTasks) {
            val retrievedTask = queueTask.get(testTask.first)
            assertNotNull(retrievedTask)
            assertEquals(testTask.first, retrievedTask!!.trackingId)
        }
    }

    @Test
    fun testRemoveTask() {
        val queueTask = TestQueueTask()

        val testTask = createSingleTask(queueTask, true)
        val isRemoved = queueTask.remove(testTask.trackingId)
        assertTrue(isRemoved)
        assertTrue(queueTask.isEmpty())
        assertEquals(0, queueTask.numTasks())
    }

    @Test
    fun testRemoveCurrentTask() {
        val queueTask = TestQueueTask()

        val testTask = createSingleTask(queueTask, true)
        testTask.preventAutoComplete = true
        queueTask.start()
        val isRemoved = queueTask.remove(testTask.trackingId)
        assertTrue(isRemoved)
        assertTrue(queueTask.isEmpty())
        assertEquals(0, queueTask.numTasks())
    }

    @Test
    fun testRemoveUnknownTask() {
        val queueTask = TestQueueTask()

        createSingleTask(queueTask, true)

        val isRemoved = queueTask.remove("UNKNOWN")
        assertFalse(isRemoved)
        assertFalse(queueTask.isEmpty())
        assertEquals(1, queueTask.numTasks())
    }

    @Test
    fun testRemoveMultipleTasks() {
        val queueTask = TestQueueTask()

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
        assertEquals(0, queueTask.numTasks())
    }

    @Test
    fun testClearMultipleTasks() {
        val queueTask = TestQueueTask()

        createMultipleTasks(queueTask, true)
        queueTask.clear()
        assertTrue(queueTask.isEmpty())
        assertEquals(0, queueTask.numTasks())

        val testTasks = createMultipleTasks(queueTask, true)
        testTasks.first().second.preventAutoComplete = true
        queueTask.start()
        queueTask.clear()
        assertTrue(queueTask.isEmpty())
        assertEquals(0, queueTask.numTasks())
    }

    private fun createSingleTask(queueTask: TestQueueTask, addToQueue: Boolean = false): TestTask {
        val testTaskTrackingId = "1"
        val testTask = TestTask(testTaskTrackingId, queueTask)
        if (addToQueue) {
            queueTask.add(testTaskTrackingId, testTask)
        }
        return testTask
    }

    private fun createMultipleTasks(queueTask: TestQueueTask, addToQueue: Boolean = false): ArrayList<Pair<String, TestTask>> {
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

class TestQueueTask: AbstractQueueTask<TestTask>() {

    override fun onSuccess(trackingId: String) {

    }

    override fun onFailure(trackingId: String) {

    }

    override fun onComplete() {

    }
}

class TestTask(val trackingId: String, val queueTask: TestQueueTask): Task {

    var isStarting = false
    var isCancelled = false
    var isFailed = false

    var preventAutoComplete = false

    override fun onStart(trackingId: String) {
        isStarting = true

        if (!preventAutoComplete) {
            queueTask.success()
        }
    }

    override fun onCancel() {
        isCancelled = true
    }

    override fun onError(error: Throwable) {
        isFailed = true
        onCancel()
        queueTask.failure()
    }
}