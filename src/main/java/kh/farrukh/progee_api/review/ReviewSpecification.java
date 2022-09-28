package kh.farrukh.progee_api.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Objects;

/**
 * It's a specification that allows you to filter reviews by language and review value
 */
@AllArgsConstructor
@Getter
@Setter
public class ReviewSpecification implements Specification<Review> {

    private Long languageId;
    private ReviewValue reviewValue;

    @Override
    public Predicate toPredicate(
            Root<Review> root,
            CriteriaQuery<?> query,
            CriteriaBuilder criteriaBuilder
    ) {
        Predicate predicate = criteriaBuilder.conjunction();

        if (languageId != null) {
            predicate.getExpressions().add(criteriaBuilder.equal(root.get("language").get("id"), languageId));
        }

        if (reviewValue != null) {
            predicate.getExpressions().add(criteriaBuilder.equal(root.get("reviewValue"), reviewValue));
        }

        return predicate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReviewSpecification that)) return false;
        return Objects.equals(languageId, that.languageId) && reviewValue == that.reviewValue;
    }
}
