package com.cau.cc.swagger;

import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Api(tags = "Swagger 접근")
public class SwaggerController {
    @GetMapping("/swagger")
    public String greeting() {
        return "redirect:/swagger-ui.html";
    }
}
