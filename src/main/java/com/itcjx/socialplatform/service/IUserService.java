package com.itcjx.socialplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itcjx.socialplatform.DTO.UserDTO;
import com.itcjx.socialplatform.entity.User;
import com.itcjx.socialplatform.util.Result;


public interface IUserService extends IService<User> {

    //User selectByUsername(String username);
    Result<String> register(UserDTO userdto);
    Result<User> login(UserDTO userdto);
}
