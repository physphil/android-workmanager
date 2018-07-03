package com.example.background.workers

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import com.example.background.Constants

class BlurWorker : Worker() {

    private val TAG = BlurWorker::class.java.simpleName

    override fun doWork(): Result {
        return try {
            val context = applicationContext
            val imageUri = inputData.getString(Constants.KEY_IMAGE_URI, null)

            // Bail if non-valid image uri
            if (imageUri.isNullOrEmpty()) {
                Log.e(TAG, "Invalid input uri")
                throw IllegalArgumentException("Invalid input uri")
            }

            val picture = BitmapFactory.decodeStream(context.contentResolver.openInputStream(Uri.parse(imageUri)))
            val blurred = WorkerUtils.blurBitmap(picture, context)
            val outputUri = WorkerUtils.writeBitmapToFile(context, blurred)

            // Return URI of blurred image as output data
            outputData = Data.Builder()
                    .putString(Constants.KEY_IMAGE_URI, outputUri.toString())
                    .build()

            WorkerUtils.makeStatusNotification("Output is $outputUri", context)
            Result.SUCCESS
        } catch (t: Throwable) {
            Log.e(TAG, "Error applying blur", t)
            Result.FAILURE
        }
    }
}