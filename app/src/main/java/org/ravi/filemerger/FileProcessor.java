package org.ravi.filemerger;

import android.content.Context;
import android.net.Uri;
import androidx.documentfile.provider.DocumentFile;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Set;

public class FileProcessor {

    /**
     * Interface to communicate progress back to the MainActivity UI.
     */
    public interface ProgressListener {
        void onProgress(String status);
        void onFinished(String finalMessage);
        void onError(String error);
    }

    /**
     * Entry point for merging files.
     */
    public static void mergeFiles(Context context, Uri sourceTreeUri, Uri outputFileUri, 
                                  Set<String> extensions, ProgressListener listener) {
        
        // Convert the Tree Uri into a DocumentFile directory
        DocumentFile sourceDir = DocumentFile.fromTreeUri(context, sourceTreeUri);
        
        // Open the output stream for the specific file selected by the user
        try (OutputStream os = context.getContentResolver().openOutputStream(outputFileUri);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os))) {

            if (sourceDir != null && sourceDir.isDirectory()) {
                // Start recursive walking with an empty base path
                walkAndWrite(context, sourceDir, writer, extensions, "", listener);
                listener.onFinished("Merge completed successfully!");
            } else {
                listener.onError("Selected source is not a valid directory.");
            }

        } catch (IOException e) {
            listener.onError("IO Error: " + e.getMessage());
        } catch (Exception e) {
            listener.onError("Unexpected Error: " + e.getMessage());
        }
    }

    /**
     * Recursively walks through directories and writes matching files to the output.
     */
    private static void walkAndWrite(Context context, DocumentFile dir, BufferedWriter writer, 
                                     Set<String> extensions, String currentPath, 
                                     ProgressListener listener) throws IOException {
        
        DocumentFile[] files = dir.listFiles();

        for (DocumentFile file : files) {
            String name = file.getName();
            if (name == null) continue;

            // Construct the relative path for the separator (e.g., "src/main/java/App.java")
            String relativePath = currentPath.isEmpty() ? name : currentPath + "/" + name;

            if (file.isDirectory()) {
                // Recursively call for subdirectories
                walkAndWrite(context, file, writer, extensions, relativePath, listener);
            } else {
                // Process the file if it matches the extensions
                if (hasMatchingExtension(name, extensions)) {
                    listener.onProgress("Merging: " + relativePath);
                    
                    // --- ENHANCEMENT: MODERN SEPARATOR WITH PATH ---
                    writer.write("\n");
                    writer.write(" SOURCE: " + relativePath + "\n");
                   
                    
                    try (InputStream is = context.getContentResolver().openInputStream(file.getUri());
                         BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                        
                        String line;
                        while ((line = reader.readLine()) != null) {
                            writer.write(line);
                            writer.newLine();
                        }
                    }
                    
                    // Add spacing between files for readability
                    writer.write("\n\n");
                    writer.flush(); // Ensure data is written incrementally
                }
            }
        }
    }

    /**
     * Helper to check if file extension is in the allowed set.
     */
    private static boolean hasMatchingExtension(String fileName, Set<String> extensions) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) return false;

        String ext = fileName.substring(dotIndex + 1).toLowerCase();
        return extensions.contains(ext);
    }
}
