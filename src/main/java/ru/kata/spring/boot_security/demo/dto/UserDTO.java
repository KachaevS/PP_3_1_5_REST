package ru.kata.spring.boot_security.demo.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ru.kata.spring.boot_security.demo.model.Role;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Collection;

public class UserDTO {


    private long id;

    @Size(min = 3, max = 20, message = "Name should be between 3 and 20 characters")
    private String username;
    @Email(message = "Email is not valid")
    @NotEmpty(message = "Email should not be empty")
    private String email;
    @Column(name = "password")
    private String password;
    @NotEmpty(message = "At least one role must be selected")
    @JsonIgnoreProperties("users")
    private Collection<Role> roles;


    public UserDTO() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }
}
