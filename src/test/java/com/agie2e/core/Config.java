package com.agie2e.core;

import java.time.Duration;

public final class Config {

    public static final String BLOG_BASE_URL = read("BLOG_BASE_URL", "https://blog.agibank.com.br");
    public static final String SITE_BASE_URL = read("SITE_BASE_URL", "https://agibank.com.br");

    public static final Duration DEFAULT_WAIT = Duration.ofSeconds(readInt("DEFAULT_WAIT_SECONDS", 10));
    public static final Duration PAGE_LOAD_TIMEOUT = Duration.ofSeconds(readInt("PAGE_LOAD_TIMEOUT_SECONDS", 30));

    public static final boolean HEADLESS = Boolean.parseBoolean(read("headless", "false"));

    private Config() {}

    private static String read(String key, String fallback) {
        String value = System.getProperty(key);
        if (value != null && !value.isBlank()) return value;
        value = System.getenv(key);
        if (value != null && !value.isBlank()) return value;
        return fallback;
    }

    private static int readInt(String key, int fallback) {
        try {
            return Integer.parseInt(read(key, String.valueOf(fallback)).trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
