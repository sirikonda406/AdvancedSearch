package com.api.advancedsearch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
public class GraphSearch {

    private String id;
    private java.util.List<String> ids;
    private String state;
    private java.util.List<String> states;
    private SearchCounting counting;
    private SearchPaging paging;
    private SearchSorting sorting;

}
