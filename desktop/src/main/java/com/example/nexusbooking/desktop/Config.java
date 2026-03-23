package com.example.nexusbooking.desktop;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

public class Config {

    private static final String FILE_NAME = "config.properties";
    private static final String DEFAULT_URL = "http://localhost:8080";

    private static final Properties props = new Properties();

    static {
        // 1. Look for config.properties at the repo root (../../config.properties relative to desktop/)
        Path repoRoot = Paths.get("..").resolve(FILE_NAME).normalize();
        // 2. Also check next to the running JAR / working directory
        Path localConfig = Paths.get(FILE_NAME);

        Path externalConfig = Files.exists(repoRoot) ? repoRoot
                            : Files.exists(localConfig) ? localConfig
                            : null;

        if (externalConfig != null) {
            try (InputStream in = Files.newInputStream(externalConfig)) {
                props.load(in);
            } catch (IOException e) {
                System.err.println("Warning: could not read " + externalConfig + ": " + e.getMessage());
            }
        } else {
            // 2. Fall back to the one bundled inside the JAR
            try (InputStream in = Config.class.getResourceAsStream("/config.properties")) {
                if (in != null) props.load(in);
            } catch (IOException e) {
                System.err.println("Warning: could not read bundled config.properties");
            }

            // 3. Write a default config.properties next to the JAR so the user can edit it
            try (OutputStream out = Files.newOutputStream(externalConfig)) {
                Properties defaults = new Properties();
                defaults.setProperty("api.base.url", DEFAULT_URL);
                defaults.store(out, "NexusBooking Desktop Configuration");
            } catch (IOException e) {
                System.err.println("Warning: could not write default " + FILE_NAME);
            }
        }
    }

    public static String getBaseUrl() {
        return props.getProperty("api.base.url", DEFAULT_URL);
    }
}
