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
        Class<?> javaType = path.getJavaType(); // Determine the Java type of the path dynamically

        return switch (operation) {
            case CONTAINS -> {
                if (javaType.equals(String.class)) {
                    yield cb.like(cb.lower(path.as(String.class)), WILDCARD + stringValue + WILDCARD);
                } else {
                    throw new IllegalArgumentException("CONTAINS operation is only applicable for String fields.");
                }
            }
            case DOES_NOT_CONTAIN -> {
                if (javaType.equals(String.class)) {
                    yield cb.notLike(cb.lower(path.as(String.class)), WILDCARD + stringValue + WILDCARD);
                } else {
                    throw new IllegalArgumentException("DOES_NOT_CONTAIN operation is only applicable for String fields.");
                }
            }
            case BEGINS_WITH -> {
                if (javaType.equals(String.class)) {
                    yield cb.like(cb.lower(path.as(String.class)), stringValue + WILDCARD);
                } else {
                    throw new IllegalArgumentException("BEGINS_WITH operation is only applicable for String fields.");
                }
            }
            case DOES_NOT_BEGIN_WITH -> {
                if (javaType.equals(String.class)) {
                    yield cb.notLike(cb.lower(path.as(String.class)), stringValue + WILDCARD);
                } else {
                    throw new IllegalArgumentException("DOES_NOT_BEGIN_WITH operation is only applicable for String fields.");
                }
            }
            case ENDS_WITH -> {
                if (javaType.equals(String.class)) {
                    yield cb.like(cb.lower(path.as(String.class)), WILDCARD + stringValue);
                } else {
                    throw new IllegalArgumentException("ENDS_WITH operation is only applicable for String fields.");
                }
            }
            case DOES_NOT_END_WITH -> {
                if (javaType.equals(String.class)) {
                    yield cb.notLike(cb.lower(path.as(String.class)), WILDCARD + stringValue);
                } else {
                    throw new IllegalArgumentException("DOES_NOT_END_WITH operation is only applicable for String fields.");
                }
            }
            case EQUAL -> cb.equal(path, originalValue);
            case NOT_EQUAL -> cb.notEqual(path, originalValue);
            case NUL -> cb.isNull(path);
            case NOT_NULL -> cb.isNotNull(path);
            case GREATER_THAN -> {
                if (Comparable.class.isAssignableFrom(javaType)) {
                    yield cb.greaterThan(path.as(Comparable.class), (Comparable) originalValue);
                } else {
                    throw new IllegalArgumentException("GREATER_THAN operation is applicable only for Comparable fields.");
                }
            }
            case GREATER_THAN_EQUAL -> {
                if (Comparable.class.isAssignableFrom(javaType)) {
                    yield cb.greaterThanOrEqualTo(path.as(Comparable.class), (Comparable) originalValue);
                } else {
                    throw new IllegalArgumentException("GREATER_THAN_EQUAL operation is applicable only for Comparable fields.");
                }
            }
            case LESS_THAN -> {
                if (Comparable.class.isAssignableFrom(javaType)) {
                    yield cb.lessThan(path.as(Comparable.class), (Comparable) originalValue);
                } else {
                    throw new IllegalArgumentException("LESS_THAN operation is applicable only for Comparable fields.");
                }
            }
            case LESS_THAN_EQUAL -> {
                if (Comparable.class.isAssignableFrom(javaType)) {
                    yield cb.lessThanOrEqualTo(path.as(Comparable.class), (Comparable) originalValue);
                } else {
                    throw new IllegalArgumentException("LESS_THAN_EQUAL operation is applicable only for Comparable fields.");
                }
            }
            default -> throw new UnsupportedOperationException("Unsupported operation: " + operation);
        };

    }

    /**
     * Resolves the proper path for a given field.
     * This determines whether the field belongs to the root entity (Employee) or is part of a related entity (e.g., Department).
     */
    private Path<?> resolvePath(Root<T> root, String filterKey) {
        if (fieldMappings.containsKey(filterKey)) {
            Join<T, ?> join = root.join(fieldMappings.get(filterKey));
            return join.get(filterKey);
        } else if (root.getModel().getAttributes().stream().anyMatch(a -> a.getName().equals(filterKey))) {
            return root.get(filterKey);
        } else {
            throw new IllegalArgumentException("Invalid filter key: " + filterKey);
        }

    }
}

