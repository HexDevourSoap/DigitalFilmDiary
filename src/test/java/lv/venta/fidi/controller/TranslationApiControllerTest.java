package lv.venta.fidi.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import lv.venta.fidi.dto.TranslateBatchRequest;
import lv.venta.fidi.dto.TranslatePlotRequest;
import lv.venta.fidi.service.PlotTranslationService;

class TranslationApiControllerTest {

    @Mock
    private PlotTranslationService plotTranslationService;

    @InjectMocks
    private TranslationApiController translationApiController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(translationApiController).build();
    }

    @Test
    void testPostTranslatePlotController() throws Exception {
        TranslatePlotRequest request = new TranslatePlotRequest("The hero saves the city.");

        when(plotTranslationService.translateEnToLv("The hero saves the city."))
                .thenReturn("Varonis izglābj pilsētu.");

        mockMvc.perform(post("/api/translate/plot")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Varonis izglābj pilsētu."));
    }

    @Test
    void testPostTranslateBatchController() throws Exception {
        TranslateBatchRequest request = new TranslateBatchRequest(List.of("Action", "Drama"));

        when(plotTranslationService.translateListEnToLv(List.of("Action", "Drama")))
                .thenReturn(List.of("Asa sižeta", "Drāma"));

        mockMvc.perform(post("/api/translate/batch")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.texts[0]").value("Asa sižeta"))
                .andExpect(jsonPath("$.texts[1]").value("Drāma"));
    }
}

