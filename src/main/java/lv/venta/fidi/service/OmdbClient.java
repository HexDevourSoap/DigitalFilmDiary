package lv.venta.fidi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lv.venta.fidi.dto.OmdbMovieDto;

@Service
public class OmdbClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${omdb.api-key}")
    private String apiKey;

    @Value("${omdb.base-url}")
    private String baseUrl;

    public OmdbMovieDto getByImdbId(String imdbId) {
        String url = baseUrl + "?apikey=" + apiKey + "&i=" + imdbId + "&plot=full";
        return restTemplate.getForObject(url, OmdbMovieDto.class);
    }
}
