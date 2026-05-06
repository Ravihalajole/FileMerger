package org.ravi.filemerger;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private EditText extInput;
    private TextView tvSourcePath, tvOutputPath, tvStatus;
    private ProgressBar progressBar;
    private Button btnStart;

    private Uri sourceFolderUri;
    private Uri outputFileUri;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // 1. Force the UI to use the full screen and set Bar Colors to Black
    // This removes that stubborn grey/teal system tint
    getWindow().setStatusBarColor(android.graphics.Color.BLACK);
    getWindow().setNavigationBarColor(android.graphics.Color.BLACK);

    // 2. Ensure icons are white (Light Mode Status Bar = false)
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
        View decor = getWindow().getDecorView();
        // 0 clears the LIGHT_STATUS_BAR flag, keeping icons white
        decor.setSystemUiVisibility(0);
    }

    setContentView(R.layout.activity_main);

    // Initialize UI Elements
    extInput = findViewById(R.id.extInput);
    tvSourcePath = findViewById(R.id.tvSourcePath);
    tvOutputPath = findViewById(R.id.tvOutputPath);
    tvStatus = findViewById(R.id.tvStatus);
    progressBar = findViewById(R.id.progressBar);
    btnStart = findViewById(R.id.btnStart);

    // Folder Picker Launcher (Source)
    ActivityResultLauncher<Uri> folderPicker = registerForActivityResult(
            new ActivityResultContracts.OpenDocumentTree(),
            uri -> {
                if (uri != null) {
                    sourceFolderUri = uri;
                    // Persist permission so the app can keep reading this folder
                    getContentResolver().takePersistableUriPermission(uri, 
                            Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    
                    tvSourcePath.setText("Folder: " + uri.getLastPathSegment());
                }
            }
    );

    // File Creator Launcher (Destination)
    ActivityResultLauncher<String> fileCreator = registerForActivityResult(
            new ActivityResultContracts.CreateDocument("text/plain"),
            uri -> {
                if (uri != null) {
                    outputFileUri = uri;
                    tvOutputPath.setText("Output: " + uri.getLastPathSegment());
                }
            }
    );

    // Link the One UI Layout Containers to the Launchers
    findViewById(R.id.layoutSource).setOnClickListener(v -> folderPicker.launch(null));
    
    findViewById(R.id.layoutOutput).setOnClickListener(v -> 
            fileCreator.launch("merged_files.txt"));

    btnStart.setOnClickListener(v -> startMergingProcess());
}


    private void startMergingProcess() {
        String input = extInput.getText().toString().trim();
        
        if (input.isEmpty()) {
            Toast.makeText(this, "Enter extensions (e.g., java, txt)", Toast.LENGTH_SHORT).show();
            return;
        }
        if (sourceFolderUri == null || outputFileUri == null) {
            Toast.makeText(this, "Please select source and output paths", Toast.LENGTH_SHORT).show();
            return;
        }

        Set<String> extensions = new HashSet<>();
        for (String ext : input.split(",")) {
            String trimmed = ext.trim().toLowerCase();
            if (!trimmed.isEmpty()) extensions.add(trimmed);
        }

        // UI Feedback
        progressBar.setVisibility(View.VISIBLE);
        btnStart.setEnabled(false);
        tvStatus.setText("Merging files...");

        executorService.execute(() -> {
            FileProcessor.mergeFiles(this, sourceFolderUri, outputFileUri, extensions, new FileProcessor.ProgressListener() {
                @Override
                public void onProgress(String status) {
                    runOnUiThread(() -> tvStatus.setText(status));
                }

                @Override
                public void onFinished(String finalMessage) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        btnStart.setEnabled(true);
                        tvStatus.setText(finalMessage);
                        Toast.makeText(MainActivity.this, "Success!", Toast.LENGTH_LONG).show();
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        btnStart.setEnabled(true);
                        tvStatus.setText("Error: " + error);
                        Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
                    });
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
