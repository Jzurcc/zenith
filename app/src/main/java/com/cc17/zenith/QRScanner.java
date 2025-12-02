package com.cc17.zenith;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Surface;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.camera.view.PreviewView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import android.net.Uri;

public class QRScanner extends AppCompatActivity {
    private static final String TAG = "MLKit Barcode";
    private static final int PERMISSION_CODE = 1001;
    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    private PreviewView previewView;
    private ImageButton importImagesBtn;
    private ImageButton fileImportBtn;
    private CameraSelector cameraSelector;
    private ProcessCameraProvider cameraProvider;
    private Preview previewUseCase;
    private ImageAnalysis analysisUseCase;
    private ImageButton shutterBtn;
    // Handler for timeout
    private android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
    private Runnable timeoutRunnable;
    private static final long SCAN_TIMEOUT = 5000; // 5 seconds timeout

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    setupCamera();
                } else {
                    Toast.makeText(this, "Camera permission is required.", Toast.LENGTH_SHORT).show();
                }
            }
    );

    // Launcher for Gallery (Import Images)
    private final ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    stopAnalysis(); // Stop live camera to save resources
                    scanImageFromUri(uri);
                }
            }
    );

    // Launcher for Files (File Import)
    private final ActivityResultLauncher<String[]> fileLauncher = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(),
            uri -> {
                if (uri != null) {
                    stopAnalysis();
                    scanImageFromUri(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_qrscanner);

        previewView = findViewById(R.id.previewView);
        shutterBtn = findViewById(R.id.shutterBtn);
        importImagesBtn = findViewById(R.id.importImagesBtn);
        fileImportBtn = findViewById(R.id.fileImportBtn);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        hideSystemBars();
        findViewById(R.id.home).setOnClickListener(v -> finish());
        checkCameraPermission();
        shutterBtn.setOnClickListener(v -> toggleScanning());

        importImagesBtn.setOnClickListener(v -> {
            // Opens the system photo picker. No extra permission needed for this in modern Android.
            galleryLauncher.launch("image/*");
        });

        fileImportBtn.setOnClickListener(v -> {
            // limit to images because ML Kit can only scan images
            fileLauncher.launch(new String[]{"image/*"});
        });
    }

    private void scanImageFromUri(Uri uri) {
        try {
            // Convert URI to InputImage
            InputImage image = InputImage.fromFilePath(getApplicationContext(), uri);
            BarcodeScanner scanner = BarcodeScanning.getClient();

            scanner.process(image)
                    .addOnSuccessListener(barcodes -> {
                        if (!barcodes.isEmpty()) {
                            // Use the same logic as the live camera
                            handleQrResult(barcodes.get(0));
                        } else {
                            Toast.makeText(this, "No QR code found in image", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to process image", e);
                        Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
                    });

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
            setupCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{CAMERA_PERMISSION}, PERMISSION_CODE);
        }
    }

    // Hide the status bar and the navigation bar
    private void hideSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());

        windowInsetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        );
    }


    public void startCamera() {
        if(ContextCompat.checkSelfPermission(this, CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
            setupCamera();
        } else {
            getPermissions();
        }
    }

    private void toggleScanning() {
        bindAnalysisUseCase();
        shutterBtn.setEnabled(false);
        Toast.makeText(this, "Scanning...", Toast.LENGTH_SHORT).show();

        timeoutRunnable = () -> {
            stopAnalysis();
            Toast.makeText(QRScanner.this, "No QR code found", Toast.LENGTH_SHORT).show();
            shutterBtn.setEnabled(true);
        };

        handler.postDelayed(timeoutRunnable, SCAN_TIMEOUT);
    }

    private void getPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA_PERMISSION}, PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupCamera();
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupCamera() {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        int lensFacing = CameraSelector.LENS_FACING_BACK;
        cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();

        cameraProviderFuture.addListener(() -> {
            try {
                if (isDestroyed() || isFinishing()) return;
                cameraProvider = cameraProviderFuture.get();
                bindAllCameraUseCases();
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Camera Init Error", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindAllCameraUseCases() {
        if (cameraProvider != null) {
            cameraProvider.unbindAll();

            bindPreviewUseCase();
        }
    }

    private void bindPreviewUseCase() {
        if (cameraProvider == null) {
            return;
        }

        if (previewUseCase != null) {
            cameraProvider.unbind(previewUseCase);
        }

        Preview.Builder builder = new Preview.Builder();
        builder.setTargetRotation(getRotation());

        previewUseCase = builder.build();
        previewUseCase.setSurfaceProvider(previewView.getSurfaceProvider());

        try {
            cameraProvider.bindToLifecycle(this, cameraSelector, previewUseCase);
        } catch (Exception e) {
            Log.e(TAG, "Error when bind preview", e);
            Toast.makeText(this, "Error starting camera.", Toast.LENGTH_SHORT).show();
        }
    }

    private void bindAnalysisUseCase() {
        if (cameraProvider == null) {
            return;
        }

        if (analysisUseCase != null) {
            cameraProvider.unbind(analysisUseCase);
        }

        Executor cameraExecutor = Executors.newSingleThreadExecutor();

        ImageAnalysis.Builder builder = new ImageAnalysis.Builder();
        builder.setTargetRotation(getRotation());

        analysisUseCase = builder.build();
        analysisUseCase.setAnalyzer(cameraExecutor, this::analyze);

        try {
            cameraProvider.bindToLifecycle(this, cameraSelector, analysisUseCase);
        } catch (Exception e) {
            Log.e(TAG, "Error when bind analysis", e);
        }
    }

    protected int getRotation() throws NullPointerException {
        if (previewView == null || previewView.getDisplay() == null) {
            return Surface.ROTATION_0; // Default if display isn't ready
        }
        return previewView.getDisplay().getRotation();
    }


    @SuppressLint("UnsafeOptInUsageError")
    private void analyze(@NonNull ImageProxy image) {
        if (image.getImage() == null) return;

        InputImage inputImage = InputImage.fromMediaImage(
                image.getImage(),
                image.getImageInfo().getRotationDegrees()
        );

        BarcodeScanner barcodeScanner = BarcodeScanning.getClient();

        barcodeScanner.process(inputImage)
                .addOnSuccessListener(barcodes -> {
                    if (!barcodes.isEmpty()) {
                        handler.removeCallbacks(timeoutRunnable); // Stop timer
                        stopAnalysis();
                        handleQrResult(barcodes.get(0)); // Use the shared logic
                        shutterBtn.setEnabled(true);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Barcode failure", e))
                .addOnCompleteListener(task -> image.close());
    }

    private void handleQrResult(Barcode barcode) {
        String rawValue = barcode.getRawValue();

        if (rawValue != null) {
            // Case 1: Patient Data (JSON)
            if (rawValue.trim().startsWith("{")) {
                Log.d(TAG, "JSON Data found: " + rawValue);
                Intent intent = new Intent(QRScanner.this, MainActivity.class);
                intent.putExtra("scanned_patient_json", rawValue);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
            // Case 2: URL (Optional, kept from previous code)
            else if (barcode.getValueType() == Barcode.TYPE_URL) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse(barcode.getUrl().getUrl()));
                startActivity(browserIntent);
            }
            // Case 3: Invalid Format
            else {
                Toast.makeText(this, "QR Code does not contain patient data.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onSuccessListener(List<Barcode> barcodes) {
        if (!barcodes.isEmpty()) {
            handler.removeCallbacks(timeoutRunnable); // QR code scanned, stop the timer bro

            Barcode barcode = barcodes.get(0); // Get the first barcode
            String rawValue = barcode.getRawValue();

            if (rawValue.trim().startsWith("{")) {
                Log.d("QRScanner", "JSON Data found: " + rawValue);

                // navigate back to MainActivity and pass the data
                Intent intent = new Intent(QRScanner.this, MainActivity.class);
                intent.putExtra("scanned_patient_json", rawValue);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish(); // Close the scanner
            } else {
                Toast.makeText(this, "Unknown QR format", Toast.LENGTH_SHORT).show();
                shutterBtn.setEnabled(true);
            }

            stopAnalysis();
        }
    }

    // stop the camera analysis
    private void stopAnalysis() {
        if (analysisUseCase != null && cameraProvider != null) {
            cameraProvider.unbind(analysisUseCase);
            analysisUseCase = null;
        }
    }




}