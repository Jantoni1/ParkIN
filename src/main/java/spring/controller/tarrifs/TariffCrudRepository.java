package spring.controller.tarrifs;

import org.springframework.data.rest.core.annotation.RestResource;

public interface TariffCrudRepository extends TariffRepository {

    @RestResource
    Tariff save(Tariff tariff);
}
