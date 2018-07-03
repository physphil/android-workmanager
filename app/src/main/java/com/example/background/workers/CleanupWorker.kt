package com.example.background.workers

import android.util.Log
import androidx.work.Worker
import com.example.background.Constants
import java.io.File

class CleanupWorker : Worker() {
    private val TAG = CleanupWorker::class.java.simpleName

    override fun doWork(): Result {
        val applicationContext = applicationContext

        try {
            val outputDirectory = File(applicationContext.filesDir, Constants.OUTPUT_PATH)
            if (outputDirectory.exists()) {
                val entries = outputDirectory.listFiles()
                entries?.let {
                    it.forEach { entry ->
                        val name = entry.name
                        if (name.isNotEmpty() && name.endsWith(".png")) {
                            val deleted = entry.delete()
                            Log.i(TAG, String.format("Deleted $name - $deleted"))
                        }
                    }
                }
            }
            return Result.SUCCESS
        } catch (exception: Exception) {
            Log.e(TAG, "Error cleaning up", exception)
            return Result.FAILURE
        }
    }
}