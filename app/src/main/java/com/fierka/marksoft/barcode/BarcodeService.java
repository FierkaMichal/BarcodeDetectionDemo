package com.fierka.marksoft.barcode;

import android.media.Image;
import androidx.camera.core.ImageProxy;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

public class BarcodeService {

    @androidx.camera.core.ExperimentalGetImage
    public void analyze(ImageProxy imageProxy) {
        BarcodeScanner scanner = BarcodeScanning.getClient();

        Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            InputImage image =
                    InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());

            scanner.process(image).addOnSuccessListener(barcodes -> {
                StringBuilder builder = new StringBuilder();
                int i = 0;
                for (Barcode barcode : barcodes) {
                    if (barcode.getDisplayValue() != null) {
                        i++;
                        builder.append("Kod ").append(i).append(": ").append(barcode.getDisplayValue()).append("\n");
                    }
                }
                if (i > 0) {
//                    textView.setText(builder.toString());
                } else {
//                    textView.setText("Nie znaleziono kodów");
                }
            }).addOnFailureListener(Throwable::printStackTrace);
        }
    }
}
