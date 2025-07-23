package feedzupzup.backend.s3.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum S3ObjectType {

    IMAGE("png", "image/png");

    private final String extension;
    private final String contentType;
}
