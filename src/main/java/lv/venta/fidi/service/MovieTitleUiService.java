package lv.venta.fidi.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
        List<Movie> needMachineTranslation = new ArrayList<>();
        for (Movie m : movies) {
            if (m == null) {
                continue;
            }
            String known = KnownMovieTitlesLv.titleOrNull(m.getImdbId());
            if (known != null) {
                out.put(String.valueOf(m.getMovieId()), known);
            } else {
                needMachineTranslation.add(m);
            }
        }
        if (needMachineTranslation.isEmpty()) {
            return out;
        }
        LinkedHashMap<String, Long> firstMovieIdByTitle = new LinkedHashMap<>();
        for (Movie m : needMachineTranslation) {
            String t = m.getTitle();
            if (t == null || t.isBlank()) {
                continue;
            }
            firstMovieIdByTitle.putIfAbsent(t.trim(), m.getMovieId());
        }
        List<String> uniqueTitles = new ArrayList<>(firstMovieIdByTitle.keySet());
        List<String> translated = plotTranslationService.translateListEnToLv(uniqueTitles);
        for (int i = 0; i < uniqueTitles.size() && i < translated.size(); i++) {
            Long id = firstMovieIdByTitle.get(uniqueTitles.get(i));
            if (id != null) {
                out.put(String.valueOf(id), translated.get(i));
            }
        }
        return out;
    }

    public Map<String, String> mapLvTitlesByImdbId(String appLang, List<OmdbSearchItemDto> items) {
        if (!wantLatvianTitles(appLang) || items == null || items.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> out = new HashMap<>();
        List<OmdbSearchItemDto> needMt = new ArrayList<>();
        for (OmdbSearchItemDto item : items) {
            if (item == null) {
                continue;
            }
            String imdb = item.getImdbID();
            String known = KnownMovieTitlesLv.titleOrNull(imdb);
            if (known != null && imdb != null && !imdb.isBlank()) {
                out.put(imdb, known);
            } else {
                needMt.add(item);
            }
        }
        LinkedHashMap<String, String> uniqueTitlesInOrder = new LinkedHashMap<>();
        for (OmdbSearchItemDto item : needMt) {
            String t = item.getTitle();
            if (t != null && !t.isBlank()) {
                uniqueTitlesInOrder.putIfAbsent(t.trim(), t.trim());
            }
        }
        List<String> uniqueList = new ArrayList<>(uniqueTitlesInOrder.keySet());
        List<String> translated = plotTranslationService.translateListEnToLv(uniqueList);
        Map<String, String> titleToLv = new HashMap<>();
        for (int i = 0; i < uniqueList.size() && i < translated.size(); i++) {
            titleToLv.put(uniqueList.get(i), translated.get(i));
        }
        for (OmdbSearchItemDto item : needMt) {
            if (item == null) {
                continue;
            }
            String imdb = item.getImdbID();
            String t = item.getTitle();
            if (imdb == null || imdb.isBlank() || t == null || t.isBlank()) {
                continue;
            }
            if (out.containsKey(imdb)) {
                continue;
            }
            String lv = titleToLv.get(t.trim());
            if (lv != null) {
                out.put(imdb, lv);
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
        String known = KnownMovieTitlesLv.titleOrNull(imdbId);
        if (known != null) {
            return known;
        }
        return plotTranslationService.translateShortEnToLv(t);
    }

    public void localizeOmdbTitle(String appLang, OmdbMovieDto dto) {
        if (dto == null || !wantLatvianTitles(appLang)) {
            return;
        }
        String known = KnownMovieTitlesLv.titleOrNull(dto.getImdbID());
        if (known != null) {
            dto.setTitle(known);
            return;
        }
        String t = dto.getTitle();
        if (t == null || t.isBlank()) {
            return;
        }
        dto.setTitle(plotTranslationService.translateShortEnToLv(t.trim()));
    }
}
