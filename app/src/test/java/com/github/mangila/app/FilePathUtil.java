package com.github.mangila.app;

import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FilePathUtil {

    /**
     * Read to get a JSON object from test/resources/json/***.json,
     * Gets a fake object that can be used for testing.
     * <br>
     * <b>Note:</b>
     * It's a good idea to re-use the same object as much as possible, during simple tests
     * This approach or just an in-memory object.
     */
    public static String readJsonFileToString(String filePath) throws IOException {
        if (!filePath.endsWith(".json")) {
            throw new IllegalArgumentException("File must be a json file");
        }
        File resource = new ClassPathResource(filePath).getFile();
        byte[] byteArray = Files.readAllBytes(resource.toPath());
        return new String(byteArray);
    }

}
