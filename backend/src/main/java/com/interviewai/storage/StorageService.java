package com.interviewai.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    /**
     * Store a file and return its public/accessible URL.
     *
     * @param file   the uploaded file
     * @param folder logical folder (e.g. "resumes", "job-descriptions")
     * @return URL of the stored file
     */
    String store(MultipartFile file, String folder);

    /**
     * Delete a previously stored file by its URL or public id.
     */
    void delete(String fileUrl);

    /**
     * Provider name (cloudinary | local).
     */
    String provider();
}
