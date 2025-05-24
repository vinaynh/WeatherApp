package com.android.app.weather.util

import com.android.app.weather.AppExecutors
import java.util.concurrent.Executor

/**
 * A test implementation of AppExecutors that executes tasks synchronously.
 * This is useful for unit testing to ensure that background tasks complete
 * before assertions are made.
 *
 * Note: For this to be used effectively, the class under test (e.g., UserRepository)
 * would need to be refactored to accept an AppExecutors instance via its constructor
 * or a setter method, instead of using the static AppExecutors.getInstance().
 */
class TestAppExecutors(
    private val diskIO: Executor = Executor { it.run() },
    private val networkIO: Executor = Executor { it.run() },
    private val mainThread: Executor = Executor { it.run() }
) : AppExecutors(diskIO, networkIO, mainThread) {

    fun runCurrentDiskIO() {
        // If tasks were queued, this would be the place to run them.
        // Since our test executor runs them immediately, this might not be needed
        // unless the execute method itself queues and this flushes.
        // For the simple Executor { it.run() }, this is a no-op.
    }

    fun runCurrentNetworkIO() {
        // Similar to runCurrentDiskIO()
    }
}
