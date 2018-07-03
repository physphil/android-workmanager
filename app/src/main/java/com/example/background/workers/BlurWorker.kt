package com.example.background.workers

import android.graphics.BitmapFactory
import android.util.Log
import androidx.work.Worker
import com.example.background.R

class BlurWorker : Worker() {

    private val TAG = BlurWorker::class.java.simpleName

    override fun doWork(): Result {
        return try {
            val context = applicationContext
            val picture = BitmapFactory.decodeResource(context.resources, R.drawable.test)
            val blurred = WorkerUtils.blurBitmap(picture, context)
            val outputUri = WorkerUtils.writeBitmapToFile(context, blurred)

            WorkerUtils.makeStatusNotification("Output is $outputUri", context)
            Result.SUCCESS
        } catch (t: Throwable) {
            Log.e(TAG, "Error applying blur", t)
            Result.FAILURE
        }
    }
}