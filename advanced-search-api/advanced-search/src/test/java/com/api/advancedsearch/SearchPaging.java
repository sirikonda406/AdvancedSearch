package com.api.advancedsearch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class SearchPaging {
    /** The offset of the first result to return. */
    private java.lang.Integer offset;

    /** The number of results to return. */
    private java.lang.Integer size;
}
