package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.services.UserService;

import java.security.Principal;

@RestController
public class MainController {

    UserService userService;

    @Autowired
    public MainController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public ModelAndView homePage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");
        return modelAndView;
    }


    @GetMapping("/user")
    public ModelAndView modelAndView (Principal principal) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("user");
        User user = userService.findByUsername(principal.getName());
        modelAndView.addObject(user);
        return modelAndView;
    }



//
//    @GetMapping("/authenticated")
//    public String pageForAuthenticatedUser(Principal principal) {
//        User user = userService.findByUsername(principal.getName());
//        return "secured part of web service " + user.getUsername() + ", " + user.getEmail();
//    }
}
