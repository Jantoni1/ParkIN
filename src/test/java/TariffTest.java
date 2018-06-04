import com.fasterxml.jackson.databind.ObjectMapper;
import com.parkin.tariffs.Tariff;
import com.parkin.tariffs.TariffController;
import com.parkin.tariffs.TariffCrudRepository;
import org.assertj.core.api.Assertions;
import org.junit.Before;
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
import java.math.BigDecimal;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(MockitoJUnitRunner.class)
public class TariffTest {

    private MockMvc mockMvc;
    private TariffCrudRepository tariffCrudRepository;
    private TariffController tariffController;
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setup() {
        tariffCrudRepository = mock(TariffCrudRepository.class);
        tariffController = new TariffController(tariffCrudRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(tariffController).build();
    }

    @Test
    public void getTariffResponseSHouldBeOk() throws Exception {
        //given
        Tariff tOlder = new Tariff();
        tOlder.setBasicBid(BigDecimal.valueOf(3.4));
        tOlder.setExtendedBid(BigDecimal.valueOf(4.4));
        tOlder.setBasicPeriod(1.2);

        BDDMockito.given(tariffCrudRepository.findTopByOrderByIdDesc()).willReturn(tOlder);

        //when
        MockHttpServletResponse response = mockMvc.perform(get("/tariff-data")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(mapper.writeValueAsString(tOlder));
    }

    @Test
    public void postTariffTariffIsSaved() throws Exception {
        //given
        Tariff t = new Tariff();
        t.setBasicBid(BigDecimal.valueOf(3.4));
        t.setExtendedBid(BigDecimal.valueOf(4.4));
        t.setBasicPeriod(3.2);

        //when
        MockHttpServletResponse response = mockMvc.perform(post("/tariff-data")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(mapper.writeValueAsString(t))
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        BDDMockito.verify(tariffCrudRepository).save(Mockito.any(Tariff.class));
    }

    @Test
    public void givenValidTariffIdShouldReturnTariff() throws Exception {
        //given
        Tariff t[] = new Tariff[3];
        for(int i=0;i<3;i++) {
            t[i] = new Tariff();
            t[i].setBasicBid(BigDecimal.valueOf(3.4));
            t[i].setExtendedBid(BigDecimal.valueOf(4.4));
            t[i].setBasicPeriod(0.2+i);
            BDDMockito.given(tariffCrudRepository.findOne((long)i)).willReturn(Optional.of(t[i]));
        }
        //then
        for(int i=0;i<3;i++) assertThat(tariffCrudRepository.findOne((long)i)).isEqualTo(Optional.of(t[i]));
    }

    @Test
    public void givenNotValidTariffIdShouldReturnNull() throws Exception {
        assertThat(tariffCrudRepository.findOne((long)4)).isEqualTo(null);
        assertThat(tariffCrudRepository.findOne((long)5)).isEqualTo(null);
    }
}