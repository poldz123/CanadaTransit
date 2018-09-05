package com.rodolfonavalon.canadatransit.controller.util

import android.content.Context
import android.os.Environment
import java.io.File
import timber.log.Timber
import android.os.StatFs
import com.rodolfonavalon.canadatransit.controller.manager.transfer.Transferable

/**
 * FileUtil
 */
object FileUtil {

    private const val TEMP_FILE_NAME_SUFFIX = "-temp"

    /**
     * createFile
     */
    fun createFile(context: Context, transferable: Transferable, temporary: Boolean = false): File {
        DebugUtil.assertTrue(transferable.transferDirectoryPath().isNotEmpty(), "Entity's directory path is empty")
        DebugUtil.assertTrue(transferable.trackingId().isNotEmpty(), "Entity's id is empty")
        val suffix = if (temporary) TEMP_FILE_NAME_SUFFIX else ""
        val fileName = transferable.trackingId() + suffix
        // TODO Change the directory as internal
        return File(createExternalDirectoryFile(transferable.transferDirectoryPath()), fileName)
    }

    private fun createInternalDirectoryFile(context: Context, directory: String): File {
        val file = File(context.filesDir, directory)
        // Create the directory only when it was not already created from before
        if (!file.exists()) {
            file.mkdirs()
        }
        return file
    }

    private fun createExternalDirectoryFile(directory: String): File {
        // TODO Must request a permission dialog for user to accept the permission to write in
        //       the sd card.
        val file = File(Environment.getExternalStorageDirectory(), directory)
        // Create the directory only when it was not already created from before
        if (!file.exists()) {
            file.mkdirs()
        }
        return file
    }

    /**
     * availableInternalMemory
     */
    fun availableInternalMemory(): Long = availableDiskMemory(Environment.getDataDirectory().absolutePath)

    /**
     * availableExternalMemory
     */
    fun availableExternalMemory(): Long = availableDiskMemory(Environment.getExternalStorageDirectory().absolutePath)

    /**
     * availableSystemMemory
     */
    fun availableSystemMemory(): Long = availableDiskMemory(Environment.getRootDirectory().absolutePath)

    @Suppress("MagicNumber")
    private fun availableDiskMemory(memoryFileAbsolutePath: String): Long {
        val statFs = StatFs(memoryFileAbsolutePath)
        val availableBlocks = statFs.availableBlocksLong
        val blockSize = statFs.blockSizeLong
        val freeMemoryInMegabytes = availableBlocks * blockSize / 1048576L
        Timber.d("Disk Memory left: %d MB", freeMemoryInMegabytes)
        return freeMemoryInMegabytes
    }

}
