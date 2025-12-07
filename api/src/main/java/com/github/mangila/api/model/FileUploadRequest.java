package com.github.mangila.api.model;

import java.nio.file.Path;

public record FileUploadRequest(Path path,
                                String originalFileName,
                                String contentType) {
}
