package com.agie2e.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(FailureEvidenceExtension.class)
public abstract class BaseTest {

    private static final Logger log = LoggerFactory.getLogger(BaseTest.class);

    protected WebDriver driver;

    @BeforeEach
    void setUpDriver(TestInfo info) {
        log.info("Iniciando teste: {}", info.getDisplayName());
        driver = DriverFactory.create();
    }

    @AfterEach
    void tearDownDriver(TestInfo info) {
        if (driver != null) {
            log.info("Encerrando teste: {}", info.getDisplayName());
            driver.quit();
            driver = null;
        }
    }

    public WebDriver getDriver() {
        return driver;
    }
}
