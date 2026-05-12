package com.agie2e.pages;

import com.agie2e.core.Config;
import com.agie2e.core.WaitFactory;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

/**
 * Page Object da Calculadora de Juros Compostos.
 * <p>A calculadora está num iframe externo (calculadora_juros_compostos_final_v4.html).
 * Esta page entra no iframe; quem usar deve estar ciente de que o frame muda o contexto
 * do driver.</p>
 * <p>Inputs usam JS handlers (formatarMoeda / formatarTaxa) que interpretam dígitos como
 * centavos. Para evitar flaky por sendKeys char-a-char, setamos os values via JavaScript
 * já formatados — o cálculo lê via desformatarMoeda, então isso é seguro.</p>
 */
public class CalculadoraJurosCompostosPage {

    private final WebDriver driver;
    private final WaitFactory waits;

    private static final By IFRAME =
            By.cssSelector("iframe[title='Calculadora de Juros Compostos']");

    private static final By CARD_INVESTIMENTO =
            By.cssSelector(".choice-card.investimento");

    private static final By FORM_INVESTIMENTO = By.id("formInvestimento");
    private static final By INPUT_VALOR_INICIAL = By.id("valorInicial");
    private static final By INPUT_VALOR_MENSAL = By.id("valorMensal");
    private static final By INPUT_TAXA = By.id("taxaJurosInvest");
    private static final By SELECT_TIPO_TAXA = By.id("tipoTaxaInvest");
    private static final By INPUT_PERIODO = By.id("periodoInvest");
    private static final By SELECT_TIPO_PERIODO = By.id("tipoPeriodoInvest");
    private static final By BOTAO_CALCULAR =
            By.xpath("//div[@id='formInvestimento']//button[normalize-space()='Calcular Agora']");

    private static final By RESULT_INVESTIMENTO = By.id("resultInvestimento");
    private static final By VALOR_FINAL = By.id("valorFinal");

    public CalculadoraJurosCompostosPage(WebDriver driver) {
        this.driver = driver;
        this.waits = new WaitFactory(driver);
    }

    public CalculadoraJurosCompostosPage acessar() {
        driver.get(Config.BLOG_BASE_URL + "/como-calcular-juros-compostos/");
        WebElement iframe = waits.visible(IFRAME);
        driver.switchTo().frame(iframe);
        waits.visible(CARD_INVESTIMENTO);
        return this;
    }

    public CalculadoraJurosCompostosPage escolherInvestimento() {
        waits.clickable(CARD_INVESTIMENTO).click();
        waits.until(d -> d.findElement(FORM_INVESTIMENTO).getAttribute("class").contains("active"));
        return this;
    }

    public CalculadoraJurosCompostosPage preencherInvestimento(BigDecimal capital,
                                                                BigDecimal aporteMensal,
                                                                BigDecimal taxaPercentual,
                                                                int periodo,
                                                                String tipoTaxa,
                                                                String tipoPeriodo) {
        setarValorMoeda(INPUT_VALOR_INICIAL, capital);
        setarValorMoeda(INPUT_VALOR_MENSAL, aporteMensal);
        setarValorTaxa(INPUT_TAXA, taxaPercentual);

        new Select(driver.findElement(SELECT_TIPO_TAXA)).selectByValue(tipoTaxa);

        WebElement periodoEl = driver.findElement(INPUT_PERIODO);
        periodoEl.clear();
        periodoEl.sendKeys(String.valueOf(periodo));

        new Select(driver.findElement(SELECT_TIPO_PERIODO)).selectByValue(tipoPeriodo);

        return this;
    }

    public CalculadoraJurosCompostosPage calcular() {
        waits.clickable(BOTAO_CALCULAR).click();
        waits.until(d -> d.findElement(RESULT_INVESTIMENTO).getAttribute("class").contains("active"));
        waits.visible(VALOR_FINAL);
        return this;
    }

    /** Lê o montante final exibido ("R$ X.XXX,XX") e converte para BigDecimal. */
    public BigDecimal montanteFinal() {
        String texto = waits.visible(VALOR_FINAL).getText();
        return parseMoedaBR(texto);
    }

    private void setarValorMoeda(By locator, BigDecimal valor) {
        String formatado = formatarMoedaBR(valor);
        ((JavascriptExecutor) driver).executeScript(
                "var el = document.querySelector(arguments[0]);"
                        + " el.value = arguments[1];",
                cssOf(locator), formatado);
    }

    private void setarValorTaxa(By locator, BigDecimal valor) {
        String formatado = valor.setScale(2).toPlainString().replace('.', ',');
        ((JavascriptExecutor) driver).executeScript(
                "var el = document.querySelector(arguments[0]);"
                        + " el.value = arguments[1];",
                cssOf(locator), formatado);
    }

    /** Helper: converte By.id("x") em selector CSS "#x". Funciona para id/css simples. */
    private static String cssOf(By locator) {
        String s = locator.toString();
        if (s.startsWith("By.id: ")) return "#" + s.substring("By.id: ".length());
        if (s.startsWith("By.cssSelector: ")) return s.substring("By.cssSelector: ".length());
        throw new IllegalArgumentException("By não suportado para cssOf: " + s);
    }

    private static String formatarMoedaBR(BigDecimal valor) {
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("pt", "BR"));
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        return nf.format(valor);
    }

    private static BigDecimal parseMoedaBR(String texto) {
        String limpo = texto.replace("R$", "").replace(".", "").replace(",", ".").trim();
        try {
            return new BigDecimal(limpo);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Não consegui parsear montante: " + texto, e);
        }
    }
}
