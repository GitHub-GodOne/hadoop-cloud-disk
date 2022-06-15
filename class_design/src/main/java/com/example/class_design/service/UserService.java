package com.example.class_design.service;

import com.example.class_design.dao.MapperImp.UserMapper;
import com.example.class_design.domain.User;

public interface UserService {
    User queryUserByName(String name);
    public boolean exixtUsername(String username);
    public void insert_user(User user);
}
