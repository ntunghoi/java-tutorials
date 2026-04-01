package com.ntunghoi.tutorials.oauth2.controller;

import com.ntunghoi.tutorials.oauth2.dto.UserDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    @GetMapping
    public UserDto getUser() {
        return new UserDto("UserId", "username");
    }
}
