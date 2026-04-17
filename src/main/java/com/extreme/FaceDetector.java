package com.extreme;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import org.opencv.core.*;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FaceDetector {

    private VideoCapture cam;
    private volatile boolean isRunning = true; // Volatile for thread safety

    public void startCameraFX(ImageView view, Runnable onDetected) {
        cam = new VideoCapture(0);

        if (!cam.isOpened()) {
            System.err.println("❌ Camera Error: Check if camera is in use by another app.");
            return;
        }

        CascadeClassifier detector = loadCascade();
        if (detector == null || detector.empty()) return;

        Mat frame = new Mat();
        Mat grayFrame = new Mat();

        Thread streamThread = new Thread(() -> {
            int detectionBuffer = 0;

            while (isRunning) {
                if (cam.read(frame)) {
                    // Pre-process for better recognition accuracy
                    Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
                    Imgproc.equalizeHist(grayFrame, grayFrame);

                    MatOfRect faces = new MatOfRect();
                    // Detecting faces - size constrained for speed
                    detector.detectMultiScale(grayFrame, faces, 1.1, 5, 0, new Size(150, 150), new Size());

                    Rect[] facesArray = faces.toArray();
                    for (Rect r : facesArray) {
                        // Drawing the Sky Blue Box (BGR for OpenCV: Blue=199, Green=132, Red=2)
                        Imgproc.rectangle(
                                frame,
                                new Point(r.x, r.y),
                                new Point(r.x + r.width, r.y + r.height),
                                new Scalar(199, 132, 2),
                                3 // Thicker border for better visibility
                        );
                        detectionBuffer++;
                    }

                    // On-detection logic: After 10 consistent frames, confirm identity
                    if (facesArray.length > 0 && detectionBuffer >= 10) {
                        detectionBuffer = 0;
                        isRunning = false; // Stop scanning once confirmed
                        Platform.runLater(onDetected);
                    }

                    // Render to the Dashboard
                    ImageViewFrameConverter.update(view, frame);
                    faces.release();
                }

                try { Thread.sleep(33); } catch (InterruptedException e) { break; }
            }

            // Proper hardware cleanup
            frame.release();
            grayFrame.release();
            if (cam.isOpened()) cam.release();
            System.out.println("📹 Camera stream terminated safely.");
        });

        streamThread.setDaemon(true);
        streamThread.start();
    }

    public void stop() {
        this.isRunning = false;
    }

    private CascadeClassifier loadCascade() {
        try {
            // This ensures it works on your machine AND when you export it as a .JAR
            InputStream is = getClass().getResourceAsStream("/haarcascade_frontalface_default.xml");
            if (is == null) throw new Exception("Cascade XML not found in resources!");

            File tempFile = File.createTempFile("sentinel_cascade", ".xml");
            tempFile.deleteOnExit();
            Files.copy(is, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return new CascadeClassifier(tempFile.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("❌ Cascade Load Failure: " + e.getMessage());
            return null;
        }
    }
}