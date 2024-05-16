package it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.faceframehandler;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

public interface CameraFrameListener {

    void onCameraFrameAnalyzed(@NonNull Bitmap bitmap);

}
