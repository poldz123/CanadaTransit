package com.rodolfonavalon.canadatransit.controller.util

import android.content.Context
import android.os.Environment
import java.io.File
import timber.log.Timber
import android.os.StatFs
import com.rodolfonavalon.canadatransit.model.database.TransferableEntity

/**
 * FileUtil
 */
object FileUtil {

    private const val TEMP_FILE_NAME_SUFFIX = "-temp"

    /**
     * createFile
     */
    fun createFile(context: Context, transferableEntity: TransferableEntity, temporary: Boolean = false): File {
        DebugUtil.assertTrue(transferableEntity.entityDirectoryPath().isNotEmpty(), "Entity's directory path is empty")
        DebugUtil.assertTrue(transferableEntity.entityId().isNotEmpty(), "Entity's id is empty")
        val suffix = if (temporary) TEMP_FILE_NAME_SUFFIX else ""
        val fileName = transferableEntity.entityId() + suffix
        return File(createInternalDirectoryFile(context, transferableEntity.entityDirectoryPath()), fileName)
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

    private fun availableDiskMemory(memoryFileAbsolutePath: String): Long {
        val statFs = StatFs(memoryFileAbsolutePath)
        val availableBlocks = statFs.availableBlocksLong
        val blockSize = statFs.blockSizeLong
        val freeMemoryInMegabytes = availableBlocks * blockSize / 1048576L
        Timber.d("Disk Memory left: %d MB", freeMemoryInMegabytes)
        return freeMemoryInMegabytes
    }

}