package com.agie2e.components;

import com.agie2e.core.WaitFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Footer do blog do Agibank — onde aparecem os badges de download dos apps.
 * Site de terceiro: seletores baseados em href (atributo estável), revisar trimestralmente.
 */
public class FooterComponent {

    private final WebDriver driver;
    private final WaitFactory waits;

    private static final By LINK_APP_STORE =
            By.cssSelector("a[href*='apps.apple.com']");

    private static final By LINK_GOOGLE_PLAY =
            By.cssSelector("a[href*='play.google.com']");

    public FooterComponent(WebDriver driver) {
        this.driver = driver;
        this.waits = new WaitFactory(driver);
    }

    public FooterComponent rolarAteOFooter() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
        return this;
    }

    public boolean exibeBadgeAppStore() {
        return existeElementoVisivel(LINK_APP_STORE);
    }

    public boolean exibeBadgeGooglePlay() {
        return existeElementoVisivel(LINK_GOOGLE_PLAY);
    }

    public String hrefAppStore() {
        WebElement link = waits.visible(LINK_APP_STORE);
        return link.getAttribute("href");
    }

    public String hrefGooglePlay() {
        WebElement link = waits.visible(LINK_GOOGLE_PLAY);
        return link.getAttribute("href");
    }

    private boolean existeElementoVisivel(By locator) {
        try {
            return waits.visible(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
