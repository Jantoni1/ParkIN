import com.parkin.registrations.Registration;
import com.parkin.registrations.RegistrationController;
import com.parkin.registrations.RegistrationRepository;
import com.parkin.registrations.infrastructure.configuration.ConfigurationRepository;
import com.parkin.tariffs.Tariff;
import com.parkin.tariffs.TariffCrudRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.reflect.Whitebox;
import org.springframework.test.context.junit4.SpringRunner;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class RegistrationTest {

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
    public void givenDepartureAndArrivalShouldCalculatePriceCorrectly() throws Exception {

        //before
        Registration testRegistration = new Registration();
        Tariff testTariff = mock(Tariff.class);
        ConfigurationRepository configurationRepository = mock(ConfigurationRepository.class);
        RegistrationRepository registrationRepository = mock(RegistrationRepository.class);
        TariffCrudRepository tariffCrudRepository = mock(TariffCrudRepository.class);
        RegistrationController controller = new RegistrationController
                (configurationRepository,registrationRepository,tariffCrudRepository);

        //given
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

    // slots-info test
    // register test
    // checkout test
    // unregister test
}
