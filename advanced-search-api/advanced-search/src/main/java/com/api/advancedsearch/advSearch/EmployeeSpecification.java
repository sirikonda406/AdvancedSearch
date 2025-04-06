package com.api.advancedsearch.advSearch;

import com.api.advancedsearch.domain.Employee;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;
import java.util.Objects;

public class EmployeeSpecification implements Specification<Employee> {

    private final SearchCriteria searchCriteria;

    public EmployeeSpecification(final SearchCriteria searchCriteria){
        super();
        this.searchCriteria = searchCriteria;
    }

    @Override
    public Predicate toPredicate(Root<Employee> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        String strToSearch = searchCriteria.getValue().toString().toLowerCase();

        // Determine the correct attribute path based on the filterKey
        Path<?> path = resolvePath(root, searchCriteria.getFilterKey());

        switch (Objects.requireNonNull(SearchOperation.getSimpleOperation(searchCriteria.getOperation()))) {
            case CONTAINS:
                return cb.like(cb.lower(path.as(String.class)), "%" + strToSearch + "%");
            case DOES_NOT_CONTAIN:
                return cb.notLike(cb.lower(path.as(String.class)), "%" + strToSearch + "%");
            case BEGINS_WITH:
                return cb.like(cb.lower(path.as(String.class)), strToSearch + "%");
            case DOES_NOT_BEGIN_WITH:
                return cb.notLike(cb.lower(path.as(String.class)), strToSearch + "%");
            case ENDS_WITH:
                return cb.like(cb.lower(path.as(String.class)), "%" + strToSearch);
            case DOES_NOT_END_WITH:
                return cb.notLike(cb.lower(path.as(String.class)), "%" + strToSearch);
            case EQUAL:
                return cb.equal(path, searchCriteria.getValue());
            case NOT_EQUAL:
                return cb.notEqual(path, searchCriteria.getValue());
            case NUL:
                return cb.isNull(path);
            case NOT_NULL:
                return cb.isNotNull(path);
            case GREATER_THAN:
                return cb.greaterThan(path.as(String.class), searchCriteria.getValue().toString());
            case GREATER_THAN_EQUAL:
                return cb.greaterThanOrEqualTo(path.as(String.class), searchCriteria.getValue().toString());
            case LESS_THAN:
                return cb.lessThan(path.as(String.class), searchCriteria.getValue().toString());
            case LESS_THAN_EQUAL:
                return cb.lessThanOrEqualTo(path.as(String.class), searchCriteria.getValue().toString());
            default:
                return null;
        }
    }

    /**
     * Resolves the proper path for a given field.
     * This determines whether the field belongs to the root entity (Employee) or is part of a related entity (e.g., Department).
     */
    private Path<?> resolvePath(Root<Employee> root, String filterKey) {
        // Map of related entities and their corresponding root joins, dynamic enough to add more fields in the future
        Map<String, String> fieldMappings = Map.of("deptName", "department" // Example of supporting multiple related field mappings
        );

        if (fieldMappings.containsKey(filterKey)) {
            Join<Employee, ?> join = root.join(fieldMappings.get(filterKey));
            return join.get(filterKey);
        }

        // Default case: Assume that the field is part of the Employee entity
        return root.get(filterKey);
    }

}
