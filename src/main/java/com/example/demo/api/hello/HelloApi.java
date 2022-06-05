package com.example.demo.api.hello;

import com.example.demo.api.order.OrderRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/push")
public class HelloApi {

    @GetMapping(path = "/hello")
    public @ResponseBody String order() {
        return "hello23";
    }
}
