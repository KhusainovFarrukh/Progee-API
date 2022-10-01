package kh.farrukh.progee_api.image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ImageRepositoryTest {

    @Autowired
    private ImageRepository underTest;

}