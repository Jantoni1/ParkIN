package com.parkin.registrations.infrastructure.configuration;

import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface ConfigurationRepository extends Repository<Configuration, Long> {

    Optional<Configuration> findByName(String name);
}