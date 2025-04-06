package com.api.advancedsearch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class SearchSorting {
    /** The column used to sort the data. */
    private java.lang.String sortColumn;

    /** The colum is sorted in ascending mode by default. */
    private java.lang.Boolean sortAscending;

}
