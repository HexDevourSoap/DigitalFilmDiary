package lv.venta.fidi.config;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Builds redirect targets that preserve the {@code /lv} or {@code /en} prefix set by {@link LocalePathFilter}.
 */
public final class LocaleRedirectPaths {

    private LocaleRedirectPaths() {
    }

    public static String redirectDiary(HttpServletRequest request) {
        String prefix = langPrefix(request);
        return "redirect:" + prefix + "/diary";
    }

    private static String langPrefix(HttpServletRequest request) {
        Object v = request.getAttribute("LANG_PREFIX");
        return v instanceof String s ? s : "";
    }
}
