package edu.cit.sarismart.features.user.tabs.scan.ui.scanner

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.TimeUnit

class BarcodeAnalyzer(
    private val onBarcodeDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {
    private var lastAnalyzedTimestamp = 0L
    private val minimumInterval = TimeUnit.SECONDS.toMillis(1) // 1 second interval

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val currentTimestamp = System.currentTimeMillis()
        if (currentTimestamp - lastAnalyzedTimestamp >= minimumInterval) {
            imageProxy.image?.let { image ->
                val inputImage = InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)

                // Process image with ML Kit barcode scanner
                val scanner = BarcodeScanning.getClient()
                scanner.process(inputImage)
                    .addOnSuccessListener { barcodes ->
                        if (barcodes.isNotEmpty()) {
                            // We only care about the first barcode
                            val barcode = barcodes[0]
                            barcode.rawValue?.let { value ->
                                Log.d("BarcodeAnalyzer", "Barcode detected: $value")
                                onBarcodeDetected(value)
                                lastAnalyzedTimestamp = currentTimestamp
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("BarcodeAnalyzer", "Barcode scanning failed", e)
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            } ?: imageProxy.close()
        } else {
            imageProxy.close()
        }
    }
}