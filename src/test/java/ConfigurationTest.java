import com.parkin.registrations.infrastructure.LocalDateTimeConverter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static junit.framework.TestCase.assertNull;
import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
public class ConfigurationTest {

    private LocalDateTimeConverter converter;

    @Before
    public void setUp() throws Exception {
        converter = new LocalDateTimeConverter();
    }

    @Test
    public void givenValidLdtShouldReturnCorrectTimestamp() throws Exception {
        LocalDateTime ldt;
        Timestamp ts;

        ldt = LocalDateTime.now();
        ts = Timestamp.valueOf(ldt.withNano(0));

        assertThat(converter.convertToDatabaseColumn(ldt)).isEqualTo(ts);
    }

    @Test
    public void givenNotValidLdtShouldReturnNull() throws Exception {
        assertNull(converter.convertToDatabaseColumn(null));
    }

    @Test
    public void givenValidTimestampShouldReturnLdt() throws Exception {
        LocalDateTime ldt = LocalDateTime.now();
        Timestamp ts = Timestamp.valueOf(ldt);

        assertThat(converter.convertToEntityAttribute(ts)).isEqualTo(ldt);
    }

    @Test
    public void givenNotValidTimestampShouldReturnNull() throws Exception {
        assertNull(converter.convertToEntityAttribute(null));
    }
}

