package spring.controller.tarrifs;

import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface TariffRepository extends Repository<Tariff, Long> {
    Optional<Tariff> findOne(Long id);

    Tariff findTopByOrderByIdDesc();
}
