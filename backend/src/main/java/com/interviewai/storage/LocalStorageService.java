package com.interviewai.storage;

import com.interviewai.exception.FileStorageException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "app.storage.provider", havingValue = "local", matchIfMissing = false)
public class LocalStorageService implements StorageService {

    private final Path root = Paths.get("uploads");

    public LocalStorageService() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new FileStorageException("Could not create uploads directory", e);
        }
    }

    @Override
    public String store(MultipartFile file, String folder) {
        try {
            Path dir = root.resolve(folder);
            Files.createDirectories(dir);
            String ext = extractExtension(file.getOriginalFilename());
            String name = UUID.randomUUID() + ext;
            Path target = dir.resolve(name);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + folder + "/" + name;
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file locally", e);
        }
    }

    @Override
    public void delete(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) return;
        try {
            String relative = fileUrl.startsWith("/uploads/") ? fileUrl.substring("/uploads/".length()) : fileUrl;
            Files.deleteIfExists(root.resolve(relative));
        } catch (IOException e) {
            throw new FileStorageException("Failed to delete local file", e);
        }
    }

    @Override
    public String provider() {
        return "local";
    }

    private String extractExtension(String original) {
        if (original == null) return "";
        int dot = original.lastIndexOf('.');
        return dot >= 0 ? original.substring(dot) : "";
    }
}