package com.parkin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class HTMLProviderController {

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

    @RequestMapping("/reset")
    public String resetHTML() {
        return "reset";
    }
}
