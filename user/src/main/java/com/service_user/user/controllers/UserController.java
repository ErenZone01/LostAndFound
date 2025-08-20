package com.service_user.user.controllers;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.service_user.user.models.User;
import com.service_user.user.models.UserDTO;
import com.service_user.user.services.UserService;

@RestController
@RequestMapping("/api")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/getProfile")
    public UserDTO getProfile(@RequestHeader(value = "X-User-Id", required = true) String userId) {
        return userService.getProfile(userId);
    }

    @GetMapping("/getUserById")
    public UserDTO getUserById(@RequestParam(required = true) String userId) {
        return userService.getUserById(userId);
    }

    @DeleteMapping("/delete")
    public String delete(@RequestHeader(value = "X-User-Id", required = true) String userId) {
        return userService.deleteUserById(userId);
    }

    @PutMapping("/update")
    public String update(@RequestHeader(value = "X-User-Id", required = true) String id, @RequestBody User user) {
        System.out.println("Updating user with User: " + user);
        return userService.updateUser(id, user);
    }

}
