package lv.venta.fidi.service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;

import lv.venta.fidi.model.Genre;

@Service
public class GenreLabelUiService {

    private static final Map<String, String> LV_BY_EN = Map.ofEntries(
            Map.entry("Action", "Asa sižeta"),
            Map.entry("Adventure", "Piedzīvojumu"),
            Map.entry("Animation", "Animācija"),
            Map.entry("Biography", "Biogrāfija"),
            Map.entry("Comedy", "Komēdija"),
            Map.entry("Crime", "Kriminālfilma"),
            Map.entry("Documentary", "Dokumentālā"),
            Map.entry("Drama", "Drāma"),
            Map.entry("Family", "Ģimenes"),
            Map.entry("Fantasy", "Fantāzijas"),
            Map.entry("Film-Noir", "Nūara"),
            Map.entry("History", "Vēsturiska"),
            Map.entry("Horror", "Šausmu"),
            Map.entry("Music", "Mūzikas"),
            Map.entry("Musical", "Mūzikls"),
            Map.entry("Mystery", "Mistērija"),
            Map.entry("Romance", "Romantika"),
            Map.entry("Sci-Fi", "Zinātniskā fantastika"),
            Map.entry("Short", "Īsfilma"),
            Map.entry("Thriller", "Trilleris"),
            Map.entry("War", "Kara"),
            Map.entry("Western", "Vesterns")
    );

    private static boolean wantLatvianLabels(String appLang) {
        if (appLang == null || appLang.isBlank()) {
            return true;
        }
        return !"en".equalsIgnoreCase(appLang.trim());
    }

    private static String lookupLv(String englishName) {
        if (englishName == null) {
            return null;
        }
        String t = englishName.trim();
        String lv = LV_BY_EN.get(t);
        if (lv != null) {
            return lv;
        }
        String lower = t.toLowerCase(Locale.ROOT);
        for (Map.Entry<String, String> e : LV_BY_EN.entrySet()) {
            if (e.getKey().toLowerCase(Locale.ROOT).equals(lower)) {
                return e.getValue();
            }
        }
        return null;
    }

    public String displayGenreName(String appLang, String dbName) {
        if (dbName == null) {
            return null;
        }
        if (!wantLatvianLabels(appLang)) {
            return dbName;
        }
        String lv = lookupLv(dbName);
        return lv != null ? lv : dbName;
    }

    public Map<Long, String> mapDisplayNamesByGenreId(String appLang, List<Genre> genres) {
        Map<Long, String> out = new HashMap<>();
        if (genres == null) {
            return out;
        }
        for (Genre g : genres) {
            if (g == null) {
                continue;
            }
            out.put(g.getGenreId(), displayGenreName(appLang, g.getName()));
        }
        return out;
    }
}
