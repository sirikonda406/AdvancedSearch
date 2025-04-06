package com.api.advancedsearch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class SearchCounting {
    private java.lang.String countMode;
     private java.lang.Integer countLimit;
    private java.lang.Integer countHint;

}
