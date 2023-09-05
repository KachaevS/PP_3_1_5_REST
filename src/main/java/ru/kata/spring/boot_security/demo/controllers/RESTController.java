package ru.kata.spring.boot_security.demo.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dto.UserDTO;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class RESTController {

    private final UserService userService;
    private final RoleService roleService;
    private final ModelMapper modelMapper;

    @Autowired
    public RESTController(UserService userService, RoleService roleService, ModelMapper modelMapper) {
        this.userService = userService;
        this.roleService = roleService;
        this.modelMapper = modelMapper;
    }


    @GetMapping("/users")
    public List<UserDTO> getAllUsers() {
        return userService.allUsers()
                .stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/users/{id}")
    public UserDTO showUserById(@PathVariable Long id) {
        return convertToUserDTO(userService.getById(id));
    }


    @GetMapping("/current")
    public UserDTO showCurrentUserInfo(Principal principal) {
        return convertToUserDTO(userService.findByUsername(principal.getName()));
    }

    @PostMapping("/users")
    public ResponseEntity<String> createUser(@Valid @RequestBody UserDTO userDTO) {
        if (userService.saveUser(convertToUser(userDTO))) {
            return ResponseEntity.ok().body("Пользователь успешно добавлен");
        } else {
            return ResponseEntity.badRequest().body("Пользователь с таким именем уже существует");
        }

    }

    @PutMapping("/users/{id}")
    public ResponseEntity<String> editUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {

        if (userService.editUser(convertToUser(userDTO), id)) {
            return ResponseEntity.ok().body("Пользователь успешно изменен");
        } else {
            return ResponseEntity.badRequest().body("Пользователь с таким именем уже существует");
        }

    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable Long id) {
        userService.delete(userService.getById(id));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/roles")
    public List<Role> getRoles() {
        return roleService.findAll();
    }


//=================================================================


    private User convertToUser(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

    private UserDTO convertToUserDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }
}
