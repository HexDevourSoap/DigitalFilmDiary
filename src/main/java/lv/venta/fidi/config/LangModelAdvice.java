package lv.venta.fidi.config;

import java.util.List;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class LangModelAdvice {

    private static final List<String> LANGS = List.of("lv", "en");

    @ModelAttribute("langPrefix")
    public String langPrefix(HttpServletRequest request) {
        Object v = request.getAttribute("LANG_PREFIX");
        return v instanceof String s ? s : "";
    }

    @ModelAttribute("appLang")
    public String appLang(HttpServletRequest request) {
        Object v = request.getAttribute("APP_LANG");
        return v instanceof String s ? s : "lv";
    }

    @ModelAttribute("langUrlLv")
    public String langUrlLv(HttpServletRequest request) {
        return langSwitchUrl(request, "lv");
    }

    @ModelAttribute("langUrlEn")
    public String langUrlEn(HttpServletRequest request) {
        return langSwitchUrl(request, "en");
    }

    private static String langSwitchUrl(HttpServletRequest request, String lang) {
        String ctx = request.getContextPath();
        String tail = pathWithoutLang(relativeUri(request));
        if ("/".equals(tail)) {
            return ctx + "/" + lang;
        }
        return ctx + "/" + lang + tail;
    }

    private static String relativeUri(HttpServletRequest request) {
        String ctx = request.getContextPath();
        Object forward = request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
        String uri = forward instanceof String s ? s : request.getRequestURI();
        return uri.substring(ctx.length());
    }

    private static String pathWithoutLang(String relative) {
        for (String lang : LANGS) {
            String p = "/" + lang;
            if (relative.equals(p)) {
                return "/";
            }
            if (relative.startsWith(p + "/")) {
                return relative.substring(p.length());
            }
        }
        return relative.isEmpty() ? "/" : relative;
    }
}
