package lv.venta.fidi.config;

import jakarta.servlet.http.HttpServletRequest;


public final class LocaleRedirectPaths {

    private LocaleRedirectPaths() {
    }

    public static String redirectDiary(HttpServletRequest request) {
        String prefix = langPrefix(request);
        return "redirect:" + prefix + "/diary";
    }

    
    public static String redirectLogin(HttpServletRequest request, String queryWithoutQuestionMark) {
        String prefix = langPrefix(request);
        String q = (queryWithoutQuestionMark == null || queryWithoutQuestionMark.isBlank()) ? ""
                : "?" + queryWithoutQuestionMark;
        return "redirect:" + prefix + "/login" + q;
    }

    private static String langPrefix(HttpServletRequest request) {
        Object v = request.getAttribute("LANG_PREFIX");
        return v instanceof String s ? s : "";
    }
}
