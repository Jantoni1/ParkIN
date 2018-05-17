package spring.controller.resgistrations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.google.common.collect.ImmutableMap;
import spring.controller.resgistrations.infrastructure.configuration.Configuration;
import spring.controller.resgistrations.infrastructure.configuration.ConfigurationRepository;
import spring.controller.tarrifs.Tariff;
import spring.controller.tarrifs.TariffCrudRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class RegistrationController {

    private ConfigurationRepository configurationRepository;

    private RegistrationRepository registrationRepository;

    private TariffCrudRepository tariffCrudRepository;

    @Autowired
    public RegistrationController(ConfigurationRepository configurationRepository
                                    , RegistrationRepository registrationRepository
                                    , TariffCrudRepository tariffCrudRepository) {
        this.configurationRepository = configurationRepository;
        this.registrationRepository = registrationRepository;
        this.tariffCrudRepository = tariffCrudRepository;
    }

    // Returns number of overall slots and occupied slots a the moment
    // In case server does not find configuration data in database, returns "error" field
    @GetMapping("/slots-info")
    public Map<String, Integer> getSlotsInfo() {
        Optional<Configuration> conf = configurationRepository.findByName("capacity");
        List<Registration> registrations = registrationRepository.findAllByDepartureIsNull();
        return conf.<Map<String, Integer>>map(configuration ->
                ImmutableMap.of("capacity", configuration.getValue(), "occupied", registrations.size())).orElseGet(() -> ImmutableMap.of("error", 1));
    }


    @PostMapping("/register")
    public ResponseEntity<Void> registerCar(@RequestBody Registration pRegistration) {
        if(registrationRepository.findTopByRegistrationPlateAndDepartureIsNullOrderByArrivalDesc(pRegistration.getRegistrationPlate())
                .isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        registrationRepository.save(createRegistration(pRegistration.getRegistrationPlate()));
        return new ResponseEntity<>(HttpStatus.OK);
    }



    @GetMapping("/checkout")
    public ResponseEntity<ImmutableMap<String, String>>carFeeLookup(@RequestBody Registration pRegistration) {
        Optional<Registration> registrationOptional = registrationRepository
                .findTopByRegistrationPlateOrderByArrivalDesc(pRegistration.getRegistrationPlate());
        if(registrationOptional.isPresent()) {
            Registration registration = registrationOptional.get();
            Optional<Tariff> tariff = tariffCrudRepository.findOne(registration.getTariffId());
            if(tariff.isPresent()) {
                LocalDateTime now = LocalDateTime.now();
                BigDecimal fee = calculatePrice(registration, tariff.get(), now);
                return new ResponseEntity<>(ImmutableMap.of("fee", fee.toString()
                        , "found", "false"
                        , "registrationPlate", registration.getRegistrationPlate()
                        , "arrivalTime", registration.getArrival().toString()
                        , "departureTime", now.toString()), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/unregister")
    public ResponseEntity<Void>  unregisterCar(@RequestBody Registration pRegistration) {
        Optional<Registration> registrationOptional = registrationRepository
                .findTopByRegistrationPlateOrderByArrivalDesc(pRegistration.getRegistrationPlate());
        if(registrationOptional.isPresent()) {
            Registration registration = registrationOptional.get();
            registration.setDeparture(LocalDateTime.now());
            registrationRepository.save(registration);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private Registration createRegistration(String registrationPlate) {
        Registration registration = new Registration();
        registration.setRegistrationPlate(registrationPlate);
        registration.setArrival(LocalDateTime.now());
        registration.setDeparture(null);
        registration.setTariffId(tariffCrudRepository.findTopByOrderByIdDesc().getId());
        return registration;
    }


    private BigDecimal calculatePrice(Registration registration, Tariff tariff, LocalDateTime now) {
        long hours = registration.getArrival().until(now, ChronoUnit.HOURS);
        long minutes = registration.getArrival().until(now, ChronoUnit.MINUTES) % 60;
        BigDecimal hoursBigDecimal = new BigDecimal(hours);
        BigDecimal minutesBigDecimal = new BigDecimal(minutes);
        BigDecimal fee;
        if(hours < tariff.getBasicPeriod()) {
            fee = tariff.getBasicBid().multiply(hoursBigDecimal);
            fee = fee.add(tariff.getBasicBid().multiply(minutesBigDecimal).divide(new BigDecimal(60), RoundingMode.FLOOR));
        }
        else {
            BigDecimal extendedBigPeriod = hoursBigDecimal.subtract(new BigDecimal(tariff.getBasicPeriod()));
            fee = tariff.getBasicBid().multiply(new BigDecimal(tariff.getBasicPeriod()));
            fee = fee.add(tariff.getExtendedBid().multiply(minutesBigDecimal).divide(new BigDecimal(60), RoundingMode.FLOOR));
            fee = fee.add(tariff.getExtendedBid().multiply(extendedBigPeriod));
        }
        return fee;
    }

}
