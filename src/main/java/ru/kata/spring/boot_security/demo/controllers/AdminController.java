package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;
    private List<Role> roles;


    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;

    }

    @GetMapping()
    public ModelAndView adminHomePage(Principal principal) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin/admin");
        User user = userService.findByUsername(principal.getName());
        modelAndView.addObject("user", user);
        modelAndView.addObject("users", userService.allUsers());
        modelAndView.addObject("newUser", new User());
        modelAndView.addObject("allRoles", getRoles());
        return modelAndView;
    }


    private List<Role> getRoles() {
        if (roles == null) {
            roles = roleService.findAll();
        }
        return roles;
    }
}
