package spring.controller.configuration;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "CONFIGURATIONS")
public class Configuration {

    @Id @GeneratedValue
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
