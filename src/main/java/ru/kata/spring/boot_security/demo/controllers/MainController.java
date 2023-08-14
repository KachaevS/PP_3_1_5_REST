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

@RestController
public class MainController {

    UserService userService;

    RoleService roleService;


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

    @PostMapping(value = "admin/edit")
    public ModelAndView editUser(@ModelAttribute("user") User user) {
        ModelAndView modelAndView = new ModelAndView();
        userService.editUser(user);
        modelAndView.setViewName("redirect:/admin");
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


    @PostMapping("/registration")
    public ModelAndView addUser(@ModelAttribute("newUser") User user) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/");
        userService.saveUser(user);
        return modelAndView;
    }
}

