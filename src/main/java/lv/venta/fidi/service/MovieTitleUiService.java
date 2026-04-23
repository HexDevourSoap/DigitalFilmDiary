package lv.venta.fidi.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lv.venta.fidi.dto.OmdbMovieDto;
import lv.venta.fidi.dto.OmdbSearchItemDto;
import lv.venta.fidi.model.Movie;


@Service
public class MovieTitleUiService {

    private final PlotTranslationService plotTranslationService;

    public MovieTitleUiService(PlotTranslationService plotTranslationService) {
        this.plotTranslationService = plotTranslationService;
    }

    private static boolean wantLatvianTitles(String appLang) {
        if (appLang == null || appLang.isBlank()) {
            return true;
        }
        return !"en".equalsIgnoreCase(appLang.trim());
    }

    
    public Map<String, String> mapLvTitlesByMovieId(String appLang, List<Movie> movies) {
        if (!wantLatvianTitles(appLang) || movies == null || movies.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> out = new HashMap<>();
        for (Movie m : movies) {
            if (m == null) {
                continue;
            }
            String t = m.getTitle();
            if (t == null || t.isBlank()) {
                continue;
            }
            String resolved = resolveTitleApiFirst(t.trim(), m.getImdbId());
            if (resolved != null && !resolved.isBlank()) {
                out.put(String.valueOf(m.getMovieId()), resolved);
            }
        }
        return out;
    }

    public Map<String, String> mapLvTitlesByImdbId(String appLang, List<OmdbSearchItemDto> items) {
        if (!wantLatvianTitles(appLang) || items == null || items.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> out = new HashMap<>();
        for (OmdbSearchItemDto item : items) {
            if (item == null) {
                continue;
            }
            String imdb = item.getImdbID();
            String t = item.getTitle();
            if (imdb == null || imdb.isBlank() || t == null || t.isBlank()) {
                continue;
            }
            String resolved = resolveTitleApiFirst(t.trim(), imdb);
            if (resolved != null && !resolved.isBlank()) {
                out.put(imdb, resolved);
            }
        }
        return out;
    }

    public String displayMovieTitle(String appLang, String englishTitle) {
        return displayMovieTitle(appLang, null, englishTitle);
    }

    public String displayMovieTitle(String appLang, String imdbId, String englishTitle) {
        if (englishTitle == null || englishTitle.isBlank()) {
            return englishTitle;
        }
        String t = englishTitle.trim();
        if (!wantLatvianTitles(appLang)) {
            return t;
        }
        return resolveTitleApiFirst(t, imdbId);
    }

    public void localizeOmdbTitle(String appLang, OmdbMovieDto dto) {
        if (dto == null || !wantLatvianTitles(appLang)) {
            return;
        }
        String t = dto.getTitle();
        if (t == null || t.isBlank()) {
            return;
        }
        dto.setTitle(resolveTitleApiFirst(t.trim(), dto.getImdbID()));
    }

    
    private String resolveTitleApiFirst(String englishTitle, String imdbId) {
        if (englishTitle == null || englishTitle.isBlank()) {
            return englishTitle;
        }
        String source = englishTitle.trim();
        String known = KnownMovieTitlesLv.titleOrNull(imdbId);
        if (known != null && !known.isBlank()) {
            return known;
        }
        String api = plotTranslationService.translateShortEnToLv(source);
        if (api == null || api.isBlank()) {
            return source;
        }
        String normalizedApi = api.trim();
        if (normalizedApi.equalsIgnoreCase(source) || looksApiErrorText(normalizedApi)) {
            return source;
        }
        return normalizedApi;
    }

    private static boolean looksApiErrorText(String value) {
        String s = value.toLowerCase();
        return s.contains("invalid email provided")
                || s.contains("mymemory warning")
                || s.contains("query length limit")
                || s.equals("?")
                || s.equals("??")
                || s.equals("???");
    }
}
