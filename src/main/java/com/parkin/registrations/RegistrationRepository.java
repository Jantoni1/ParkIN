package com.parkin.registrations;

import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends Repository<Registration, Long> {

    @RestResource
    Registration save(Registration registration);

    Integer countByDepartureIsNull();

    List<Registration> findAllByDepartureIsNull();

    Optional<Registration> findTopByRegistrationPlateAndDepartureIsNullOrderByArrivalDesc(String registrationPlate);

    Optional<Registration> findTopByRegistrationPlateOrderByArrivalDesc(String registrationPlate);

    List<Registration> findAllByDepartureGreaterThan(LocalDateTime time);

    List<Registration> findAllByArrivalGreaterThan(LocalDateTime time);

    List<Registration> findAllByDepartureBetween(LocalDateTime begin, LocalDateTime end);

    List<Registration> findAllByArrivalGreaterThanAndDepartureIsNull(LocalDateTime begin);

    @Transactional
    void deleteAllByDepartureIsNull();
}
