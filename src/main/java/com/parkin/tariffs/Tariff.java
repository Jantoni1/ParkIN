package com.parkin.tariffs;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "TARIFFS")
public class Tariff {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "BASIC_BID", precision = 8, scale = 2)
    private BigDecimal basicBid;

    @Column(name = "BASIC_PERIOD")
    private Double basicPeriod;

    @Column(name = "EXTENDED_BID", precision = 8, scale = 2)
    private BigDecimal extendedBid;

    public Long getId() {
        return id;
    }

    public BigDecimal getBasicBid() {
        return basicBid;
    }

    public void setBasicBid(BigDecimal basicBid) {
        this.basicBid = basicBid;
    }

    public Double getBasicPeriod() {
        return basicPeriod;
    }

    public void setBasicPeriod(Double basicPeriod) {
        this.basicPeriod = basicPeriod;
    }

    public BigDecimal getExtendedBid() {
        return extendedBid;
    }

    public void setExtendedBid(BigDecimal extendedBid) {
        this.extendedBid = extendedBid;
    }

}
