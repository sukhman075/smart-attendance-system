package com.extreme;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.core.MatOfByte;
import java.io.ByteArrayInputStream;

public class ImageViewFrameConverter {

    /**
     * Converts an OpenCV Mat (frame) to a JavaFX Image and updates the View.
     * Optimized to handle the byte conversion efficiently.
     */
    public static void update(ImageView view, Mat frame) {
        if (frame.empty()) return;

        try {
            MatOfByte buffer = new MatOfByte();
            // Using .jpg is slightly faster for real-time streaming than .png
            Imgcodecs.imencode(".jpg", frame, buffer);

            Image img = new Image(new ByteArrayInputStream(buffer.toArray()));

            // Apply to the ImageView
            view.setImage(img);
        } catch (Exception e) {
            System.err.println("⚠️ Frame Conversion Error: " + e.getMessage());
        }
    }
}