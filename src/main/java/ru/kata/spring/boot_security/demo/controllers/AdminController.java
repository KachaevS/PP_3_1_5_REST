package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;

import javax.validation.Valid;
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
    public ModelAndView editUser(@ModelAttribute("user") User user) {
        ModelAndView modelAndView = new ModelAndView();

//        if (!userService.editUser(user)) {
//            modelAndView.setViewName("admin/editUser");
//            modelAndView.addObject("message", "Пользователь с таким именем уже существует");
//            return modelAndView;
//        }
        System.out.println("HERE COEMGSGSEGSEGSEGSEG");
        userService.editUser(user);
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
    public ModelAndView addUser(@ModelAttribute("newUser") User user)
//                                @RequestParam(value = "isAdmin", required = false) boolean isAdmin)
    {
        ModelAndView modelAndView = new ModelAndView();


        if (!userService.saveUser(user)) {
            modelAndView.setViewName("newUser");
            modelAndView.addObject("message", "Пользователь с таким именем уже существует");
            return modelAndView;
        }

        modelAndView.setViewName("redirect:/admin");
        return modelAndView;
    }

    @GetMapping("/userForm")
    public String showEditDeleteUserPage(@RequestParam("userId") Long id, Model model) {
        model.addAttribute("user", userService.getById(id));
        model.addAttribute("allRoles", getRoles());
        System.out.println("ASS=============================================");
        return "admin/editUser";
    }

    @PostMapping("/update")
    public String saveUser(@RequestParam(value = "userId", defaultValue = "0") Long userId,
//                           @RequestParam(value = "currentEmail", defaultValue = "") String currentEmail,
                           @ModelAttribute("user") User user,
                           Model model) {

        user.setId((userId));
        userService.saveUser(user);
        return "redirect:/admin";
    }


    private List<Role> getRoles() {
        if (roles == null) {
            roles = roleService.findAll();
        }
        return roles;
    }
}
