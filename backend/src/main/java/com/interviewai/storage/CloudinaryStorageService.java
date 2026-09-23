package com.interviewai.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.interviewai.exception.FileStorageException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@ConditionalOnProperty(name = "app.storage.provider", havingValue = "cloudinary")
public class CloudinaryStorageService implements StorageService {

    private final Cloudinary cloudinary;

    public CloudinaryStorageService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String store(MultipartFile file, String folder) {
        try {
            Map<String, Object> options = ObjectUtils.asMap(
                    "folder", "interviewai/" + folder,
                    "resource_type", "auto",
                    "use_filename", true,
                    "unique_filename", true,
                    "overwrite", false
            );
            Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(), options);
            Object secureUrl = result.get("secure_url");
            if (secureUrl == null) {
                throw new FileStorageException("Cloudinary did not return a secure URL");
            }
            return secureUrl.toString();
        } catch (IOException e) {
            throw new FileStorageException("Failed to upload file to Cloudinary", e);
        }
    }

    @Override
    //@SuppressWarnings("unchecked")
    public void delete(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) return;
        try {
            String publicId = extractPublicId(fileUrl);
            if (publicId == null) return;
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            // Non-fatal — log via caller
            throw new FileStorageException("Failed to delete Cloudinary file", e);
        }
    }

    @Override
    public String provider() {
        return "cloudinary";
    }

    /**
     * Extract the public id from a Cloudinary URL.
     * Example: https://res.cloudinary.com/demo/image/upload/v123/interviewai/resumes/file.pdf
     * → interviewai/resumes/file
     */
    private String extractPublicId(String url) {
        int uploadIdx = url.indexOf("/upload/");
        if (uploadIdx < 0) return null;
        String after = url.substring(uploadIdx + "/upload/".length());
        // strip version segment v12345/
        if (after.startsWith("v")) {
            int slash = after.indexOf('/');
            if (slash > 0) after = after.substring(slash + 1);
        }
        int dot = after.lastIndexOf('.');
        if (dot > 0) after = after.substring(0, dot);
        return after;
    }
}