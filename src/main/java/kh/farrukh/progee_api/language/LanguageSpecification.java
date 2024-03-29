package kh.farrukh.progee_api.language;

import kh.farrukh.progee_api.global.resource_state.ResourceState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * It's a specification that can be used to filter languages by their state
 */
@AllArgsConstructor
@Getter
@Setter
public class LanguageSpecification implements Specification<Language> {

    private ResourceState state;

    @Override
    public Predicate toPredicate(
            @NonNull Root<Language> root,
            @NonNull CriteriaQuery<?> query,
            CriteriaBuilder criteriaBuilder
    ) {
        Predicate predicate = criteriaBuilder.conjunction();

        if (state != null) {
            predicate.getExpressions().add(criteriaBuilder.equal(root.get("state"), state));
        }

        return predicate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LanguageSpecification that)) return false;
        return state == that.state;
    }
}
