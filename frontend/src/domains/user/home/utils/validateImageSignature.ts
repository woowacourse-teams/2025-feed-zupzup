export async function validateImageSignature(file: File): Promise<boolean> {
  const blob = file.slice(0, 12);
  // HEIC/HEIF/RAW 대비 12바이트 확보
  const buffer = await blob.arrayBuffer();
  const header = new Uint8Array(buffer);

  // PNG (89 50 4E 47)
  if (
    header[0] === 0x89 &&
    header[1] === 0x50 &&
    header[2] === 0x4e &&
    header[3] === 0x47
  ) {
    return true;
  }

  // JPEG (FF D8 FF)
  if (header[0] === 0xff && header[1] === 0xd8 && header[2] === 0xff) {
    return true;
  }

  // GIF87a / GIF89a: 47 49 46 38
  if (
    header[0] === 0x47 && // G
    header[1] === 0x49 && // I
    header[2] === 0x46 && // F
    header[3] === 0x38 // 8
  ) {
    return true;
  }

  // WebP (RIFF .... WEBP)
  // header[0..3] = 'RIFF', header[8..11] = 'WEBP'
  if (
    header[0] === 0x52 && // R
    header[1] === 0x49 && // I
    header[2] === 0x46 && // F
    header[3] === 0x46 && // F
    header[8] === 0x57 && // W
    header[9] === 0x45 && // E
    header[10] === 0x42 && // B
    header[11] === 0x50 // P
  ) {
    return true;
  }

  // TIFF (Little-endian: 49 49 2A 00)
  // TIFF (Big-endian:    4D 4D 00 2A)
  const isTIFFLittle =
    header[0] === 0x49 &&
    header[1] === 0x49 &&
    header[2] === 0x2a &&
    header[3] === 0x00;

  const isTIFFBig =
    header[0] === 0x4d &&
    header[1] === 0x4d &&
    header[2] === 0x00 &&
    header[3] === 0x2a;

  if (isTIFFLittle || isTIFFBig) {
    return true;
  }

  // BMP (42 4D)
  if (header[0] === 0x42 && header[1] === 0x4d) {
    return true;
  }

  // SVG -> 텍스트 기반이므로 매직넘버 없음
  // 보통 '<svg' 로 시작
  const textHeader = new TextDecoder().decode(buffer).trim().toLowerCase();
  if (textHeader.startsWith('<svg')) {
    return true;
  }

  // ICO (00 00 01 00)
  if (
    header[0] === 0x00 &&
    header[1] === 0x00 &&
    header[2] === 0x01 &&
    header[3] === 0x00
  ) {
    return true;
  }

  // PSD (38 42 50 53 = 8BPS)
  if (
    header[0] === 0x38 &&
    header[1] === 0x42 &&
    header[2] === 0x50 &&
    header[3] === 0x53
  ) {
    return true;
  }

  // HEIC / HEIF (ftyp brand)
  //
  // ASCII: 66 74 79 70 = 'ftyp'
  // MIME: image/heif, image/heic
  //
  // offset 4~7 위치에서 'ftyp'
  if (
    header[4] === 0x66 &&
    header[5] === 0x74 &&
    header[6] === 0x79 &&
    header[7] === 0x70
  ) {
    return true;
  }

  // RAW formats (manufacturer-specific)
  // 완벽 검증은 어렵지만 대표적 매직넘버 일부라도 체크

  // Canon CR2 : 49 49 2A 00 ... 'CR'
  if (isTIFFLittle && header[8] === 0x43 && header[9] === 0x52) {
    return true;
  }

  // Nikon NRW : TIFF-like 구조
  if (isTIFFLittle || isTIFFBig) {
    return true;
  }

  // Sony ARW : TIFF 기반
  if (isTIFFLittle || isTIFFBig) {
    return true;
  }

  return false;
}
