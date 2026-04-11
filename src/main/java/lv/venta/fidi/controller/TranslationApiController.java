package lv.venta.fidi.controller;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lv.venta.fidi.dto.TranslateBatchRequest;
import lv.venta.fidi.dto.TranslateBatchResponse;
import lv.venta.fidi.dto.TranslatePlotRequest;
import lv.venta.fidi.dto.TranslatePlotResponse;
import lv.venta.fidi.service.PlotTranslationService;

@RestController
@RequestMapping("/api")
public class TranslationApiController {

    private final PlotTranslationService plotTranslationService;

    public TranslationApiController(PlotTranslationService plotTranslationService) {
        this.plotTranslationService = plotTranslationService;
    }

    @PostMapping(value = "/translate/plot", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public TranslatePlotResponse translatePlot(@RequestBody TranslatePlotRequest request) {
        if (request == null || request.text() == null || request.text().isBlank()) {
            return new TranslatePlotResponse("");
        }
        String translated = plotTranslationService.translateEnToLv(request.text());
        return new TranslatePlotResponse(translated);
    }

    @PostMapping(value = "/translate/batch", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public TranslateBatchResponse translateBatch(@RequestBody TranslateBatchRequest request) {
        if (request == null || request.texts() == null || request.texts().isEmpty()) {
            return new TranslateBatchResponse(List.of());
        }
        Set<String> seen = new LinkedHashSet<>();
        List<String> unique = new ArrayList<>();
        for (String t : request.texts()) {
            if (t == null) {
                continue;
            }
            String s = t.trim();
            if (s.isEmpty()) {
                continue;
            }
            if (s.length() > 500) {
                s = s.substring(0, 500);
            }
            if (seen.add(s)) {
                unique.add(s);
            }
            if (unique.size() >= 40) {
                break;
            }
        }
        List<String> translated = plotTranslationService.translateListEnToLv(unique);
        return new TranslateBatchResponse(translated);
    }
}
