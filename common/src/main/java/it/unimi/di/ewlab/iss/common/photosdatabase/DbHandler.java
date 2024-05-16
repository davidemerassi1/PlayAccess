package it.unimi.di.ewlab.iss.common.photosdatabase;

import androidx.annotation.NonNull;

import java.util.List;

public class DbHandler {
    private final PhotosDatabase db;

    public DbHandler(PhotosDatabase db) {
        this.db = db;
    }

    public void insert(final PosePhoto posePhoto) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                db.posePhotoDao().insert(posePhoto);
            }
        });
        thread.start();
    }

    public void getByLabel(String label, final OnDatabaseResultCallback<PosePhoto> resultCallback) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                    PosePhoto result = db.posePhotoDao().getByPoseName(label);
                    resultCallback.result(result);
            }
        });
        thread.start();
    }

    public void getAllPoses(final OnDatabaseResultCallback<List<PosePhoto>> resultCallback) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                List<PosePhoto> result = db.posePhotoDao().getAll();
                resultCallback.result(result);
            }
        });
        thread.start();
    }

    public void deletePosePhotoByLabel(String label) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                PosePhoto temp = new PosePhoto();
                temp.poseName = label;
                db.posePhotoDao().delete(temp);
            }
        });
        thread.start();
    }

    public void getByLabelsList(@NonNull List<String> labels, final OnDatabaseResultCallback<List<PosePhoto>> resultCallback ) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                List<PosePhoto> posePhotos = db.posePhotoDao().getByLabelsList(labels);
                resultCallback.result(posePhotos);
            }
        });
        thread.start();
    }

    public void deleteAllPosePhoto() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                db.posePhotoDao().nukeTable();
            }
        });
        thread.start();
    }
}
