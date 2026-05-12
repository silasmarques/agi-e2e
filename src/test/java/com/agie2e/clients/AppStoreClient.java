package com.agie2e.clients;

import static io.restassured.RestAssured.given;

import com.agie2e.data.AppStoreExpected;
import io.restassured.response.Response;

public final class AppStoreClient {

    private AppStoreClient() {}

    /**
     * Faz GET na URL da App Store usando User-Agent realista (anti-bot tolerance).
     */
    public static Response get(String url) {
        return given()
                .header("User-Agent", AppStoreExpected.REALISTIC_USER_AGENT)
                .header("Accept-Language", "pt-BR,pt;q=0.9,en;q=0.8")
                .redirects().follow(true)
                .when()
                .get(url);
    }

    /**
     * True se o corpo HTML contém alguma menção a "Agibank" (case insensitive).
     */
    public static boolean corpoMencionaAgibank(Response response) {
        String body = response.getBody().asString();
        return body != null && AppStoreExpected.DEVELOPER_PATTERN.matcher(body).find();
    }
}
