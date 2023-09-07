package ru.kata.spring.boot_security.demo.controllers;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping()
    public ModelAndView adminHomePage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin/admin");
        return modelAndView;
    }
}
