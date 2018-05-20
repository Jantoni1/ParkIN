package com.parkin.registrations;


import com.parkin.registrations.infrastructure.LocalDateTimeConverter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "REGISTRATIONS")
public class Registration {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "REGISTRATION_PLATE", length = 12)
    private String registrationPlate;

    @Convert(converter = LocalDateTimeConverter.class)
    @Column(name = "ARRIVAL_TIME")
    private LocalDateTime arrival;

    @Convert(converter = LocalDateTimeConverter.class)
    @Column(name = "DEPARTURE_TIME")
    private LocalDateTime departure;

    @Column(name = "TARIFF_ID")
    private Long tariffId;

    public Long getId() {
        return id;
    }

    public String getRegistrationPlate() {
        return registrationPlate;
    }

    public void setRegistrationPlate(String registrationPlate) {
        this.registrationPlate = registrationPlate;
    }

    public LocalDateTime getArrival() {
        return arrival;
    }

    public void setArrival(LocalDateTime arrival) {
        this.arrival = arrival;
    }

    public LocalDateTime getDeparture() {
        return departure;
    }

    public void setDeparture(LocalDateTime departure) {
        this.departure = departure;
    }

    public void setDeparture(String departure) {
        this.departure = LocalDateTime.parse(departure, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public Long getTariffId() {
        return tariffId;
    }

    public void setTariffId(Long tariffId) {
        this.tariffId = tariffId;
    }
}
