package com.agie2e.components;

import com.agie2e.core.WaitFactory;
import com.agie2e.pages.AgibankSitePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Header do blog do Agibank.
 * Site de terceiro: seletores baseados em href + texto (estáveis), revisar trimestralmente.
 */
public class HeaderComponent {

    private final WebDriver driver;
    private final WaitFactory waits;

    private static final By IR_PARA_O_SITE =
            By.xpath("//a[contains(@href, 'agibank.com.br') and normalize-space()='Ir para o site']");

    public HeaderComponent(WebDriver driver) {
        this.driver = driver;
        this.waits = new WaitFactory(driver);
    }

    public boolean exibeLinkIrParaSite() {
        try {
            return waits.visible(IR_PARA_O_SITE).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String hrefIrParaSite() {
        WebElement link = waits.visible(IR_PARA_O_SITE);
        return link.getAttribute("href");
    }

    public AgibankSitePage clicarIrParaSite() {
        WebElement link = waits.clickable(IR_PARA_O_SITE);
        link.click();
        return new AgibankSitePage(driver);
    }
}
