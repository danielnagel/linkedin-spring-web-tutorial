package linkedin.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/greeting")
public class GreetingRestController {
    
    @Value("${linkedin.greeting}")
    private String greeting;

    @GetMapping
    public String get() {
        return greeting;
    }
    
}
