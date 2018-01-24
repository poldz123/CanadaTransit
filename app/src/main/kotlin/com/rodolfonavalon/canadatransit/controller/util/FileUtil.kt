package com.rodolfonavalon.canadatransit.controller.util

import android.content.Context
import android.os.Environment
import com.rodolfonavalon.canadatransit.model.database.OperatorFeedVersion
import java.io.File
import timber.log.Timber
import android.os.StatFs

/**
 * Type of memories within the application.
 *
 * @property SYSTEM The system memory, entire phone memory
 * @property EXTERNAL The external memory, sd card memory
 * @property INTERNAL The system memory, application memory
 */
enum class MemoryType {
    SYSTEM,
    EXTERNAL,
    INTERNAL
}

/**
 * FileUtil
 */
object FileUtil {

    private const val TRANSIT_LAND_DIRECTORY = "feed/transitland/"

    /**
     * createFile
     */
    fun createFile(context: Context, operatorFeedVersion: OperatorFeedVersion): File {
        val directoryName = TRANSIT_LAND_DIRECTORY + operatorFeedVersion.feedOneStopId
        val fileName = operatorFeedVersion.feedOneStopId + operatorFeedVersion.sha1
        return File(createInternalDirectoryFile(context, directoryName), fileName)
    }

    private fun createInternalDirectoryFile(context: Context, directory: String): File {
        return context.getDir(directory, Context.MODE_PRIVATE)
    }

    private fun createExternalDirectoryFile(directory: String): File {
        val file = File(Environment.getExternalStorageDirectory(), directory)
        // Create the directory only when it was not already created from before
        if (!file.exists()) {
            file.mkdirs()
        }
        return file
    }

    /**
     * availableDiskMemory
     */
    fun availableDiskMemory(memoryType: MemoryType): Long {
        val memoryFileAbsolutePath = when (memoryType) {
            MemoryType.SYSTEM -> Environment.getRootDirectory().absolutePath
            MemoryType.EXTERNAL -> Environment.getExternalStorageDirectory().absolutePath
            MemoryType.INTERNAL -> Environment.getDataDirectory().absolutePath
        }

        val statFs = StatFs(memoryFileAbsolutePath)
        val availableBlocks = statFs.availableBlocksLong
        val blockSize = statFs.blockSizeLong

        val freeMemoryInMegabytes = availableBlocks * blockSize / 1048576L
        Timber.d("Disk Memory left From: %s || %d MB", memoryType, freeMemoryInMegabytes)
        return freeMemoryInMegabytes
    }

}