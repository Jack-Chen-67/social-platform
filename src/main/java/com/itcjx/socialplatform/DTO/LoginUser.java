package com.itcjx.socialplatform.DTO;

import com.itcjx.socialplatform.entity.User;

public class LoginUser extends User {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
