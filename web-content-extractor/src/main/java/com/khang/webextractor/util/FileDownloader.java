package com.khang.webextractor.util;

import com.khang.webextractor.model.ExtractedResource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@Slf4j
public class FileDownloader {

    public ExtractedResource downloadAndSave(
            String fileUrl,
            String resourceType,
            String domain,
            String baseUrl) throws IOException {

        // Remove Query parameters if present
        String urlString = URI.create(fileUrl).getPath();

        // Generate filename
        String extension = FilenameUtils.getExtension(urlString);
        if (extension == null || extension.isEmpty()) {
            extension = getDefaultExtension(resourceType);
        }

        String filename = generateFilename(fileUrl, resourceType, "." + extension);
        Path targetPath = createTargetPath(domain, resourceType, filename);

        // Download and save
        FileUtils.copyURLToFile(
                URI.create(fileUrl).toURL(),
                targetPath.toFile(),
                10000, // connection timeout
                10000 // read timeout
        );

        File file = targetPath.toFile();
        return ExtractedResource.builder()
                .originalUrl(fileUrl)
                .savedPath(targetPath.toString())
                .filename(filename)
                .contentType(detectContentType(extension))
                .fileSize(file.length())
                .build();
    }

    public ExtractedResource saveTextContent(
            String content,
            String domain,
            String baseUrl) throws IOException {
        String filename = generateFilename(baseUrl, "text", ".txt");
        Path targetPath = createTargetPath(domain, "text", filename);

        FileUtils.writeStringToFile(
                targetPath.toFile(),
                content,
                StandardCharsets.UTF_8);

        File file = targetPath.toFile();
        return ExtractedResource.builder()
                .originalUrl(baseUrl)
                .savedPath(targetPath.toString())
                .filename(filename)
                .contentType("text/plain")
                .fileSize(file.length())
                .build();
    }

    private Path createTargetPath(String domain, String resourceType, String filename) {
        Path path = Paths.get(domain, resourceType, filename);
        File targetDir = path.getParent().toFile();
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        return path;
    }

    private String generateFilename(String url, String type, String extension) {
        // Generate unique filename
        String baseName = url
                .replaceFirst("https?://", "")
                .replaceAll("[^a-zA-Z0-9]", "_");

        if (baseName.length() > 50) {
            baseName = baseName.substring(0, 50);
        }

        return baseName + "_" + type + "_" + System.currentTimeMillis() + extension;
    }

    private String getDefaultExtension(String resourceType) {
        switch (resourceType) {
            case "images":
                return "jpg";
            case "videos":
                return "mp4";
            case "audios":
                return "mp3";
            default:
                return "dat";
        }
    }

    private String detectContentType(String extension) {
        switch (extension.toLowerCase()) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "mp4":
                return "video/mp4";
            case "webm":
                return "video/webm";
            case "mp3":
                return "audio/mpeg";
            case "wav":
                return "audio/wav";
            case "txt":
                return "text/plain";
            default:
                return "application/octet-stream";
        }
    }
}
