package com.khang.webextractor.controller;

import com.khang.webextractor.model.ExtractionResponse;
import com.khang.webextractor.service.WebExtractorService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/extract")
@RequiredArgsConstructor
@Validated
public class ExtractionController {

    private final WebExtractorService extractorService;

    @GetMapping
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Web Extractor Service is running");
    }

    @PostMapping
    @Operation(summary = "Extract content from a given URL")
    public ResponseEntity<ExtractionResponse> extractContent(
            @Valid @RequestBody Map<String, String> request) {
        String url = request.get("url");
        if (url == null || url.isBlank()) {
            return ResponseEntity.badRequest().body(
                    ExtractionResponse.builder()
                            .success(false)
                            .message("URL is required")
                            .build());
        }

        ExtractionResponse response = extractorService.extractContent(url);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
