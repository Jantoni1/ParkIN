import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.parkin.registrations.Registration;
import com.parkin.registrations.RegistrationController;
import com.parkin.registrations.RegistrationRepository;
import com.parkin.registrations.infrastructure.configuration.Configuration;
import com.parkin.registrations.infrastructure.configuration.ConfigurationRepository;
import com.parkin.tariffs.Tariff;
import com.parkin.tariffs.TariffCrudRepository;
import org.apache.tomcat.jni.Local;
import org.assertj.core.api.Assertions;
import org.hibernate.annotations.Check;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
public class RegistrationTest {

    private MockMvc mockMvc;
    private ConfigurationRepository configurationRepository;
    private RegistrationRepository registrationRepository;
    private TariffCrudRepository tariffCrudRepository;
    private RegistrationController controller;
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setup() {
        configurationRepository = mock(ConfigurationRepository.class);
        registrationRepository = mock(RegistrationRepository.class);
        tariffCrudRepository = mock(TariffCrudRepository.class);
        controller = new RegistrationController(configurationRepository,registrationRepository,tariffCrudRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }


    //not-Rest tests
    @Test
    public void givenRegistrationWithoutDbShouldHaveNullId() {
        Registration registration = new Registration();
        assertThat(registration.getId()).isNull();
    }

    @Test
    public void givenRegistrationShouldBeAbleToSetDeparture() {
        Registration registration = new Registration();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currTime = formatter.format(LocalDateTime.now());
        registration.setDeparture(currTime);
        assertThat(registration.getDeparture()).isEqualTo(LocalDateTime.parse(currTime, formatter));
    }

    @Test
    public void givenRegistrationAllMembersShouldInitializeCorrectly() {

        //given
        Registration registration = new Registration();
        LocalDateTime currTime = LocalDateTime.now();

        //when
        registration.setRegistrationPlate("test123");
        registration.setArrival(currTime);
        registration.setTariffId(1L);
        registration.setDeparture(currTime);

        //then
        assertThat(registration).isNotEqualTo(null);
        assertThat(registration.getRegistrationPlate()).isEqualTo("test123");
        assertThat(registration.getArrival()).isEqualTo(currTime);
        assertThat(registration.getTariffId()).isEqualTo(1L);
        assertThat(registration.getDeparture()).isEqualTo(currTime);
    }

    @Test
    public void givenDepartureAndArrivalShouldCalculatePriceCorrectlyOutsideBasicPeriod() throws Exception {
        //given
        Tariff testTariff = mock(Tariff.class);
        Registration testRegistration = new Registration();
        LocalDateTime currTime = LocalDateTime.now();

        //when
        when(testTariff.getBasicBid()).thenReturn(new BigDecimal("2.0"));
        when(testTariff.getBasicPeriod()).thenReturn(2.0);
        when(testTariff.getExtendedBid()).thenReturn(new BigDecimal("1.0"));
        testRegistration.setArrival(currTime);
        testRegistration.setDeparture(currTime.plusHours(2));

        //then
        double price = Whitebox.<BigDecimal> invokeMethod(controller, "calculatePrice", testRegistration, testTariff, LocalDateTime.now().plusHours(2)).doubleValue();
        assertThat(price).isEqualTo(4.0);
    }

    @Test
    public void givenDepartureAndArrivalTimeShouldCalculatePriceCorrectlyInsideBasicPeriod() throws Exception {
        //given
        Registration testRegistration = new Registration();
        Tariff testTariff = mock(Tariff.class);
        LocalDateTime currTime = LocalDateTime.now();

        //when
        when(testTariff.getBasicBid()).thenReturn(new BigDecimal("2.0"));
        when(testTariff.getBasicPeriod()).thenReturn(2.0);
        when(testTariff.getExtendedBid()).thenReturn(new BigDecimal("1.0"));
        testRegistration.setArrival(currTime);
        testRegistration.setDeparture(currTime.plusHours(1));

        //then
        double price = Whitebox.<BigDecimal>invokeMethod(controller, "calculatePrice", testRegistration, testTariff, LocalDateTime.now().plusHours(1)).doubleValue();
        assertThat(price).isEqualTo(2.0);

    }

    @Test
    public void givenRegistrationPlateShouldReturnRegistration()throws Exception {
        //given
        Tariff t = new Tariff();
        t.setBasicBid(BigDecimal.valueOf(3.4));
        t.setExtendedBid(BigDecimal.valueOf(4.4));
        t.setBasicPeriod(3.2);

        //when
        when(tariffCrudRepository.findTopByOrderByIdDesc()).thenReturn(t);

        //then
        String registrationPlate = "WAW12345";
        Registration result = Whitebox.<Registration>invokeMethod(controller, "createRegistration", registrationPlate);
        assertThat(registrationPlate).isEqualTo(result.getRegistrationPlate());
    }

    //Rest GET tests
    @Test
    public void getSlotsInfoShouldReturnParkingInfo()throws Exception {
        //given
        Configuration conf = new Configuration();
        conf.setName("capacity");
        conf.setValue(1234);

        Tariff t = new Tariff();
        t.setBasicBid(BigDecimal.valueOf(3.4));
        t.setExtendedBid(BigDecimal.valueOf(4.4));
        t.setBasicPeriod(3.2);

        ArrayList<Registration> al = new ArrayList<>();
        for(int i=0;i<123;i++) al.add(new Registration());

        //when
        when(tariffCrudRepository.findTopByOrderByIdDesc()).thenReturn(t);
        when(configurationRepository.findByName("capacity")).thenReturn(Optional.of(conf));
        when(registrationRepository.findAllByDepartureIsNull()).thenReturn(al);

        MockHttpServletResponse response = mockMvc.perform(get("/slots-info")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(mapper.writeValueAsString(ImmutableMap.of("capacity", conf.getValue(), "occupied", 123)));
        assertThat(conf.getId()).isEqualTo(null);
        assertThat(conf.getName()).isEqualTo("capacity");
    }

    @Test
    public void getStatisticsShouldReturnCorrectData()throws Exception {
        //given
        Tariff t = new Tariff();
        t.setBasicBid(BigDecimal.valueOf(2.0));
        t.setExtendedBid(BigDecimal.valueOf(3.0));
        t.setBasicPeriod(3.0);

        ArrayList<Registration> arr = new ArrayList<>();

        Registration reg[] = new Registration[3];
        for(int i=0;i<3;i++) {
            reg[i]=new Registration();
            reg[i].setRegistrationPlate("WAW120"+i);
            reg[i].setTariffId((long)1);
            reg[i].setArrival(LocalDateTime.now().minusHours(3));
            arr.add(reg[i]);
        }
        reg[2].setDeparture(LocalDateTime.now().minusHours(1));

        ArrayList<Registration> dep = new ArrayList<>();
        dep.add(reg[2]);
        LocalDateTime time = LocalDate.now().atStartOfDay();

        //when
        when(registrationRepository.findAllByArrivalGreaterThan(time)).thenReturn(arr);
        when(registrationRepository.findAllByDepartureGreaterThan(time)).thenReturn(dep);
        when(tariffCrudRepository.findOne((long)1)).thenReturn(Optional.of(t));

        MockHttpServletResponse response = mockMvc.perform(get("/statistics")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        //then
        StatisticsResponse res = mapper.readValue(response.getContentAsString(), StatisticsResponse.class);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(res.getEarnings()).isEqualTo(4.0);
        assertThat(res.getArrivals()).isEqualTo(3);
        assertThat(res.getDepartures()).isEqualTo(1);
    }

    //Rest POST tests
    @Test
    public void postCheckoutGivenExistingRegistrationPlateShouldCheckout()throws Exception {
        //given
        String correctPlate = "WAW123";

        Tariff t = new Tariff();
        t.setBasicBid(BigDecimal.valueOf(3.4));
        t.setExtendedBid(BigDecimal.valueOf(4.4));
        t.setBasicPeriod(4.0);

        Registration reg = new Registration();
        reg.setRegistrationPlate(correctPlate);
        reg.setArrival(LocalDateTime.now().minusHours(3));
        reg.setTariffId((long)1);

        Registration request = new Registration();
        request.setRegistrationPlate(correctPlate);

        //when
        when(registrationRepository.findTopByRegistrationPlateOrderByArrivalDesc(correctPlate)).thenReturn(Optional.of(reg));
        when(tariffCrudRepository.findOne((long)1)).thenReturn(Optional.of(t));

        MockHttpServletResponse response = mockMvc.perform(post("/checkout")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content("{\"registrationPlate\":\"WAW123\"}")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        CheckoutResponse res = mapper.readValue(response.getContentAsString(), CheckoutResponse.class);

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(res.getRegistrationPlate()).isEqualTo(correctPlate);
        assertThat(res.getFee()).isEqualTo(10.2);
    }

    @Test
    public void postCheckoutGivenNotExistingRegistrationPlateShouldSendNotFoundStatus()throws Exception {
        //when
        when(registrationRepository.findTopByRegistrationPlateOrderByArrivalDesc("BI123")).thenReturn(Optional.empty());
        MockHttpServletResponse response = mockMvc.perform(post("/checkout")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content("{\"registrationPlate\":\"BI123\"}")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }


    @Test
    public void postUnregisterGivenNotExistingEntryShouldSendNotFound()throws Exception {
        //when
        when(registrationRepository.findTopByRegistrationPlateOrderByArrivalDesc("WAW121")).thenReturn(Optional.empty());
        MockHttpServletResponse response = mockMvc.perform(post("/unregister")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content("{\"registrationPlate\":\"WAW121\"}")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void postUnregisterGivenExistingEntryShouldUnregister()throws Exception {
        //given
        Registration reg=new Registration();
        reg.setRegistrationPlate("WAW120");
        reg.setTariffId((long)1);
        reg.setArrival(LocalDateTime.now().minusHours(3));

        //when
        when(registrationRepository.findTopByRegistrationPlateOrderByArrivalDesc("WAW120")).thenReturn(Optional.of(reg));
        MockHttpServletResponse response = mockMvc.perform(post("/unregister")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content("{\"registrationPlate\":\"WAW120\"}")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        BDDMockito.verify(registrationRepository).save(Mockito.any(Registration.class));
    }

    @Test
    public void postRegisterGivenTooLongPlateShouldSendNotAcceptable()throws Exception {
        //when
        when(registrationRepository.findTopByRegistrationPlateAndDepartureIsNullOrderByArrivalDesc("WAW1234567890")).thenReturn(Optional.empty());

        MockHttpServletResponse response = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content("{\"registrationPlate\":\"WAW1234567890\"}")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
    }

    @Test
    public void postRegisterGivenAlreadyExistingEntryShouldSendBadRequest()throws Exception {
        //given
        Registration reg = new Registration();
        reg.setRegistrationPlate("WAW120");
        reg.setTariffId((long) 1);
        reg.setArrival(LocalDateTime.now().minusHours(3));

        //when
        when(registrationRepository.findTopByRegistrationPlateAndDepartureIsNullOrderByArrivalDesc("WAW120")).thenReturn(Optional.of(reg));

        MockHttpServletResponse response = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content("{\"registrationPlate\":\"WAW120\"}")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void postRegisterNotAlreadyExistingEntryShouldRegister()throws Exception {
        //given
        Tariff t = new Tariff();
        t.setBasicBid(BigDecimal.valueOf(2.0));
        t.setExtendedBid(BigDecimal.valueOf(3.0));
        t.setBasicPeriod(3.0);

        //when
        when(registrationRepository.findTopByRegistrationPlateAndDepartureIsNullOrderByArrivalDesc("WAW121")).thenReturn(Optional.empty());
        when(tariffCrudRepository.findTopByOrderByIdDesc()).thenReturn(t);

        MockHttpServletResponse response = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content("{\"registrationPlate\":\"WAW121\"}")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        BDDMockito.verify(registrationRepository).save(Mockito.any(Registration.class));
    }
}

class CheckoutResponse {
    private Double fee;
    private String registrationPlate;
    private LocalDateTime arrival;
    private LocalDateTime departure;
    private Long tariffId;
    public Double getFee() { return fee; }
    public void setFee(Double fee) {
        this.fee = fee;
    }
    public String getRegistrationPlate() {
        return registrationPlate;
    }
    public void setRegistrationPlate(String registrationPlate) {
        this.registrationPlate = registrationPlate;
    }
    public void setArrival(String arrival) {
        arrival=arrival.replace('T',' ');
        arrival=arrival.substring(0,arrival.length()-4);
        this.arrival = LocalDateTime.parse(arrival, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    public void setDeparture(String departure) {
        departure=departure.replace('T',' ');
        departure=departure.substring(0,departure.length()-4);
        this.departure = LocalDateTime.parse(departure, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}

class StatisticsResponse {
    private Double earnings;
    private Integer arrivals;
    private Integer departures;
    public Double getEarnings() { return earnings; }
    public void setEarnings(Double e) {
        this.earnings = e;
    }
    public Integer getArrivals() { return arrivals; }
    public void setArrivals(int e) { this.arrivals = e; }
    public Integer getDepartures() { return departures; }
    public void setDepartures(int e) { this.departures = e; }
}
