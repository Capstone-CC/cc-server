package com.cau.cc.swagger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SwaggerController {
    @RequestMapping("/api/swagger")
    public String greeting() {
        return "redirect:/swagger-ui.html";
    }
}
