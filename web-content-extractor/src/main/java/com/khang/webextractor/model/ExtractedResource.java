package com.khang.webextractor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtractedResource {
    private String type; // IMAGE, VIDEO, SOUND, TEXT
    private String originalUrl;
    private String savedPath;
    private String filename;
    private String contentType;
    private long fileSize;
}
