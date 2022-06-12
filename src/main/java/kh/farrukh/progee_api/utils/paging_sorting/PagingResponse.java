package kh.farrukh.progee_api.utils.paging_sorting;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class PagingResponse<T> {
    @JsonProperty("next_page")
    private Integer nextPage = null;
    @JsonProperty("prev_page")
    private Integer prevPage = null;
    @JsonProperty("total_pages")
    private int totalPages;
    @JsonProperty("total_items")
    private long totalItems;
    private int page;
    private List<T> items;

    public PagingResponse(Page<T> page) {
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
