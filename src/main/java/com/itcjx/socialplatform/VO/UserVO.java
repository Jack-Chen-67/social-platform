package com.itcjx.socialplatform.VO;

import lombok.Data;

//@Data
public class UserVO {
    private String username;
    private String nikename;
    private String avatar;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNikename() {
        return nikename;
    }

    public void setNikename(String nikename) {
        this.nikename = nikename;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "UserVO{" +
                "username='" + username + '\'' +
                ", nikename='" + nikename + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
