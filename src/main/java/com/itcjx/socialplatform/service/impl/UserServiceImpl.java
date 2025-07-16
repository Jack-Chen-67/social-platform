package com.itcjx.socialplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itcjx.socialplatform.DTO.LoginUser;
import com.itcjx.socialplatform.DTO.UserDTO;
import com.itcjx.socialplatform.controller.UserController;
import com.itcjx.socialplatform.entity.User;
import com.itcjx.socialplatform.mapper.UserMapper;
import com.itcjx.socialplatform.service.IUserService;
import com.itcjx.socialplatform.util.JwtTokenUtil;
import com.itcjx.socialplatform.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private final UserMapper userMapper;

    private final JwtTokenUtil jwtTokenUtil;

    public UserServiceImpl(UserMapper userMapper,JwtTokenUtil jwtTokenUtil) {
        this.userMapper = userMapper;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    // 注册
    @Override
    public Result<String> register(UserDTO userDTO) {
        try {
            // 参数检查
            if (StringUtils.isBlank(userDTO.getUsername()) || StringUtils.isBlank(userDTO.getPassword())) {
                return Result.error(401,"用户名和密码不能为空");
            }

            // 构建用户对象
            User user = new User();
            user.setUsername(userDTO.getUsername());
            user.setPassword(DigestUtils.md5DigestAsHex(userDTO.getPassword().getBytes()));

            userMapper.insert(user);
            return Result.success("注册成功");

        } catch (Exception e) {
            log.error("注册失败", e);
            return Result.error(1003,"注册失败: " + e.getMessage());
        }
    }

    //登录
    @Override
    public Result<User> login(UserDTO userDTO) {
        // 1. 基础参数校验
        if (StringUtils.isBlank(userDTO.getUsername()) || StringUtils.isBlank(userDTO.getPassword())) {
            return Result.error(Result.ErrorCode.PARAM_ERROR, "用户名和密码不能为空");
        }

        // 2. 查询用户（保留基础日志）
        User user = lambdaQuery()
                .eq(User::getUsername, userDTO.getUsername())
                .one();
        log.debug("登录用户查询: {}", userDTO.getUsername());

        if (user == null) {
            return Result.error(Result.ErrorCode.NOT_FOUND, "用户不存在");
        }

        // 3. 密码验证
        String encryptedInput = DigestUtils.md5DigestAsHex(userDTO.getPassword().getBytes(StandardCharsets.UTF_8));
        if (!user.getPassword().equals(encryptedInput)) {
            return Result.error(Result.ErrorCode.UNAUTHORIZED, "密码错误");
        }

        // 4. 生成JWT token
        String token = jwtTokenUtil.generateToken(user.getUsername(), user.getId()); // 使用JwtTokenUtil生成token


        // 5. 构造返回对象
        LoginUser loginUser = new LoginUser();
        BeanUtils.copyProperties(user, loginUser); // 将 User 属性拷贝到 LoginUser
        loginUser.setToken(token);

        return Result.success(loginUser);
    }
}
