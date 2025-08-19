package feedzupzup.backend.qr.dto.request;

import feedzupzup.backend.s3.dto.request.FileUploadRequest;

public record QRCodeUploadRequest(
        String extension,
        String objectDir,
        String objectId,
        byte[] fileData
) implements FileUploadRequest {

}
