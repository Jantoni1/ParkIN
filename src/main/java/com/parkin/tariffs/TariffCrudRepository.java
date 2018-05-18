package com.parkin.tariffs;

import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Optional;

public interface TariffCrudRepository extends Repository<Tariff, Long> {

    Optional<Tariff> findOne(Long id);

    Tariff findTopByOrderByIdDesc();

    @RestResource
    Tariff save(Tariff tariff);
}
