package com.project2.service;



import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * QRCodeService — Generates a real QR code PNG using Google's ZXing library.
 *
 * The QR code encodes registration info as plain text, which any
 * QR scanner app can read at the event for attendance verification.
 */
@Service
public class QRCodeService {

    private static final Logger log = LoggerFactory.getLogger(QRCodeService.class);

    private static final int QR_WIDTH  = 250;  // pixels
    private static final int QR_HEIGHT = 250;  // pixels

    /**
     * Generates a QR code and returns it as a Base64-encoded PNG string.
     * This can be embedded directly in an HTML email as:
     *   <img src="data:image/png;base64,{base64String}" />
     *
     * @param content  The text to encode in the QR code
     * @return         Base64-encoded PNG image string
     */
    public String generateQRCodeBase64(String content) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // High error correction
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 2); // Quiet zone (border)

            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE,
                    QR_WIDTH, QR_HEIGHT, hints);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

            byte[] pngBytes = outputStream.toByteArray();
            return Base64.getEncoder().encodeToString(pngBytes);

        } catch (WriterException | IOException e) {
            log.error("Failed to generate QR code for content: {}", content, e);
            // Return a fallback 1x1 transparent PNG as Base64 so the email still sends
            return "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==";
        }
    }

    /**
     * Builds the QR code content string from registration details.
     * Format is human-readable so any scanner can parse it.
     */
    public String buildQRContent(Long registrationId,
                                  String studentName,
                                  String studentEmail,
                                  String studentId,
                                  String eventName,
                                  String eventDate,
                                  String eventVenue) {
        return String.format(
            "=== CAMPUS EVENTS - REGISTRATION PASS ===\n" +
            "Registration ID : %d\n" +
            "Student Name    : %s\n" +
            "Student Email   : %s\n" +
            "Student ID      : %s\n" +
            "Event           : %s\n" +
            "Date            : %s\n" +
            "Venue           : %s\n" +
            "==========================================",
            registrationId,
            studentName,
            studentEmail,
            studentId,
            eventName,
            eventDate,
            eventVenue
        );
    }
}
