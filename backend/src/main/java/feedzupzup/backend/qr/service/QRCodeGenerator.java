package feedzupzup.backend.qr.service;

public interface QRCodeGenerator {

    byte[] generateQRCode(String url);
}
