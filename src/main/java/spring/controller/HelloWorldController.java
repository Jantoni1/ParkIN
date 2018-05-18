package spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class HelloWorldController {
    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/tariff")
    public String tariff() {
        return "tariff";
    }

    @RequestMapping("/stats")
    public String stats() {
        return "stats";
    }
}
