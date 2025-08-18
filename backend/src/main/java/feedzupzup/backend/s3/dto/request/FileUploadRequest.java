package feedzupzup.backend.s3.dto.request;

public interface FileUploadRequest {

    String extension();

    String objectDir();

    String objectId();

    byte[] fileData();
}
