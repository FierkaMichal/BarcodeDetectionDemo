package com.fierka.marksoft;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.camera.core.*;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import com.fierka.marksoft.barcode.BarcodeService;
import com.fierka.marksoft.email.EmailSender;
import com.fierka.marksoft.printer.FilePrinterService;
import com.google.common.util.concurrent.ListenableFuture;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private Context context;

    private EditText emailReceiverEdit;
    private EditText emailMessageEdit;

    private EditText emailHost;
    private EditText emailPort;
    private EditText emailAddress;
    private EditText emailPassword;

    private TextView barcodeTextView;

    private PreviewView cameraPreview;
    private ImageCapture imageCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = this.getBaseContext();
        initView();

        startCamera();
    }

    private void initView() {
        this.emailReceiverEdit = findViewById(R.id.email_receiver);
        this.emailMessageEdit = findViewById(R.id.email_message);
        this.emailHost = findViewById(R.id.email_host);
        this.emailPort = findViewById(R.id.email_port);
        this.emailAddress = findViewById(R.id.email_sender);
        this.emailPassword = findViewById(R.id.email_password);
        this.cameraPreview = findViewById(R.id.cameraPreview);
        this.barcodeTextView = findViewById(R.id.barcode);
    }

    public void onSendMailClick(View view) {
        String host = this.emailHost.getText().toString();
        int port = Integer.parseInt(this.emailPort.getText().toString());
        String email = this.emailAddress.getText().toString();
        String password = this.emailPassword.getText().toString();

        final EmailSender emailSender = new EmailSender(host, port, email, password, context);

        new Thread(() -> emailSender.sendMail("Subject title",
                emailMessageEdit.getText().toString(),
                emailReceiverEdit.getText().toString(),
                "attachment.jpg")).start();
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(cameraPreview.getSurfaceProvider());

        imageCapture = new ImageCapture.Builder().build();

        cameraProvider.bindToLifecycle(this, cameraSelector, imageCapture, preview);
    }

    public void onScanCodeClick(View view) {
        imageCapture.takePicture(Runnable::run, new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull @NotNull ImageProxy image) {
                barcodeTextView.setText("");
                BarcodeService barcodeService = new BarcodeService();
                barcodeService.analyze(image, barcodeTextView);
                image.close();
            }

            @Override
            public void onError(@NonNull @NotNull ImageCaptureException exception) {
                Toast.makeText(context, exception.toString(), 2000);
            }
        });
    }

    public void onPrintClick(View view) {
        FilePrinterService printer = new FilePrinterService();
        printer.print(this.context);
    }
}