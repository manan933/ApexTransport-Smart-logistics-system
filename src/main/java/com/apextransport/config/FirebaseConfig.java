package com.apextransport.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.config.path:}")
    private String configPath;

    @Value("${firebase.config.json:}")
    private String configJson;

    @Value("${firebase.storage.bucket:}")
    private String storageBucket;

    @Value("${firebase.project.id:apex-transport-demo}")
    private String projectId;

    private boolean firebaseInitialized = false;

    @PostConstruct
    public void initializeFirebase() {
        try {
            if (!FirebaseApp.getApps().isEmpty()) {
                firebaseInitialized = true;
                return;
            }

            InputStream serviceAccountStream = null;

            if (configJson != null && !configJson.trim().isEmpty()) {
                log.info("Initializing Firebase using FIREBASE_CONFIG_JSON environment variable");
                serviceAccountStream = new ByteArrayInputStream(configJson.getBytes(StandardCharsets.UTF_8));
            } else if (configPath != null && !configPath.trim().isEmpty()) {
                log.info("Initializing Firebase using file at: {}", configPath);
                serviceAccountStream = new FileInputStream(configPath);
            }

            if (serviceAccountStream != null) {
                try (InputStream is = serviceAccountStream) {
                    FirebaseOptions.Builder optionsBuilder = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(is))
                            .setProjectId(projectId);

                    if (storageBucket != null && !storageBucket.trim().isEmpty()) {
                        optionsBuilder.setStorageBucket(storageBucket);
                    }

                    FirebaseApp.initializeApp(optionsBuilder.build());
                    firebaseInitialized = true;
                    log.info("✅ Firebase Admin SDK initialized successfully for project: {}", projectId);
                }
            } else {
                log.warn("⚠️ No Firebase service account credentials found. Firebase Admin operations will use simulation/dev mode.");
            }
        } catch (Exception e) {
            log.error("Failed to initialize Firebase Admin SDK: {}. Running in local simulation mode.", e.getMessage());
            firebaseInitialized = false;
        }
    }

    public boolean isFirebaseInitialized() {
        return firebaseInitialized;
    }

    public String getStorageBucket() {
        return storageBucket;
    }

    public String getProjectId() {
        return projectId;
    }
}
