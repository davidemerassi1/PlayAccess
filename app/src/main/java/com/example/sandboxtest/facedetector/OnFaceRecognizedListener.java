package com.example.sandboxtest.facedetector;

import com.google.mlkit.vision.face.Face;

public interface OnFaceRecognizedListener {
    void onFaceRecognized(Face face);
}
