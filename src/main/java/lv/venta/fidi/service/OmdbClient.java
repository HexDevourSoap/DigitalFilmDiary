package lv.venta.fidi.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

import lv.venta.fidi.dto.OmdbMovieDto;
import lv.venta.fidi.dto.OmdbSearchItemDto;
import lv.venta.fidi.dto.OmdbSearchResponseDto;

@Service
public class OmdbClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${omdb.api-key}")
    private String apiKey;

    @Value("${omdb.base-url}")
    private String baseUrl;

    public OmdbMovieDto getByImdbId(String imdbId) {
        try {
            String url = baseUrl + "?apikey=" + apiKey + "&i=" + imdbId + "&plot=full";
            OmdbMovieDto dto = restTemplate.getForObject(url, OmdbMovieDto.class);

            if (dto == null || dto.getImdbID() == null || !"True".equalsIgnoreCase(dto.getResponse())) {
                return null;
            }

            return dto;
        } catch (HttpClientErrorException ex) {
            return null;
        } catch (RestClientException ex) {
            return null;
        }
    }

    public List<OmdbSearchItemDto> searchByTitle(String query) {
        if (query == null || query.isBlank()) {
            return Collections.emptyList();
        }

        String encodedQuery = UriUtils.encode(query.trim(), StandardCharsets.UTF_8);
        String url = baseUrl + "?apikey=" + apiKey + "&s=" + encodedQuery;

        OmdbSearchResponseDto response;
        try {
            response = restTemplate.getForObject(url, OmdbSearchResponseDto.class);
        } catch (HttpClientErrorException ex) {
            return Collections.emptyList();
        } catch (RestClientException ex) {
            return Collections.emptyList();
        }

        if (response == null || response.getSearch() == null || !"True".equalsIgnoreCase(response.getResponse())) {
            return Collections.emptyList();
        }

        return response.getSearch();
    }
}