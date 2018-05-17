package spring.controller.resgistrations;

import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RestResource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends Repository<Registration, Long> {

    @RestResource
    Registration save(Registration registration);

    List<Registration> findAllByDepartureIsNull();

    Optional<Registration> findTopByRegistrationPlateAndDepartureIsNullOrderByArrivalDesc(String registrationPlate);

    Optional<Registration> findTopByRegistrationPlateOrderByArrivalDesc(String registrationPlate);

    List<Registration> findAllByDepartureGreaterThan(LocalDateTime time);
}
