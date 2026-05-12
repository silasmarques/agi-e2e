package com.agie2e.pages;

import com.agie2e.core.Config;
import com.agie2e.core.WaitFactory;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Page Object da Calculadora de Dias Úteis.
 * <p>Roda dentro de iframe externo (calculadora_dias_uteis_final_v4.html).
 * Datas (<code>&lt;input type="date"&gt;</code>) são setadas via JavaScriptExecutor em
 * formato ISO (<code>yyyy-MM-dd</code>) para evitar problemas de locale do navegador.</p>
 */
public class CalculadoraDiasUteisPage {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE;

    private final WebDriver driver;
    private final WaitFactory waits;

    private static final By IFRAME =
            By.cssSelector("iframe[title='Calculadora de Dias Úteis']");

    private static final By FORM_SCREEN = By.id("formScreen");
    private static final By INPUT_DATA_INICIO = By.id("dataInicio");
    private static final By INPUT_DATA_FIM = By.id("dataFim");
    private static final By CHK_SABADO = By.id("incluirSabado");
    private static final By CHK_DOMINGO = By.id("incluirDomingo");
    private static final By BOTAO_CALCULAR =
            By.xpath("//div[@id='formScreen']//button[normalize-space()='Calcular Agora']");

    private static final By RESULT_SCREEN = By.id("resultScreen");
    private static final By DIAS_UTEIS = By.id("diasUteis");
    private static final By ERRO = By.id("erro");

    public CalculadoraDiasUteisPage(WebDriver driver) {
        this.driver = driver;
        this.waits = new WaitFactory(driver);
    }

    public CalculadoraDiasUteisPage acessar() {
        driver.get(Config.BLOG_BASE_URL + "/calculadora-dias-uteis/");
        WebElement iframe = waits.visible(IFRAME);
        driver.switchTo().frame(iframe);
        waits.visible(FORM_SCREEN);
        return this;
    }

    public CalculadoraDiasUteisPage preencherDataInicio(LocalDate data) {
        setarInputDate(INPUT_DATA_INICIO, data);
        return this;
    }

    public CalculadoraDiasUteisPage preencherDataFim(LocalDate data) {
        setarInputDate(INPUT_DATA_FIM, data);
        return this;
    }

    public CalculadoraDiasUteisPage marcarIncluirSabado(boolean desejado) {
        garantirCheckbox(CHK_SABADO, desejado);
        return this;
    }

    public CalculadoraDiasUteisPage marcarIncluirDomingo(boolean desejado) {
        garantirCheckbox(CHK_DOMINGO, desejado);
        return this;
    }

    public CalculadoraDiasUteisPage calcular() {
        waits.clickable(BOTAO_CALCULAR).click();
        waits.until(d -> d.findElement(RESULT_SCREEN).getAttribute("class").contains("active"));
        waits.visible(DIAS_UTEIS);
        return this;
    }

    public CalculadoraDiasUteisPage clicarCalcularEsperandoErro() {
        waits.clickable(BOTAO_CALCULAR).click();
        WaitFactory shortWait = new WaitFactory(driver, Duration.ofSeconds(5));
        shortWait.until(d -> {
            WebElement el = d.findElement(ERRO);
            return el.isDisplayed() && !el.getText().isBlank();
        });
        return this;
    }

    public long diasUteis() {
        String texto = waits.visible(DIAS_UTEIS).getText().trim();
        try {
            return Long.parseLong(texto);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Resultado não numérico: '" + texto + "'", e);
        }
    }

    public String mensagemDeErro() {
        return driver.findElement(ERRO).getText().trim();
    }

    private void setarInputDate(By locator, LocalDate data) {
        WebElement input = waits.visible(locator);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1];"
                        + " arguments[0].dispatchEvent(new Event('change', {bubbles:true}));",
                input, data.format(ISO));
    }

    private void garantirCheckbox(By locator, boolean desejado) {
        WebElement cb = driver.findElement(locator);
        if (cb.isSelected() != desejado) {
            // o input pode estar visualmente escondido pelo CSS; clicamos via JS para evitar
            // ElementNotInteractableException em headless
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", cb);
        }
    }
}
