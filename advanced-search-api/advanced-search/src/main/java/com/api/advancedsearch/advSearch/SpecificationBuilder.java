package com.api.advancedsearch.advSearch;

import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpecificationBuilder<T> {

    private final List<SearchCriteria> params;

    public SpecificationBuilder() {
        this.params = new ArrayList<>();
    }

    public final SpecificationBuilder<T> with(String key, String operation, Object value) {
        params.add(new SearchCriteria(key, operation, value));
        return this;
    }

    public final SpecificationBuilder<T> with(SearchCriteria searchCriteria) {
        params.add(searchCriteria);
        return this;
    }

    public Specification<T> build(Map<String, String> fieldMappings) {
        if (params.isEmpty()) {
            return Specification.where(null); // Returns all results
        }

        Specification<T> result = new GenericSpecification<>(params.get(0), fieldMappings);
        for (int idx = 1; idx < params.size(); idx++) {
            SearchCriteria criteria = params.get(idx);
            result = SearchOperation.getDataOption(criteria.getDataOption()) == SearchOperation.ALL
                    ? Specification.where(result).and(new GenericSpecification<>(criteria, fieldMappings))
                    : Specification.where(result).or(new GenericSpecification<>(criteria, fieldMappings));
        }

        return result;

    }
}

