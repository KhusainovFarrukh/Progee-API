package kh.farrukh.progee_api.endpoints.review;

import kh.farrukh.progee_api.review.ReviewRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureTestDatabase
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }
}