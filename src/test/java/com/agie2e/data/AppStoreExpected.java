package com.agie2e.data;

import java.util.regex.Pattern;

/**
 * Constantes esperadas para validação dos apps do Agibank nas lojas.
 * Razão social pode variar entre lojas (S.A., S/A, SA) — usamos regex tolerante.
 */
public final class AppStoreExpected {

    public static final String APP_STORE_HOST = "apps.apple.com";
    public static final String APP_STORE_APP_ID = "id1173498435";
    public static final String APP_STORE_URL_FRAGMENT = "/br/app/agibank";

    public static final String GOOGLE_PLAY_HOST = "play.google.com";
    public static final String GOOGLE_PLAY_PACKAGE = "br.com.agipag.app";

    /** Aceita "Agibank", "Banco Agibank S.A.", "Banco Agibank S/A", "Agibank SA" etc. */
    public static final Pattern DEVELOPER_PATTERN = Pattern.compile("(?i)agibank");

    public static final String REALISTIC_USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                    + "(KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36";

    private AppStoreExpected() {}
}
