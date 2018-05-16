package spring.controller.tarrifs;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

@RestController
public class TariffController {

    private TariffRepository tariffRepository;

    @Autowired
    public TariffController(TariffRepository tariffRepository) {
        this.tariffRepository = tariffRepository;
    }


    @GetMapping("/tariffs")
    public ResponseEntity<Tariff> getLatestTariff() {
        Tariff tariff = tariffRepository.findTopByOrderByIdDesc();
        return new ResponseEntity<>(tariff, HttpStatus.OK);
    }

}
