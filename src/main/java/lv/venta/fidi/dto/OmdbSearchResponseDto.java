package lv.venta.fidi.dto;

import java.util.List;

public class OmdbSearchResponseDto {

    private List<OmdbSearchItemDto> Search;
    private String totalResults;
    private String Response;

    public OmdbSearchResponseDto() {
    }

    public List<OmdbSearchItemDto> getSearch() {
        return Search;
    }

    public void setSearch(List<OmdbSearchItemDto> search) {
        Search = search;
    }

    public String getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(String totalResults) {
        this.totalResults = totalResults;
    }

    public String getResponse() {
        return Response;
    }

    public void setResponse(String response) {
        Response = response;
    }
}