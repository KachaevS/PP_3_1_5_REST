package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.services.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;


    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;

    }

    @GetMapping()
    public ModelAndView adminHomePage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin");
        modelAndView.addObject("users", userService.allUsers());
        return modelAndView;
    }

    @GetMapping("/edit/{id}")
    public ModelAndView editPage(@PathVariable("id") Long id) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("editPage");
        modelAndView.addObject("user", userService.getById(id));
        return modelAndView;
    }

    @GetMapping("/delete/{id}")
    public ModelAndView deleteUser(@PathVariable("id") Long id) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/admin");
        userService.delete(userService.getById(id));
        return modelAndView;
    }

    @PostMapping("/edit")
    public ModelAndView editUser(@ModelAttribute("user") User user, @RequestParam(value = "isAdmin", required = false) boolean isAdmin) {
        ModelAndView modelAndView = new ModelAndView();

        if (!userService.editUser(user, isAdmin)) {
            modelAndView.setViewName("editPage");
            modelAndView.addObject("message", "Пользователь с таким именем уже существует");
            return modelAndView;
        }
        userService.editUser(user, isAdmin);
        modelAndView.setViewName("redirect:/admin");
        return modelAndView;
    }



    @GetMapping("/registration")
    public ModelAndView registration() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("registration");
        modelAndView.addObject("newUser", new User());
        return modelAndView;
    }

    @PostMapping("/registration")
    public ModelAndView addUser(@ModelAttribute("newUser") User user, @RequestParam(value = "isAdmin", required = false) boolean isAdmin) {
        ModelAndView modelAndView = new ModelAndView();


        if (!userService.saveUser(user, isAdmin)) {
            modelAndView.setViewName("registration");
            modelAndView.addObject("message", "Пользователь с таким именем уже существует");
            return modelAndView;
        }

        modelAndView.setViewName("redirect:/admin");
        return modelAndView;
    }
}
