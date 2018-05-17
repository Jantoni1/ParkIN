package spring.controller;

import demo.CarEntry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.ArrayList;
import java.util.List;

@EnableWebMvc
@Controller
public class HelloWorldController {
    private List<CarEntry> db = new ArrayList<>();

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("carData", this.db);
        return "index";
    }

    @ResponseBody @RequestMapping(value = "/PostService", method = RequestMethod.POST)
    public CarEntry PostService(@RequestBody CarEntry car) {
        car.setId(db.size() + 1);
        db.add(car);
//
//        for (CarEntry item : db) {
//            System.out.println(item.getId() + " " + item.getRegistration());
//        }
//        System.out.println();

        return car;
    }
}
