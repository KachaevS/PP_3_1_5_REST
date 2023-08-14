package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

@RestController
public class MainController {

    private final UserService userService;

    private final RoleService roleService;


    @Autowired
    public MainController(UserService userService, RoleService roleRepository) {
        this.userService = userService;
        this.roleService = roleRepository;
    }

    @GetMapping("/")
    public ModelAndView homePage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");
        return modelAndView;
    }

    @GetMapping("/user")
    public ModelAndView modelAndView(Principal principal) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("user");
        User user = userService.findByUsername(principal.getName());
        modelAndView.addObject(user);
        return modelAndView;
    }

    @GetMapping(value = "/admin")
    public ModelAndView allUsers() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin");
        modelAndView.addObject("users", userService.allUsers());
        return modelAndView;
    }

    @GetMapping(value = "admin/edit/{id}")
    public ModelAndView editPage(@PathVariable("id") Long id) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("editPage");
        modelAndView.addObject("user", userService.getById(id));
        return modelAndView;
    }


    @GetMapping(value = "admin/delete/{id}")
    public ModelAndView deleteUser(@PathVariable("id") Long id) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/admin");
        userService.delete(userService.getById(id));
        return modelAndView;
    }


    @GetMapping("/registration")
    public ModelAndView registration() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("registration");
        modelAndView.addObject("newUser", new User());
        List<Role> roles = roleService.findAll();
        modelAndView.addObject("allRoles", roles);
        return modelAndView;
    }


    @PostMapping(value = "admin/edit")
    public ModelAndView editUser(@ModelAttribute("user") User user, @RequestParam(value = "isAdmin", required = false) boolean isAdmin) {
        ModelAndView modelAndView = new ModelAndView();

        if (!userService.editUser(user)) {
            modelAndView.setViewName("editPage");
            modelAndView.addObject("message", "Пользователь с таким именем уже существует");
            return modelAndView;
        }

        if (isAdmin) {
            user.addRole(roleService.findAll().get(1));
        } else {
            Role adminRole = roleService.findAll().get(1);
            user.getRoles().remove(adminRole);
        }

        userService.editUser(user);
        modelAndView.setViewName("redirect:/admin");
        return modelAndView;
    }

    @PostMapping("/registration")
    public ModelAndView addUser(@ModelAttribute("newUser") User user, @RequestParam(value = "isAdmin", required = false) boolean isAdmin, Principal principal) {
        ModelAndView modelAndView = new ModelAndView();


        if (!userService.saveUser(user, isAdmin)) {
            modelAndView.setViewName("registration");
            modelAndView.addObject("message", "Пользователь с таким именем уже существует");
            return modelAndView;
        }


        if (principal != null) {
            if (userService.findByUsername(principal.getName()).getRoles().stream().anyMatch(role -> Objects.equals(role.getName(), "ROLE_ADMIN"))) {
                modelAndView.setViewName("redirect:/admin");
                return modelAndView;
            }
        }

        modelAndView.addObject("message", "Регистрация прошла успешно. Зайдите на сайт");
        modelAndView.setViewName("home");
        return modelAndView;
    }

}

