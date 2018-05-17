import com.fasterxml.jackson.databind.ObjectMapper;
import demo.CarEntry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import spring.controller.HelloWorldController;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = HelloWorldController.class)
public class HelloWorldControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;
    ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void testPostService() throws Exception {
        CarEntry ec = new CarEntry();
        for (int i=0;i<3;i++) {
            ec.setRegistration("BI12"+i);
            mockMvc.perform(post("/PostService")
                    .contentType(MediaType.APPLICATION_JSON_VALUE).content(mapper.writeValueAsString(ec)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(jsonPath("id", is(i+1)))
                    .andExpect(jsonPath("registration", is("BI12"+i)));
        }
    }

    @Test
    public void testIndex() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }
}