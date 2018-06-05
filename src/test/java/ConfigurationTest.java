import com.fasterxml.jackson.databind.ObjectMapper;
import com.parkin.registrations.infrastructure.LocalDateTimeConverter;
import com.parkin.registrations.infrastructure.configuration.Configuration;
import com.parkin.tariffs.Tariff;
import com.parkin.tariffs.TariffController;
import com.parkin.tariffs.TariffCrudRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.junit4.SpringRunner;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static junit.framework.TestCase.assertNull;
import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
public class ConfigurationTest {

    private LocalDateTimeConverter  c = new LocalDateTimeConverter();

    @Test
    public void givenValidLdtShouldReturnCorrectTimestamp() throws Exception {
        LocalDateTime ldt;
        Timestamp ts;

        ldt = LocalDateTime.now();
        ts =  Timestamp.valueOf(ldt.withNano(0));

        assertThat(c.convertToDatabaseColumn(ldt)).isEqualTo(ts);
    }

    @Test
    public void givenNotValidLdtShouldReturnNull() throws Exception {
        assertNull(c.convertToDatabaseColumn(null));
    }

    @Test
    public void givenValidTimestampShouldReturnLdt() throws Exception {
        LocalDateTime ldt = LocalDateTime.now();
        Timestamp ts =Timestamp.valueOf(ldt);

        assertThat(c.convertToEntityAttribute(ts)).isEqualTo(ldt);
    }

    @Test
    public void givenNotValidTimestampShouldReturnNull() throws Exception {
        assertNull(c.convertToEntityAttribute(null));
    }
}

