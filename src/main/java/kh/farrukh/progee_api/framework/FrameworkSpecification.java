package kh.farrukh.progee_api.framework;

import kh.farrukh.progee_api.global.resource_state.ResourceState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Objects;

/**
 * It's a specification that can be used to filter frameworks by language and state
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FrameworkSpecification implements Specification<Framework> {

    private Long languageId;
    private ResourceState state;

    @Override
    public Predicate toPredicate(
            @NonNull Root<Framework> root,
            @NonNull CriteriaQuery<?> query,
            CriteriaBuilder criteriaBuilder
    ) {
        Predicate predicate = criteriaBuilder.conjunction();

        if (languageId != null) {
            predicate.getExpressions().add(criteriaBuilder.equal(root.get("language").get("id"), languageId));
        }

        if (state != null) {
            predicate.getExpressions().add(criteriaBuilder.equal(root.get("state"), state));
        }

        return predicate;
    }

    /**
     * If the languageId and state of the current object are equal to the languageId and state of the object passed in,
     * then return true. Otherwise, return false
     *
     * @param o The object to compare with
     * @return The hashcode of the object.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FrameworkSpecification that = (FrameworkSpecification) o;
        return Objects.equals(languageId, that.languageId) && state == that.state;
    }
}
