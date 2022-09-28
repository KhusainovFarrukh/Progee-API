package kh.farrukh.progee_api.review;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }
}