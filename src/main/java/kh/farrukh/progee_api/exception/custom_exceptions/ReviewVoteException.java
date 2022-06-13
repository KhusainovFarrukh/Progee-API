package kh.farrukh.progee_api.exception.custom_exceptions;

import kh.farrukh.progee_api.exception.ApiException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static kh.farrukh.progee_api.utils.constant.ExceptionMessages.EXCEPTION_REVIEW_VOTE;

/**
 * It's a custom exception that extends the ApiException class
 * and is thrown when a user tries to vote on a review that
 * they've already voted on
 *
 * HttpStatus of the response will be BAD_REQUEST
 */
@Getter
public class ReviewVoteException extends ApiException {

    private final String voteType;

    public ReviewVoteException(String voteType) {
        super(
                String.format("You already %sd this review", voteType),
                HttpStatus.BAD_REQUEST,
                EXCEPTION_REVIEW_VOTE,
                new Object[]{voteType}
        );
        this.voteType = voteType;
    }
}
