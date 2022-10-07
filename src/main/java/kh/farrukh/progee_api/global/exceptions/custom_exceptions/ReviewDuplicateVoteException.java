package kh.farrukh.progee_api.global.exceptions.custom_exceptions;

import kh.farrukh.progee_api.global.exceptions.ApiException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static kh.farrukh.progee_api.global.exceptions.ExceptionMessages.EXCEPTION_REVIEW_DUPLICATE_VOTE;

/**
 * It's a custom exception that extends the ApiException class
 * and is thrown when a user tries to vote on a review that
 * they've already voted on
 * <p>
 * HttpStatus of the response will be BAD_REQUEST
 */
@Getter
public class ReviewDuplicateVoteException extends ApiException {

    private final String voteType;

    public ReviewDuplicateVoteException(String voteType) {
        super(
                String.format("You already %sd this review", voteType),
                HttpStatus.BAD_REQUEST,
                EXCEPTION_REVIEW_DUPLICATE_VOTE,
                new Object[]{voteType}
        );
        this.voteType = voteType;
    }
}
