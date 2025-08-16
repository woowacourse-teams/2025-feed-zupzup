package feedzupzup.backend.qr.service;

public interface QRImageGenerator {

    byte[] generateQRImage(String url);
}
