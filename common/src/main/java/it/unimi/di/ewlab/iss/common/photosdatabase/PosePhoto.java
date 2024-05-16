package it.unimi.di.ewlab.iss.common.photosdatabase;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class PosePhoto {

    @PrimaryKey
    @NonNull
    public String poseName = "";

    @NonNull
    public String photo = "";

    @Ignore
    public Bitmap decodedPhoto = null;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PosePhoto posePhoto = (PosePhoto) o;
        return poseName.equals(posePhoto.poseName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(poseName, photo, decodedPhoto);
    }
}
