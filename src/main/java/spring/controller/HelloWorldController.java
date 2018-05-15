package spring.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    @RequestMapping("/")
    public String helloWorld()
    {
        return "Hello Sollers! ;) Pipeline is working! Testing code coverage plugin!";
    }
    @RequestMapping("/test")
    public String test()
    {
        return "Testing mapping! - important";
    }

    @RequestMapping("/vcsTest")
    public String vcsTest() {return "vcs testing - nothing to watch here!";}
}
