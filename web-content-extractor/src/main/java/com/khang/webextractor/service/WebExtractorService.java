package com.khang.webextractor.service;

import com.khang.webextractor.model.ExtractedResource;
import com.khang.webextractor.model.ExtractionResponse;
import com.khang.webextractor.util.FileDownloader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebExtractorService {

    @Value("${storage.base-path}")
    private String baseStoragePath;

    private final FileDownloader fileDownloader;

    public ExtractionResponse extractContent(String url) {
        try {
            // Validate URL
            validateUrl(url);
            String domain = getDomain(url);

            // Create domain directory
            Path domainPath = Paths.get(baseStoragePath, domain);
            domainPath.toFile().mkdirs();

            // Connect and parse HTML
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(10000)
                    .get();

            // Extract resources
            List<ExtractedResource> resources = new ArrayList<>();
            resources.addAll(extractImages(doc, url, baseStoragePath + File.separatorChar + domain));
            resources.addAll(extractVideos(doc, url, baseStoragePath + File.separatorChar + domain));
            resources.addAll(extractAudio(doc, url, baseStoragePath + File.separatorChar + domain));
            resources.addAll(extractText(doc, url, baseStoragePath + File.separatorChar + domain));

            return ExtractionResponse.builder()
                    .success(true)
                    .message("Extracted " + resources.size() + " resources")
                    .totalFiles(resources.size())
                    .resources(resources)
                    .build();

        } catch (MalformedURLException e) {
            log.error("Invalid URL format: {}", url, e);
            return errorResponse("Invalid URL format");
        } catch (IOException e) {
            log.error("Failed to connect to URL: {}", url, e);
            return errorResponse("Failed to connect to the website");
        } catch (Exception e) {
            log.error("Unexpected error during extraction", e);
            return errorResponse("Unexpected error: " + e.getMessage());
        }
    }

    private List<ExtractedResource> extractImages(Document doc, String baseUrl, String domain) {
        List<ExtractedResource> images = new ArrayList<>();
        Elements imgElements = doc.select("img[src]");

        AtomicInteger counter = new AtomicInteger(1);
        for (Element img : imgElements) {
            String src = img.absUrl("src");
            if (StringUtils.hasText(src) && !src.startsWith("data:")) {
                try {
                    ExtractedResource resource = fileDownloader.downloadAndSave(
                            src,
                            "images",
                            domain,
                            baseUrl);
                    if (resource != null) {
                        resource.setType("IMAGE");
                        images.add(resource);
                    }
                } catch (Exception e) {
                    log.warn("Failed to download image: {}", src, e);
                }
            }
        }
        return images;
    }

    private List<ExtractedResource> extractVideos(Document doc, String baseUrl, String domain) {
        List<ExtractedResource> videos = new ArrayList<>();

        // Extract from <video> tags
        Elements videoElements = doc.select("video > source[src]");
        for (Element video : videoElements) {
            String src = video.absUrl("src");
            if (StringUtils.hasText(src)) {
                try {
                    ExtractedResource resource = fileDownloader.downloadAndSave(
                            src,
                            "video",
                            domain,
                            baseUrl);
                    if (resource != null) {
                        resource.setType("VIDEO");
                        videos.add(resource);
                    }
                } catch (Exception e) {
                    log.warn("Failed to download video: {}", src, e);
                }
            }
        }

        // Extract from <video src="...">
        videoElements = doc.select("video[src]");
        for (Element video : videoElements) {
            String src = video.absUrl("src");
            if (StringUtils.hasText(src)) {
                try {
                    ExtractedResource resource = fileDownloader.downloadAndSave(
                            src,
                            "videos",
                            domain,
                            baseUrl);
                    if (resource != null) {
                        resource.setType("VIDEO");
                        videos.add(resource);
                    }
                } catch (Exception e) {
                    log.warn("Failed to download video: {}", src, e);
                }
            }
        }

        return videos;
    }

    private List<ExtractedResource> extractAudio(Document doc, String baseUrl, String domain) {
        List<ExtractedResource> audios = new ArrayList<>();

        // Extract from <audio> tags
        Elements audioElements = doc.select("audio > source[src]");
        for (Element audio : audioElements) {
            String src = audio.absUrl("src");
            if (StringUtils.hasText(src)) {
                try {
                    ExtractedResource resource = fileDownloader.downloadAndSave(
                            src,
                            "sound",
                            domain,
                            baseUrl);
                    if (resource != null) {
                        resource.setType("SOUND");
                        audios.add(resource);
                    }
                } catch (Exception e) {
                    log.warn("Failed to download audio: {}", src, e);
                }
            }
        }

        // Extract from <audio src="...">
        audioElements = doc.select("audio[src]");
        for (Element audio : audioElements) {
            String src = audio.absUrl("src");
            if (StringUtils.hasText(src)) {
                try {
                    ExtractedResource resource = fileDownloader.downloadAndSave(
                            src,
                            "audios",
                            domain,
                            baseUrl);
                    if (resource != null) {
                        resource.setType("SOUND");
                        audios.add(resource);
                    }
                } catch (Exception e) {
                    log.warn("Failed to download audio: {}", src, e);
                }
            }
        }

        return audios;
    }

    private List<ExtractedResource> extractText(Document doc, String baseUrl, String domain) {
        Elements textElements = doc.select("p, h1, h2, h3, h4, h5, h6, div, span");
        StringBuilder content = new StringBuilder();

        for (Element el : textElements) {
            // Skip elements with no text or script/style elements
            if (!el.hasText() ||
                    "script".equals(el.tagName()) ||
                    "style".equals(el.tagName()) ||
                    el.className().contains("ad") ||
                    el.className().contains("banner")) {
                continue;
            }

            String text = el.text().trim();
            if (!text.isEmpty()) {
                content.append(text).append("\n\n");
            }
        }

        if (content.length() > 0) {
            try {
                ExtractedResource resource = fileDownloader.saveTextContent(
                        content.toString(),
                        domain,
                        baseUrl);
                resource.setType("TEXT");
                return Collections.singletonList(resource);
            } catch (IOException e) {
                log.error("Failed to save text content", e);
            }
        }

        return Collections.emptyList();
    }

    private void validateUrl(String url) throws MalformedURLException {
        try {
            URI.create(url).toURL(); // Will throw IllegalArgumentException or MalformedURLException if invalid
        } catch (IllegalArgumentException e) {
            throw new MalformedURLException("Invalid URL: " + url);
        }
    }

    private String getDomain(String url) throws MalformedURLException {
        URL uri = URI.create(url).toURL();
        String host = uri.getHost();
        return host != null ? host.replace("www.", "") : "unknown_domain";
    }

    private ExtractionResponse errorResponse(String message) {
        return ExtractionResponse.builder()
                .success(false)
                .message(message)
                .totalFiles(0)
                .resources(Collections.emptyList())
                .build();
    }
}
