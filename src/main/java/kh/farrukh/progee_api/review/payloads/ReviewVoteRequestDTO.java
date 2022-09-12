package kh.farrukh.progee_api.review.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * It's a DTO that represents a vote for review
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewVoteRequestDTO {

    @NotNull(message = "Vote must not be null")
    private boolean vote;
}
