package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
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
}
