package spring;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import spring.controller.tarrifs.Tariff;
import spring.controller.tarrifs.TariffController;
import spring.controller.tarrifs.TariffCrudRepository;
import java.math.BigDecimal;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(MockitoJUnitRunner.class)
public class TariffTest {

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    private JacksonTester<Tariff> jsonConverter;

    @Test
    public void canRetrieveTariffREST() throws Exception {
        JacksonTester.initFields(this, new ObjectMapper());

        Tariff tOlder = new Tariff();
        tOlder.setBasicBid(BigDecimal.valueOf(3.4));
        tOlder.setExtendedBid(BigDecimal.valueOf(4.4));
        tOlder.setBasicPeriod(1.2);


        TariffCrudRepository tariffCrudRepository = Mockito.mock(TariffCrudRepository.class);
        BDDMockito.given(tariffCrudRepository.findTopByOrderByIdDesc()).willReturn(tOlder);

        TariffController tariffController = new TariffController(tariffCrudRepository);

        mockMvc = MockMvcBuilders.standaloneSetup(tariffController).build();

        MockHttpServletResponse response = mockMvc.perform(get("/tariff-data")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        Assertions.assertThat(response.getContentAsString()).isEqualTo(jsonConverter.write(tOlder).getJson());
    }

    @Test
    public void tariffIsSaved() throws Exception {
        JacksonTester.initFields(this, new ObjectMapper());

        Tariff t = new Tariff();
        t.setBasicBid(BigDecimal.valueOf(3.4));
        t.setExtendedBid(BigDecimal.valueOf(4.4));
        t.setBasicPeriod(3.2);

        TariffCrudRepository tariffCrudRepository = Mockito.mock(TariffCrudRepository.class);

        TariffController tariffController = new TariffController(tariffCrudRepository);

        mockMvc = MockMvcBuilders.standaloneSetup(tariffController).build();

        MockHttpServletResponse response = mockMvc.perform(post("/tariff-data")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonConverter.write(t).getJson())
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        BDDMockito.verify(tariffCrudRepository).save(Mockito.any(Tariff.class));
    }
}