package com.agie2e.tests;

import static org.assertj.core.api.Assertions.assertThat;

import com.agie2e.clients.AppStoreClient;
import com.agie2e.clients.GooglePlayClient;
import com.agie2e.core.BaseTest;
import com.agie2e.data.AppStoreExpected;
import com.agie2e.pages.HomePage;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Epic("Funil de conversão")
@Feature("CTA Faça o Download")
public class DownloadAppTest extends BaseTest {

    @Test
    @Story("Badges visíveis no rodapé")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Badges App Store e Google Play devem estar visíveis e apontar para os apps do Agibank")
    @Description("Garante que os CTAs de download dos apps estão presentes no rodapé da home e que "
            + "os hrefs apontam para os apps oficiais (appId iOS e package Android do Agibank).")
    void badgesDeDownloadDevemApontarParaAppsDoAgibank() {
        HomePage home = new HomePage(driver).acessar();
        home.footer().rolarAteOFooter();

        assertThat(home.footer().exibeBadgeAppStore())
                .as("badge App Store deve estar visível no footer")
                .isTrue();
        assertThat(home.footer().exibeBadgeGooglePlay())
                .as("badge Google Play deve estar visível no footer")
                .isTrue();

        String appStoreHref = home.footer().hrefAppStore();
        String googlePlayHref = home.footer().hrefGooglePlay();

        assertThat(appStoreHref)
                .as("href App Store deve apontar para o app do Agibank")
                .contains(AppStoreExpected.APP_STORE_HOST)
                .contains(AppStoreExpected.APP_STORE_URL_FRAGMENT)
                .contains(AppStoreExpected.APP_STORE_APP_ID);

        assertThat(googlePlayHref)
                .as("href Google Play deve apontar para o package do Agibank")
                .contains(AppStoreExpected.GOOGLE_PLAY_HOST)
                .contains(AppStoreExpected.GOOGLE_PLAY_PACKAGE);
    }

    @Test
    @Story("Lojas respondem e identificam o Agibank")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Páginas dos apps na App Store e Google Play devem responder 200 e mencionar Agibank")
    @Description("Após pegar os hrefs dos badges, faz GET nas duas lojas com User-Agent realista "
            + "e valida status 200 + presença do nome 'Agibank' no HTML (cobertura Opção B).")
    void lojasDevemResponder200EMencionarAgibank() {
        HomePage home = new HomePage(driver).acessar();
        home.footer().rolarAteOFooter();

        String appStoreUrl = home.footer().hrefAppStore();
        String googlePlayUrl = home.footer().hrefGooglePlay();

        Response appStoreResp = AppStoreClient.get(appStoreUrl);
        Response googlePlayResp = GooglePlayClient.get(googlePlayUrl);

        assertThat(appStoreResp.statusCode())
                .as("App Store deve responder 200 para %s", appStoreUrl)
                .isEqualTo(200);
        assertThat(AppStoreClient.corpoMencionaAgibank(appStoreResp))
                .as("HTML da App Store deve mencionar 'Agibank'")
                .isTrue();

        assertThat(googlePlayResp.statusCode())
                .as("Google Play deve responder 200 para %s", googlePlayUrl)
                .isEqualTo(200);
        assertThat(GooglePlayClient.corpoMencionaAgibank(googlePlayResp))
                .as("HTML do Google Play deve mencionar 'Agibank'")
                .isTrue();
    }
}
