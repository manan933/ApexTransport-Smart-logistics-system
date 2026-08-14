package com.apextransport.service;


import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import com.apextransport.config.FirebaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.UUID;

@Service
public class StorageService {

    private static final Logger log = LoggerFactory.getLogger(StorageService.class);

    @Autowired
    private FirebaseConfig firebaseConfig;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    /**
     * Uploads a file to Firebase Storage if available, else stores locally.
     * 
     * @param file   The uploaded MultipartFile
     * @param folder Target folder prefix (e.g., "goods", "avatars", "banners",
     *               "qr")
     * @return Public accessible URL or relative web path
     */
    private static final java.util.Set<String> ALLOWED_EXTENSIONS = java.util.Set.of(".jpg", ".jpeg", ".png", ".webp",
            ".pdf");

    public String uploadFile(MultipartFile file, String folder) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "upload.jpg";
        String ext = "";
        int dotIdx = originalName.lastIndexOf('.');
        if (dotIdx >= 0) {
            ext = originalName.substring(dotIdx).toLowerCase();
        } else {
            ext = ".jpg";
        }

        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new IOException("Unsupported file type: " + ext);
        }
        String contentType = file.getContentType();
        if (contentType == null || !(contentType.startsWith("image/") || contentType.equals("application/pdf"))) {
            throw new IOException("Unsupported content type: " + contentType);
        }
        String uniqueFilename = folder + "/" + UUID.randomUUID() + ext;

        // Try Firebase Storage first
        if (firebaseConfig.isFirebaseInitialized() && firebaseConfig.getStorageBucket() != null
                && !firebaseConfig.getStorageBucket().isEmpty()) {
            try (InputStream is = file.getInputStream()) {
                Bucket bucket = StorageClient.getInstance().bucket();
                bucket.create(uniqueFilename, is, contentType);
                String publicUrl = String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                        bucket.getName(), uniqueFilename.replace("/", "%2F"));
                log.info("Uploaded file to Firebase Storage: {}", publicUrl);
                return publicUrl;
            } catch (Exception e) {
                log.warn("Firebase Storage upload failed: {}. Falling back to local storage.", e.getMessage());
            }
        }

        // Local Storage Fallback
        Path targetDir = Paths.get(uploadDir, folder);
        Files.createDirectories(targetDir);
        String simpleFilename = UUID.randomUUID() + ext;
        Path targetPath = targetDir.resolve(simpleFilename);
        try (InputStream is = file.getInputStream()) {
            Files.copy(is, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        return "/uploads/" + folder + "/" + simpleFilename;
    }

    /**
     * Uploads base64 encoded image (from camera capture).
     */
    public String uploadBase64Image(String base64Data, String folder) throws IOException {
        if (base64Data == null || base64Data.trim().isEmpty()) {
            return null;
        }

        String data = base64Data;
        String contentType = "image/jpeg";
        String ext = ".jpg";

        if (data.contains(",")) {
            String header = data.substring(0, data.indexOf(','));
            data = data.substring(data.indexOf(',') + 1);
            if (header.contains("image/png")) {
                contentType = "image/png";
                ext = ".png";
            } else if (header.contains("image/webp")) {
                contentType = "image/webp";
                ext = ".webp";
            }
        }

        byte[] imageBytes = Base64.getDecoder().decode(data);
        if (imageBytes.length > 25 * 1024 * 1024) {
            throw new IOException("Image too large");
        }

        String uniqueFilename = folder + "/" + UUID.randomUUID() + ext;

        // Try Firebase Storage
        if (firebaseConfig.isFirebaseInitialized() && firebaseConfig.getStorageBucket() != null
                && !firebaseConfig.getStorageBucket().isEmpty()) {
            try {
                Bucket bucket = StorageClient.getInstance().bucket();
                bucket.create(uniqueFilename, new ByteArrayInputStream(imageBytes), contentType);
                return String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                        bucket.getName(), uniqueFilename.replace("/", "%2F"));
            } catch (Exception e) {
                log.warn("Firebase Storage upload for base64 failed: {}. Falling back to local storage.",
                        e.getMessage());
            }
        }

        // Local Storage Fallback
        Path targetDir = Paths.get(uploadDir, folder);
        Files.createDirectories(targetDir);
        String simpleFilename = UUID.randomUUID() + ext;
        Path targetPath = targetDir.resolve(simpleFilename);
        Files.write(targetPath, imageBytes);

        return "/uploads/" + folder + "/" + simpleFilename;
    }
}
