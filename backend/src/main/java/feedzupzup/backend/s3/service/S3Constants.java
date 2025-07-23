package feedzupzup.backend.s3.service;

import java.time.Duration;

public final class S3Constants {

    public static final String ROOT_DIR_NAME = "feed-zupzup";
    public static final Duration SIGNATURE_DURATION = Duration.ofMinutes(5);

    public static final String FEEDBACK_OBJECT_DIR = "feedback_media";
}
