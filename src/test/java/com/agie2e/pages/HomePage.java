package com.agie2e.pages;

import com.agie2e.components.FooterComponent;
import com.agie2e.components.HeaderComponent;
import com.agie2e.core.Config;
import com.agie2e.core.WaitFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Home do blog do Agibank: blog.agibank.com.br
 */
public class HomePage {

    private final WebDriver driver;
    private final WaitFactory waits;
    private final HeaderComponent header;
    private final FooterComponent footer;

    private static final By LOGO_BLOG =
            By.cssSelector("a[href$='blog.agibank.com.br/'], a[href='https://blog.agibank.com.br/']");

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.waits = new WaitFactory(driver);
        this.header = new HeaderComponent(driver);
        this.footer = new FooterComponent(driver);
    }

    public HomePage acessar() {
        driver.get(Config.BLOG_BASE_URL);
        return this;
    }

    public boolean carregouComSucesso() {
        try {
            waits.visible(LOGO_BLOG);
            return driver.getTitle() != null && !driver.getTitle().isBlank();
        } catch (Exception e) {
            return false;
        }
    }

    public String titulo() {
        return driver.getTitle();
    }

    public String urlAtual() {
        return driver.getCurrentUrl();
    }

    public HeaderComponent header() {
        return header;
    }

    public FooterComponent footer() {
        return footer;
    }
}
