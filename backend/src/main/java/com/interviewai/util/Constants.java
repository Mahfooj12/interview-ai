package com.interviewai.util;

public final class Constants {

    private Constants() {}

    public static final String ROLE_USER = "USER";
    public static final String ROLE_ADMIN = "ADMIN";

    public static final String TOKEN_TYPE_ACCESS = "ACCESS";
    public static final String TOKEN_TYPE_REFRESH = "REFRESH";

    public static final long MAX_FILE_SIZE_BYTES = 10L * 1024 * 1024; // 10 MB

    public static final String[] ALLOWED_RESUME_MIME_TYPES = {
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    };
}