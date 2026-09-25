package com.crs.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class GmailApiUtil {

    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String SEND_URL = "https://gmail.googleapis.com/gmail/v1/users/me/messages/send";

    private GmailApiUtil() {
    }

    public static String refreshAccessToken() {
        try {
            HttpURLConnection connection = openConnection(TOKEN_URL, "POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String parameters =
                    "client_id=" + encode(requireConfiguration("CRS_GMAIL_CLIENT_ID"))
                    + "&client_secret=" + encode(requireConfiguration("CRS_GMAIL_CLIENT_SECRET"))
                    + "&refresh_token=" + encode(requireConfiguration("CRS_GMAIL_REFRESH_TOKEN"))
                    + "&grant_type=refresh_token";

            try (OutputStream output = connection.getOutputStream()) {
                output.write(parameters.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = connection.getResponseCode();
            String response = readResponse(connection, responseCode);
            if (responseCode < 200 || responseCode >= 300) {
                System.err.println("Gmail token refresh failed with HTTP " + responseCode + ".");
                return null;
            }
            return extractJsonValue(response, "access_token");
        } catch (Exception exception) {
            System.err.println("Gmail token refresh failed: " + exception.getMessage());
            return null;
        }
    }

    public static String sendPlainTextEmail(String toEmail, String subject, String bodyText) {
        return sendEmail(toEmail, subject, bodyText, false);
    }

    public static String sendHtmlEmail(String toEmail, String subject, String htmlBody) {
        return sendEmail(toEmail, subject, htmlBody, true);
    }

    public static String sendOtpEmail(String toEmail, String subject, String bodyText) {
        return sendPlainTextEmail(toEmail, subject, bodyText);
    }

    private static String sendEmail(String toEmail, String subject, String body, boolean html) {
        try {
            String accessToken = refreshAccessToken();
            if (accessToken == null || accessToken.isBlank()) {
                return "FAILED";
            }

            String mimeMessage =
                    "To: " + safeHeader(toEmail) + "\r\n"
                    + "Subject: " + safeHeader(subject) + "\r\n"
                    + "MIME-Version: 1.0\r\n"
                    + "Content-Type: " + (html ? "text/html" : "text/plain")
                    + "; charset=UTF-8\r\n\r\n"
                    + (body == null ? "" : body);

            String encodedMessage = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(mimeMessage.getBytes(StandardCharsets.UTF_8));
            String jsonPayload = "{ \"raw\": \"" + encodedMessage + "\" }";

            HttpURLConnection connection = openConnection(SEND_URL, "POST");
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            try (OutputStream output = connection.getOutputStream()) {
                output.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = connection.getResponseCode();
            readResponse(connection, responseCode);
            if (responseCode >= 200 && responseCode < 300) {
                return "SUCCESS";
            }
            System.err.println("Gmail send failed with HTTP " + responseCode + ".");
            return "FAILED";
        } catch (Exception exception) {
            System.err.println("Gmail send failed: " + exception.getMessage());
            return "FAILED";
        }
    }

    private static HttpURLConnection openConnection(String endpoint, String method) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(endpoint).openConnection();
        connection.setRequestMethod(method);
        connection.setDoOutput(true);
        connection.setConnectTimeout(10_000);
        connection.setReadTimeout(20_000);
        return connection;
    }

    private static String readResponse(HttpURLConnection connection, int responseCode) throws Exception {
        InputStream stream = responseCode >= 200 && responseCode < 300
                ? connection.getInputStream()
                : connection.getErrorStream();
        if (stream == null) {
            return "";
        }
        try (stream) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static String requireConfiguration(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            value = System.getProperty(name);
        }
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required configuration: " + name);
        }
        return value;
    }

    private static String safeHeader(String value) {
        return value == null ? "" : value.replace("\r", "").replace("\n", "");
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static String extractJsonValue(String json, String key) {
        String pattern = "\"" + key + "\":";
        int start = json.indexOf(pattern);
        if (start == -1) {
            return null;
        }
        start = json.indexOf('"', start + pattern.length());
        if (start == -1) {
            return null;
        }
        int end = json.indexOf('"', start + 1);
        return end == -1 ? null : json.substring(start + 1, end);
    }
}
