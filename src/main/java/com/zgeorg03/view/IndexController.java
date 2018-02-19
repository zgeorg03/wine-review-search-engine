package com.zgeorg03.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller()
public class IndexController {

    /**
    @RequestMapping(value = "/")
    public String getIndexHome(){
        return  "home";
    }
    **/


    @GetMapping("/")
    public RedirectView redirectView() {
        return new RedirectView("/swagger-ui.html");
    }
}
