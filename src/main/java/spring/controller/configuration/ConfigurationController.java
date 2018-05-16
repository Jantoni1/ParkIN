package spring.controller.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

@RestController
public class ConfigurationController {

    private ConfigurationRepository configurationRepository;

    @Autowired
    public ConfigurationController(ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    @GetMapping("/slots")
    public Map<String, Integer> getNumberOfSlots() {
        Configuration conf = configurationRepository.findByName("capacity");
        return ImmutableMap.of("capacity", conf.getValue());
    }
}
