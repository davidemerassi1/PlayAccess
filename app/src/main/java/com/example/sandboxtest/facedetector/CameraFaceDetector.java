package com.example.sandboxtest.facedetector;

import android.content.Context;
import android.media.Image;
import android.util.Log;

import androidx.annotation.OptIn;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;

import com.example.sandboxtest.utils.AlwaysForegroundLifecycleOwner;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class CameraFaceDetector {
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private String TAG = "myApp";
    private boolean analyze = false;
    private FaceDetectorOptions options =
            new FaceDetectorOptions.Builder()
                    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                    .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                    .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                    .build();
    private FaceDetector faceDetector = FaceDetection.getClient(options);
    private OnFaceRecognizedListener listener;

    public CameraFaceDetector(Context context, OnFaceRecognizedListener listener) {
        this.listener = listener;
        cameraProviderFuture = ProcessCameraProvider.getInstance(context);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                        .build();

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context), this::detectFaces);

                Preview preview = new Preview.Builder().build();
                Camera camera = cameraProvider.bindToLifecycle(new AlwaysForegroundLifecycleOwner(), cameraSelector, preview, imageAnalysis);
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error", e);
            }
        }, ContextCompat.getMainExecutor(context));
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void detectFaces(ImageProxy imageProxy) {
        if (!analyze) {
            imageProxy.close();
            return;
        }
        Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
            Task<List<Face>> result = faceDetector.process(image);
            result.addOnSuccessListener(faces -> {
                        try {
                            for (Face face : faces) {
                                //sinistra: y positivo, destra: y negativo
                                //su: x positivo, giù: x negativo
                                //Log.d(TAG, "X:" + face.getHeadEulerAngleX() + ", Y:" + face.getHeadEulerAngleY() + ", Z:" + face.getHeadEulerAngleZ());
                                //sorride: all'incirca >0.3
                                //Log.d(TAG, "Smile: " + face.getSmilingProbability());
                                /*StringBuilder sb = new StringBuilder();
                                for(PointF point : face.getContour(FaceContour.LEFT_EYEBROW_TOP).getPoints()) {
                                    sb.append("(").append(point.x).append(",").append(point.y).append(") ");
                                };
                                Log.d(TAG, "Left eyebrow top: " + sb.toString());*/
                                listener.onFaceRecognized(face);
                            }
                        } finally {
                            imageProxy.close();
                        }
                    })
                    .addOnFailureListener(
                            e -> {
                                // Gestione degli errori
                                Log.e(TAG, "Face detection failed", e);
                                imageProxy.close();
                            });
        }
    }

    public void startDetection() {
        analyze = true;
    }
}
