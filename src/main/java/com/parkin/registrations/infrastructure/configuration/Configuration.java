package com.parkin.registrations.infrastructure.configuration;

import javax.persistence.*;

@Entity
@Table(name = "CONFIGURATIONS")
public class Configuration {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "VALUE")
    private Integer value;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String basicBid) {
        this.name = basicBid;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
