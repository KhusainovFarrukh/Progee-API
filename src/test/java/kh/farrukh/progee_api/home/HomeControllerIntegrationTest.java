package kh.farrukh.progee_api.home;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static kh.farrukh.progee_api.home.HomeConstants.ENDPOINT_HOME;
import static kh.farrukh.progee_api.home.HomeConstants.GREETING;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HomeControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void home_returnsValidGreeting() throws Exception {
        // when
        // then
        mvc.perform(get(ENDPOINT_HOME))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(GREETING));
    }
}