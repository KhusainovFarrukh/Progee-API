package kh.farrukh.progee_api.global.utils.paging_sorting;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * It takes a Spring Page object and converts it into a
 * JSON object that can be returned by a REST endpoint
 * <p>
 * Used in all endpoints with pagination
 */
@Getter
@Setter
@NoArgsConstructor
public class PagingResponse<T> {
    @JsonProperty("next_page")
    private Integer nextPage = null;
    @JsonProperty("prev_page")
    private Integer prevPage = null;
    @JsonProperty("total_pages")
    private int totalPages = 0;
    @JsonProperty("total_items")
    private long totalItems = 0;
    private int page = 1;
    private List<T> items = List.of();

    public PagingResponse(Page<T> page) {
        if (page != null) {
            if (page.hasNext()) {
                this.nextPage = page.nextPageable().getPageNumber() + 1;
            }
            if (page.hasPrevious()) {
                this.prevPage = page.previousPageable().getPageNumber() + 1;
            }
            this.page = page.getPageable().getPageNumber() + 1;
            this.totalPages = page.getTotalPages();
            this.totalItems = page.getTotalElements();
            this.items = page.getContent();
        }
    }
}
