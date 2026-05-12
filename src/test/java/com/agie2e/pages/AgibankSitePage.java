package com.agie2e.pages;

import com.agie2e.core.WaitFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Página de destino do CTA "Ir para o site": agibank.com.br
 * Site institucional do banco — validamos identidade básica, não fluxos internos.
 */
public class AgibankSitePage {

    private final WebDriver driver;
    private final WaitFactory waits;

    private static final By BODY = By.tagName("body");

    public AgibankSitePage(WebDriver driver) {
        this.driver = driver;
        this.waits = new WaitFactory(driver);
    }

    public AgibankSitePage aguardarCarregamento() {
        waits.urlContains("agibank.com.br");
        waits.visible(BODY);
        return this;
    }

    public String urlAtual() {
        return driver.getCurrentUrl();
    }

    public String titulo() {
        return driver.getTitle();
    }

    public String pageSource() {
        return driver.getPageSource();
    }

    public boolean titleContemAgibank() {
        String t = driver.getTitle();
        return t != null && t.toLowerCase().contains("agibank");
    }

    public boolean urlContemUtmDoBlog() {
        return driver.getCurrentUrl().toLowerCase().contains("utm_source=blog");
    }
}
