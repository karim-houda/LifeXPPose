package com.lifexp.pose;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;

import com.google.mediapipe.framework.image.BitmapImageBuilder;
import com.google.mediapipe.framework.image.MPImage;
import com.google.mediapipe.tasks.core.BaseOptions;
import com.google.mediapipe.tasks.vision.core.RunningMode;
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker;
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult;

import java.util.List;

public class PoseDetector implements AutoCloseable {

    private final PoseLandmarker landmarker;

    public PoseDetector(Context context) {
        BaseOptions baseOptions =
            BaseOptions.builder()
                .setModelAssetPath("pose_landmarker_lite.task")
                .build();

        PoseLandmarker.PoseLandmarkerOptions options =
            PoseLandmarker.PoseLandmarkerOptions.builder()
                .setBaseOptions(baseOptions)
                .setRunningMode(RunningMode.IMAGE)
                .setNumPoses(1)
                .build();

        landmarker = PoseLandmarker.createFromOptions(
            context.getApplicationContext(),
            options
        );
    }

    public PointF[] detect(Bitmap bitmap) {
        if (bitmap == null) {
            throw new IllegalArgumentException(
                "Bitmap cannot be null"
            );
        }

        MPImage image =
            new BitmapImageBuilder(bitmap).build();

        PoseLandmarkerResult result =
            landmarker.detect(image);

        if (result.landmarks().isEmpty()) {
            return new PointF[0];
        }

        List<com.google.mediapipe.tasks.components.containers.NormalizedLandmark>
            landmarks = result.landmarks().get(0);

        PointF[] points = new PointF[landmarks.size()];

        for (int i = 0; i < landmarks.size(); i++) {
            points[i] = new PointF(
                landmarks.get(i).x(),
                landmarks.get(i).y()
            );
        }

        return points;
    }

    @Override
    public void close() {
        landmarker.close();
    }
}
