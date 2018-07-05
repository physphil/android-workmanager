/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.background;

import android.arch.lifecycle.ViewModel;
import android.net.Uri;
import android.text.TextUtils;

import com.example.background.workers.BlurWorker;
import com.example.background.workers.CleanupWorker;
import com.example.background.workers.SaveImageToFileWorker;

import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkManager;

public class BlurViewModel extends ViewModel {

    private Uri imageUri;
    private WorkManager workManager;

    public BlurViewModel() {
        workManager = WorkManager.getInstance();
    }

    /**
     * Create the WorkRequest to apply the blur and save the resulting image
     *
     * @param blurLevel The amount to blur the image
     */
    void applyBlur(int blurLevel) {
        OneTimeWorkRequest cleanRequest = new OneTimeWorkRequest.Builder(CleanupWorker.class).build();
        OneTimeWorkRequest blurRequest = new OneTimeWorkRequest.Builder(BlurWorker.class)
                .setInputData(createInputDataForUri())
                .build();
        OneTimeWorkRequest saveRequest = new OneTimeWorkRequest.Builder(SaveImageToFileWorker.class).build();

        // Start with clean directory and first blur request
        WorkContinuation continuation = workManager.beginUniqueWork(Constants.IMAGE_MANIPULATION_WORK_NAME, ExistingWorkPolicy.REPLACE, cleanRequest)
                .then(blurRequest);

        // Add another blur request for each blur level
        for (int i = 1; i < blurLevel; i++) {
            continuation.then(OneTimeWorkRequest.from(BlurWorker.class));
        }

        // Add final save image to file request
        continuation.then(saveRequest)
                .enqueue();
    }

    private Uri uriOrNull(String uriString) {
        if (!TextUtils.isEmpty(uriString)) {
            return Uri.parse(uriString);
        }
        return null;
    }

    private Data createInputDataForUri() {
        Data.Builder builder = new Data.Builder();
        if (imageUri != null) {
            builder.putString(Constants.KEY_IMAGE_URI, imageUri.toString());
        }
        return builder.build();
    }

    /**
     * Setters
     */
    void setImageUri(String uri) {
        imageUri = uriOrNull(uri);
    }

    /**
     * Getters
     */
    Uri getImageUri() {
        return imageUri;
    }

}