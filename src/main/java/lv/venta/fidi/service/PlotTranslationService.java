package lv.venta.fidi.service;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PlotTranslationService {

    
    private static final int TITLE_CACHE_CAP = 6_000;
    private final ConcurrentHashMap<String, String> shortEnToLvCache = new ConcurrentHashMap<>();

    private static final int MAX_INPUT = 12_000;
    private static final int CHUNK = 420;

    
    private static final Map<String, String> GENRE_EN_TO_LV = new HashMap<>();

    static {
        GENRE_EN_TO_LV.put("action", "Asa sižeta");
        GENRE_EN_TO_LV.put("adventure", "Piedzīvojumu");
        GENRE_EN_TO_LV.put("animation", "Animācija");
        GENRE_EN_TO_LV.put("biography", "Biogrāfija");
        GENRE_EN_TO_LV.put("comedy", "Komēdija");
        GENRE_EN_TO_LV.put("crime", "Noziegums");
        GENRE_EN_TO_LV.put("documentary", "Dokumentālā");
        GENRE_EN_TO_LV.put("drama", "Drāma");
        GENRE_EN_TO_LV.put("family", "Ģimenes");
        GENRE_EN_TO_LV.put("fantasy", "Fantāzija");
        GENRE_EN_TO_LV.put("film-noir", "Neonoira");
        GENRE_EN_TO_LV.put("history", "Vēsturiskā");
        GENRE_EN_TO_LV.put("horror", "Šausmu");
        GENRE_EN_TO_LV.put("music", "Mūzika");
        GENRE_EN_TO_LV.put("musical", "Mūzikls");
        GENRE_EN_TO_LV.put("mystery", "Detektīvs");
        GENRE_EN_TO_LV.put("romance", "Romantika");
        GENRE_EN_TO_LV.put("sci-fi", "Zinātniskā fantastika");
        GENRE_EN_TO_LV.put("sport", "Sports");
        GENRE_EN_TO_LV.put("thriller", "Trilleris");
        GENRE_EN_TO_LV.put("war", "Kara");
        GENRE_EN_TO_LV.put("western", "Vesterns");
    }

    
    private static String applyPlotPhraseFixesLv(String s) {
        if (s == null || s.isBlank()) {
            return s;
        }
        String t = s;
        t = t.replaceAll("(?i)bounty\\s*-\\s*hunter", "atlīdzības mednieks");
        t = t.replaceAll("(?i)bounty\\s+hunter", "atlīdzības mednieks");
        t = t.replace("var't", "nevar");
        t = t.replace("Var't", "Nevar");
        t = t.replace("var\u2019t", "nevar");
        t = t.replace("Var\u2019t", "Nevar");
        return t;
    }

    
    private static String stripOrphanPercentSigns(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        StringBuilder sb = new StringBuilder(s.length());
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (c == '%' && i + 2 < s.length() && isHex(s.charAt(i + 1)) && isHex(s.charAt(i + 2))) {
                sb.append('%').append(s.charAt(i + 1)).append(s.charAt(i + 2));
                i += 3;
                continue;
            }
            if (c == '%') {
                i++;
                continue;
            }
            sb.append(c);
            i++;
        }
        return sb.toString();
    }

    private static boolean isHex(char c) {
        return (c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f');
    }

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    
    public String translateEnToLv(String text) {
        if (text == null || text.isBlank()) {
            return text;
        }
        String trimmed = text.trim();
        if (trimmed.length() > MAX_INPUT) {
            trimmed = trimmed.substring(0, MAX_INPUT);
        }

        try {
            List<String> chunks = chunk(trimmed, CHUNK);
            StringBuilder out = new StringBuilder();
            int translatedCount = 0;
            for (String chunk : chunks) {
                String part = callMyMemoryWithRetries(chunk);
                String piece;
                if (part == null || part.isBlank()) {
                    piece = chunk;
                } else {
                    piece = normalizeTranslationOutput(part).trim();
                    translatedCount++;
                }
                if (out.length() > 0) {
                    out.append(' ');
                }
                out.append(piece);
            }
            if (translatedCount == 0) {
                return text;
            }
            return normalizeTranslationOutput(out.toString());
        } catch (Exception e) {
            return text;
        }
    }

    
    public List<String> translateListEnToLv(List<String> inputs) {
        if (inputs == null || inputs.isEmpty()) {
            return List.of();
        }
        List<String> out = new ArrayList<>();
        boolean first = true;
        for (String raw : inputs) {
            if (!first) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            first = false;
            String s = raw == null ? "" : raw.trim();
            if (s.isEmpty()) {
                out.add("");
                continue;
            }
            if (s.length() > 500) {
                s = s.substring(0, 500);
            }
            out.add(translateShortEnToLv(s));
        }
        return out;
    }

    
    public String translateShortEnToLv(String text) {
        if (text == null || text.isBlank()) {
            return text;
        }
        String t = text.trim();
        String known = knownGenreLv(t);
        if (known != null) {
            return known;
        }
        String hit = shortEnToLvCache.get(t);
        if (hit != null) {
            return hit;
        }
        if (t.length() > 450) {
            return translateEnToLv(t);
        }
        try {
            String part = callMyMemory(t);
            if (part == null || part.isBlank()) {
                return t;
            }
            String out = normalizeTranslationOutput(part);
            if (shortEnToLvCache.size() < TITLE_CACHE_CAP) {
                shortEnToLvCache.putIfAbsent(t, out);
            }
            return out;
        } catch (Exception e) {
            return t;
        }
    }

    private static String knownGenreLv(String englishName) {
        if (englishName == null) {
            return null;
        }
        return GENRE_EN_TO_LV.get(englishName.trim().toLowerCase(Locale.ROOT));
    }

    
    private static String normalizeTranslationOutput(String s) {
        if (s == null || s.isBlank()) {
            return s;
        }
        String cur = collapseBrokenPercentEscapes(s.trim());
        for (int i = 0; i < 12; i++) {
            String next = decodeCommonPercentLiterals(cur);
            next = collapseBrokenPercentEscapes(next);
            try {
                next = URLDecoder.decode(next.replace('+', ' '), StandardCharsets.UTF_8);
            } catch (IllegalArgumentException e) {
                
            }
            if (next.equals(cur)) {
                break;
            }
            cur = next;
        }
        String r = decodeCommonPercentLiterals(collapseBrokenPercentEscapes(cur));
        r = stripOrphanPercentSigns(r);
        while (r.contains(",,")) {
            r = r.replace(",,", ",");
        }
        r = applyPlotPhraseFixesLv(r);
        return r;
    }

    
    private static String collapseBrokenPercentEscapes(String s) {
        String t = s;
        for (int j = 0; j < 8; j++) {
            String next = t.replaceAll("(?i)%\\s+(?=[0-9a-f]{2})", "%");
            if (next.equals(t)) {
                break;
            }
            t = next;
        }
        return t;
    }

    
    private static String fixBareTwoCCommaArtifacts(String s) {
        if (s == null || !s.contains("2C")) {
            return s;
        }
        String t = s.replace("2C ", ", ")
                .replace("2C\n", ",\n")
                .replace("2C\t", ",\t")
                .replace("2C\r", ",\r");
        t = t.replaceAll("([\\p{L}\\p{M}])2C([\\p{L}])", "$1, $2");
        return t;
    }

    private static String decodeCommonPercentLiterals(String s) {
        s = fixBareTwoCCommaArtifacts(s);
        return s.replace("%2C", ",")
                .replace("%2c", ",")
                .replace("%28", "(")
                .replace("%29", ")")
                .replace("%27", "'")
                .replace("%22", "\"")
                .replace("%3A", ":")
                .replace("%3a", ":")
                .replace("%3F", "?")
                .replace("%3f", "?")
                .replace("%21", "!")
                .replace("%26", "&")
                .replace("%2E", ".")
                .replace("%2e", ".")
                .replace("%20", " ")
                .replace("%2D", "-")
                .replace("%2d", "-")
                .replace("%3B", ";")
                .replace("%3b", ";")
                .replace("%2F", "/")
                .replace("%2f", "/")
                .replace("%2C%20", ", ")
                .replace("%2c%20", ", ");
    }

    private static List<String> chunk(String text, int maxLen) {
        List<String> parts = new ArrayList<>();
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + maxLen, text.length());
            if (end < text.length()) {
                int space = text.lastIndexOf(' ', end);
                if (space > start + 40) {
                    end = space;
                }
            }
            String piece = text.substring(start, end).trim();
            if (!piece.isEmpty()) {
                parts.add(piece);
            }
            start = end;
            while (start < text.length() && Character.isWhitespace(text.charAt(start))) {
                start++;
            }
        }
        return parts;
    }

    private String callMyMemory(String chunk) throws Exception {
        String encoded = URLEncoder.encode(chunk, StandardCharsets.UTF_8);
        String uri = "https://api.mymemory.translated.net/get?q=" + encoded + "&langpair=en|lv";

        String body = restClient.get()
                .uri(uri)
                .retrieve()
                .body(String.class);

        if (body == null || body.isBlank()) {
            return null;
        }
        JsonNode root = objectMapper.readTree(body);
        JsonNode responseData = root.get("responseData");
        if (responseData == null || !responseData.has("translatedText")) {
            return null;
        }
        String translated = responseData.get("translatedText").asText("");
        if (translated.contains("MYMEMORY WARNING") || translated.contains("QUERY LENGTH LIMIT")) {
            return null;
        }
        return normalizeTranslationOutput(translated);
    }

    private String callMyMemoryWithRetries(String chunk) throws Exception {
        String out = null;
        for (int attempt = 0; attempt < 3; attempt++) {
            if (attempt > 0) {
                try {
                    Thread.sleep(120L * attempt);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            out = callMyMemory(chunk);
            if (out != null && !out.isBlank()) {
                return out;
            }
        }
        return out;
    }
}
