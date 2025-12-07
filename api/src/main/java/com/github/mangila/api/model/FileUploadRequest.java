package com.github.mangila.api.model;

import java.nio.file.Path;

public record FileUploadRequest(Path path,
                                String fileId,
                                String originalFileName,
                                String contentType) {
}
