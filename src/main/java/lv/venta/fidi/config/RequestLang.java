package lv.venta.fidi.config;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;


public final class RequestLang {

    private RequestLang() {
    }

    public static String appLang(HttpServletRequest request) {
        if (request == null) {
            return "lv";
        }
        Object v = request.getAttribute("APP_LANG");
        if (v instanceof String s && !s.isBlank()) {
            return normalizeLang(s);
        }
        String fromPath = langFromPath(request.getRequestURI(), request.getContextPath());
        if (fromPath != null) {
            return fromPath;
        }
        Object forwardUri = request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
        if (forwardUri instanceof String fu) {
            String fromForward = langFromPath(fu, request.getContextPath());
            if (fromForward != null) {
                return fromForward;
            }
        }
        return "lv";
    }

    private static String normalizeLang(String raw) {
        return raw.trim().toLowerCase().split("-")[0];
    }

    private static String langFromPath(String uri, String contextPath) {
        if (uri == null || uri.isBlank()) {
            return null;
        }
        String rel = uri.startsWith(contextPath) ? uri.substring(contextPath.length()) : uri;
        if (rel.startsWith("/en/") || rel.equals("/en")) {
            return "en";
        }
        if (rel.startsWith("/lv/") || rel.equals("/lv")) {
            return "lv";
        }
        return null;
    }
}
