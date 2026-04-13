package lv.venta.fidi.config;

import java.io.IOException;
import java.util.List;

import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class LocalePathFilter extends OncePerRequestFilter {

    private static final List<String> LANGS = List.of("lv", "en");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (request.getDispatcherType() != DispatcherType.REQUEST) {
            filterChain.doFilter(request, response);
            return;
        }

        String contextPath = request.getContextPath();
        String relative = request.getRequestURI().substring(contextPath.length());

        for (String lang : LANGS) {
            String prefix = "/" + lang;
            if (relative.equals(prefix)) {
                request.setAttribute("LANG_PREFIX", prefix);
                request.setAttribute("APP_LANG", lang);
                forward(request, response, contextPath + "/");
                return;
            }
            if (relative.startsWith(prefix + "/")) {
                String rest = relative.substring(prefix.length());
                if (rest.isEmpty()) {
                    rest = "/";
                }
                request.setAttribute("LANG_PREFIX", prefix);
                request.setAttribute("APP_LANG", lang);
                forward(request, response, contextPath + rest);
                return;
            }
        }

        request.setAttribute("LANG_PREFIX", "");
        request.setAttribute("APP_LANG", "lv");
        filterChain.doFilter(request, response);
    }

    private static void forward(HttpServletRequest request, HttpServletResponse response, String target)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher(target);
        dispatcher.forward(request, response);
    }
}
