package com.api.advancedsearch.advSearch;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;

public class GenericSpecification<T> implements Specification<T> {

    private static final String WILDCARD = "%";

    private final SearchCriteria searchCriteria;
    private final Map<String, String> fieldMappings;

    public GenericSpecification(final SearchCriteria searchCriteria, final Map<String, String> fieldMappings) {
        this.searchCriteria = searchCriteria;
        this.fieldMappings = fieldMappings;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        SearchOperation operation = SearchOperation.getSimpleOperation(searchCriteria.getOperation());
        if (operation == null) {
            return null; // Handle unsupported operations more gracefully if needed
        }

        Object value = searchCriteria.getValue();
        String targetValue = value != null ? value.toString().toLowerCase() : null;
        Path<?> attributePath = resolvePath(root, searchCriteria.getFilterKey());

        return buildPredicate(operation, cb, attributePath, targetValue, value);
    }

    private Predicate buildPredicate(SearchOperation operation, CriteriaBuilder cb, Path<?> path, String stringValue, Object originalValue) {
        return switch (operation) {
            case CONTAINS -> cb.like(cb.lower(path.as(String.class)), WILDCARD + stringValue + WILDCARD);
            case DOES_NOT_CONTAIN -> cb.notLike(cb.lower(path.as(String.class)), WILDCARD + stringValue + WILDCARD);
            case BEGINS_WITH -> cb.like(cb.lower(path.as(String.class)), stringValue + WILDCARD);
            case DOES_NOT_BEGIN_WITH -> cb.notLike(cb.lower(path.as(String.class)), stringValue + WILDCARD);
            case ENDS_WITH -> cb.like(cb.lower(path.as(String.class)), WILDCARD + stringValue);
            case DOES_NOT_END_WITH -> cb.notLike(cb.lower(path.as(String.class)), WILDCARD + stringValue);
            case EQUAL -> cb.equal(path, originalValue);
            case NOT_EQUAL -> cb.notEqual(path, originalValue);
            case NUL -> cb.isNull(path);
            case NOT_NULL -> cb.isNotNull(path);
            case GREATER_THAN -> cb.greaterThan(path.as(String.class), stringValue);
            case GREATER_THAN_EQUAL -> cb.greaterThanOrEqualTo(path.as(String.class), stringValue);
            case LESS_THAN -> cb.lessThan(path.as(String.class), stringValue);
            case LESS_THAN_EQUAL -> cb.lessThanOrEqualTo(path.as(String.class), stringValue);
            default -> throw new UnsupportedOperationException("Unhandled operation type: " + operation);
        };
    }

    /**
     * Resolves the proper path for a given field.
     * This determines whether the field belongs to the root entity (Employee) or is part of a related entity (e.g., Department).
     */
    private Path<?> resolvePath(Root<T> root, String filterKey) {
        // Map of related entities and their corresponding root joins, dynamic enough to add more fields in the future
       /* Map<String, String> fieldMappings = Map.of("deptName", "department" // Example of supporting multiple related field mappings
        );*/

        if (fieldMappings.containsKey(filterKey)) {
            Join<T, ?> join = root.join(fieldMappings.get(filterKey));
            return join.get(filterKey);
        }

        // Default case: Assume that the field is part of the Employee entity
        return root.get(filterKey);
    }
}

