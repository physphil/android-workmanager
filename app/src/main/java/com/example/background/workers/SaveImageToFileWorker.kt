package com.example.background.workers

import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import com.example.background.Constants
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SaveImageToFileWorker : Worker() {

    private val TAG = SaveImageToFileWorker::class.java.simpleName

    private val TITLE = "Blurred Image"
    private val DATE_FORMATTER = SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z", Locale.getDefault())

    override fun doWork(): Result {
        val context = applicationContext
        val resolver = context.contentResolver

        try {
            // Attempt to save temporary blurred image to file system
            val tempImageUri = inputData.getString(Constants.KEY_IMAGE_URI, null)
            val bitmap = BitmapFactory.decodeStream(resolver.openInputStream(Uri.parse(tempImageUri)))
            val finalImageUri = MediaStore.Images.Media.insertImage(resolver, bitmap, TITLE, DATE_FORMATTER.format(Date()))
            if (finalImageUri.isEmpty()) {
                Log.e(TAG, "Writing to MediaStore failed")
                return Result.FAILURE
            }

            // Set saved final file URI as output data
            outputData = Data.Builder().
                    putString(Constants.KEY_IMAGE_URI, finalImageUri.toString())
                    .build()
            return Result.SUCCESS
        } catch (ex: Exception) {
            Log.e(TAG, "Unable to save image to Gallery", ex)
            return Result.FAILURE
        }
    }
}