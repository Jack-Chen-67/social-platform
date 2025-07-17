package com.itcjx.socialplatform.controller;

import com.itcjx.socialplatform.DTO.UserDTO;
import com.itcjx.socialplatform.entity.User;
import com.itcjx.socialplatform.service.IUserService;
import com.itcjx.socialplatform.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    //注册
    @PostMapping("/register")
    public Result<String> register(@RequestBody UserDTO userDTO) {
        return userService.register(userDTO);
    }

    //登录
    @PostMapping("/login")
    public Result<User> Login(@RequestBody UserDTO userDTO) {
        return userService.login(userDTO);
    }
}
