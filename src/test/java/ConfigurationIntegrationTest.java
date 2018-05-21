import com.parkin.Main;
import com.parkin.registrations.infrastructure.configuration.Configuration;
import com.parkin.registrations.infrastructure.configuration.ConfigurationRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
@DataJpaTest
@PropertySource("classpath:application-test.properties")
public class ConfigurationIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @MockBean
    private ConfigurationRepository configurationRepository;



    @Test
    public void givenValidNameShouldReturnRepository() {

        //given
        Configuration testConfiguration = new Configuration();
        testConfiguration.setName("test");
        entityManager.persist(testConfiguration);
        entityManager.flush();

        //when
        Mockito.when(configurationRepository.findByName(testConfiguration.getName())).thenReturn(Optional.of(testConfiguration));
        Optional<Configuration> foundConfiguration = configurationRepository.findByName(testConfiguration.getName());

        //then
       assertThat(foundConfiguration).isEqualTo(Optional.of(testConfiguration));
    }
}
