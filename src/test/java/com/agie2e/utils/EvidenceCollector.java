package com.agie2e.utils;

import io.qameta.allure.Allure;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EvidenceCollector {

    private static final Logger log = LoggerFactory.getLogger(EvidenceCollector.class);

    private EvidenceCollector() {}

    public static void attach(WebDriver driver) {
        if (driver == null) return;
        attachScreenshot(driver);
        attachPageSource(driver);
        attachCurrentUrl(driver);
    }

    private static void attachScreenshot(WebDriver driver) {
        try {
            byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment("screenshot", "image/png", new ByteArrayInputStream(png), "png");
        } catch (Exception e) {
            log.warn("Falha ao capturar screenshot", e);
        }
    }

    private static void attachPageSource(WebDriver driver) {
        try {
            String html = driver.getPageSource();
            Allure.addAttachment("page-source", "text/html",
                    new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8)), "html");
        } catch (Exception e) {
            log.warn("Falha ao capturar page source", e);
        }
    }

    private static void attachCurrentUrl(WebDriver driver) {
        try {
            String url = driver.getCurrentUrl();
            Allure.addAttachment("current-url", "text/plain",
                    new ByteArrayInputStream(url.getBytes(StandardCharsets.UTF_8)), "txt");
        } catch (Exception e) {
            log.warn("Falha ao capturar URL atual", e);
        }
    }
}
