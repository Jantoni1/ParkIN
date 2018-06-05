package com.parkin.registrations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.google.common.collect.ImmutableMap;
import com.parkin.registrations.infrastructure.configuration.Configuration;
import com.parkin.registrations.infrastructure.configuration.ConfigurationRepository;
import com.parkin.tariffs.Tariff;
import com.parkin.tariffs.TariffCrudRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        if(registrationRepository.countByDepartureIsNull()
            .equals(configurationRepository.findByName("capacity").orElse(new Configuration()).getValue())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if(registrationRepository.findTopByRegistrationPlateAndDepartureIsNullOrderByArrivalDesc(pRegistration.getRegistrationPlate())
                .isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if(pRegistration.getRegistrationPlate().length() > 12) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        registrationRepository.save(createRegistration(pRegistration.getRegistrationPlate()));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/checkout")
    public ResponseEntity<ImmutableMap<String, String>> carFeeLookup(@RequestBody Registration pRegistration) {
        Optional<Registration> registrationOptional = registrationRepository
                .findTopByRegistrationPlateAndDepartureIsNullOrderByArrivalDesc(pRegistration.getRegistrationPlate());
        if(registrationOptional.isPresent()) {
            Registration registration = registrationOptional.get();
            Optional<Tariff> tariff = tariffCrudRepository.findOne(registration.getTariffId());
            if(tariff.isPresent()) {
                LocalDateTime now = LocalDateTime.now();
                BigDecimal fee = calculatePrice(registration, tariff.get(), now);
                return new ResponseEntity<>(ImmutableMap.of("fee", fee.toString()
                        , "registrationPlate", registration.getRegistrationPlate()
                        , "arrival", registration.getArrival().toString()
                        , "departure", now.toString()), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/unregister")
    public ResponseEntity<Void> unregisterCar(@RequestBody Registration pRegistration) {
        Optional<Registration> registrationOptional = registrationRepository
                .findTopByRegistrationPlateOrderByArrivalDesc(pRegistration.getRegistrationPlate());
        if(registrationOptional.isPresent()) {
            Registration registration = registrationOptional.get();
            registration.setDeparture(pRegistration.getDeparture());
            registrationRepository.save(registration);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/stats-day")
    public ImmutableMap<String, String> getDailyStatistics() {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Warsaw"));
        LocalDateTime time = LocalDate.now().atStartOfDay();
        List<Registration> arrivals  = registrationRepository.findAllByArrivalGreaterThan(time);
        List<Registration> departures = registrationRepository.findAllByDepartureGreaterThan(time);
        BigDecimal earnings = new BigDecimal(0);
        for(Registration registration: departures) {
            Optional<Tariff> tariffOptional = tariffCrudRepository.findOne(registration.getTariffId());
            Tariff tariff = tariffOptional.orElse(new Tariff());
            earnings = earnings.add(calculatePrice(registration, tariff, registration.getDeparture()));
        }

        List<Registration> forecasted = registrationRepository.findAllByArrivalGreaterThanAndDepartureIsNull(time);
        BigDecimal forecast = new BigDecimal(0);
        for(Registration registration: forecasted) {
            Optional<Tariff> tariffOptional = tariffCrudRepository.findOne(registration.getTariffId());
            Tariff tariff = tariffOptional.orElse(new Tariff());
            forecast = forecast.add(calculatePrice(registration, tariff, LocalDateTime.now()));
        }

        return ImmutableMap.of("earnings", earnings.toString()
                , "arrivals", Integer.toString(arrivals.size())
                , "departures", Integer.toString(departures.size())
                , "forecast", forecast.toString());
    }

    @GetMapping("/stats-month")
    public List<ImmutableMap<String, String>> getMonthlyStatistics() {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Warsaw"));

        LocalDate start = LocalDate.now().withDayOfMonth(1);
        LocalDate end = LocalDate.now();

        return Stream.iterate(start, date -> date.plusDays(1))
            .limit(ChronoUnit.DAYS.between(start, end) + 1)
            .map(date -> ImmutableMap.of(
                "date", date.toString(),
                "earnings", registrationRepository
                    .findAllByDepartureBetween(date.atStartOfDay(), date.atTime(LocalTime.MAX))
                    .stream()
                    .map(registration -> calculatePrice(
                        registration,
                        tariffCrudRepository.findOne(registration.getTariffId()).orElse(new Tariff()),
                        registration.getDeparture()
                    ))
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .toString()
            ))
            .collect(Collectors.toList());
    }

    @GetMapping("/reset-all")
    public ResponseEntity<Void> reset() {
        registrationRepository.deleteAllByDepartureIsNull();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Registration createRegistration(String registrationPlate) {
        Registration registration = new Registration();
        registration.setRegistrationPlate(registrationPlate);
        registration.setArrival(LocalDateTime.now());
        registration.setDeparture((LocalDateTime)null);
        registration.setTariffId(tariffCrudRepository.findTopByOrderByIdDesc().getId());
        return registration;
    }

    private BigDecimal calculatePrice(Registration registration, Tariff tariff, LocalDateTime now) {
        long hours = registration.getArrival().until(now, ChronoUnit.HOURS);
        long minutes = registration.getArrival().until(now, ChronoUnit.MINUTES) % 60;
        long seconds = registration.getArrival().until(now, ChronoUnit.SECONDS) % 60;
        BigDecimal hoursBigDecimal = new BigDecimal(hours);
        BigDecimal minutesBigDecimal = new BigDecimal(minutes);
        BigDecimal secondsBigDecimal = new BigDecimal(seconds);
        BigDecimal fee;
        if(hours < tariff.getBasicPeriod()) {
            fee = tariff.getBasicBid().multiply(hoursBigDecimal);
            fee = fee.add(tariff.getBasicBid().multiply(minutesBigDecimal).divide(new BigDecimal(60), RoundingMode.FLOOR));
            fee = fee.add(tariff.getBasicBid().multiply(secondsBigDecimal).divide(new BigDecimal(3600), RoundingMode.FLOOR));
        }
        else {
            BigDecimal extendedBigPeriod = hoursBigDecimal.subtract(new BigDecimal(tariff.getBasicPeriod()));
            fee = tariff.getBasicBid().multiply(new BigDecimal(tariff.getBasicPeriod()));
            fee = fee.add(tariff.getExtendedBid().multiply(extendedBigPeriod));
            fee = fee.add(tariff.getExtendedBid().multiply(minutesBigDecimal).divide(new BigDecimal(60), RoundingMode.FLOOR));
            fee = fee.add(tariff.getExtendedBid().multiply(secondsBigDecimal).divide(new BigDecimal(3600), RoundingMode.FLOOR));
        }

        return fee.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

}
