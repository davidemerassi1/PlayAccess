package it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.unimi.di.ewlab.iss.common.utils.Utils;


// Frame catturato dalla fotocamera con associata la mappa di features estratte da esso
public class Frame implements Serializable {
    private SerializableBitmap bitmap;
    private final List<Float> features;

    public Frame(@Nullable Bitmap bitmap, @NonNull List<Float> features) {
        if (bitmap != null)
            this.bitmap = new SerializableBitmap(bitmap);
        else this.bitmap = null;
        this.features = new ArrayList<>(features);
    }

    public List<Float> getFeatures() {
        return Collections.unmodifiableList(features);
    }

    public Bitmap getBitmap() {
        if (bitmap == null) return null;
        else return bitmap.bitmap;
    }

    public void clearBitmap() {
        if (bitmap != null) bitmap.bitmap.recycle();
        bitmap = null;
    }

    // Le Bitmap non sono serializzabili di base e pertanto è necessario definire una classe wrapper
    private static class SerializableBitmap implements Serializable {

        Bitmap bitmap;

        public SerializableBitmap(Bitmap bitmap) {
            this.bitmap = bitmap.copy(bitmap.getConfig(), false);
        }

        private void writeObject(ObjectOutputStream out) throws IOException {
            out.writeObject(
                Utils.INSTANCE.encodeToBase64(bitmap)
            );
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            bitmap = Utils.INSTANCE.decodeBase64ToBitmap(
                    (String) in.readObject()
            );
        }
    }
}
