package spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

//TODO wyjebac i napisac sensowny, ktory robi to co ten

@Controller
public class HelloWorldController {
    @RequestMapping("/")
    public String indexHTML() {
        return "index";
    }

    @RequestMapping("/tariff")
    public String tariffHTML() {
        return "tariff";
    }

    @RequestMapping("/stats")
    public String statsHTML() {
        return "stats";
    }
}
