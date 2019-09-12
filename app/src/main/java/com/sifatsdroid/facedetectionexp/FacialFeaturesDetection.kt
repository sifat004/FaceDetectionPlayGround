package com.sifatsdroid.facedetectionexp

import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions

/**
 * Created by Sifat Ullah Chowdhury on 9/12/2019.
 * Durbin Labs Ltd
 * sif.sifat24@gmail.com
 */
class FacialFeaturesDetection{

    // High-accuracy landmark detection and face classification
    val highAccuracyOpts = FirebaseVisionFaceDetectorOptions.Builder()
        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
        .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
        .build()

    // Real-time contour detection of multiple faces
    val realTimeOpts = FirebaseVisionFaceDetectorOptions.Builder()
        .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
        .build()

}
