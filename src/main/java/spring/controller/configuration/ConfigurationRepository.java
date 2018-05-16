package spring.controller.configuration;

import org.springframework.data.repository.Repository;

public interface ConfigurationRepository extends Repository<Configuration, Long> {

    Configuration findByName(String name);
}