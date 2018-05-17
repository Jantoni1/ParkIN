package spring.controller.resgistrations;


import spring.controller.resgistrations.infrastructure.LocalDateTimeConverter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "REGISTRATIONS")
public class Registration {

    @Id @GeneratedValue
    private Long id;

    @Column(name = "REGISTRATION_PLATE")
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

    public Long getTariffId() {
        return tariffId;
    }

    public void setTariffId(Long tariffId) {
        this.tariffId = tariffId;
    }
}
